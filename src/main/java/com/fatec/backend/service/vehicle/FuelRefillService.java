package com.fatec.backend.service.vehicle;

import com.fatec.backend.DTO.vehicle.FuelRefillDTO;
import com.fatec.backend.DTO.vehicle.FuelRefillSummaryDTO;
import com.fatec.backend.mapper.vehicle.FuelRefillMapper;
import com.fatec.backend.model.vehicle.FuelRefill;
import com.fatec.backend.model.vehicle.Vehicle;
import com.fatec.backend.repository.FuelRefillRepository;
import com.fatec.backend.repository.VehicleRespository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FuelRefillService {

    private final FuelRefillRepository fuelRefillRepository;
    private final VehicleRespository vehicleRepository;
    private final FuelRefillMapper fuelRefillMapper;

    @Transactional
    public FuelRefillDTO createFuelRefill(FuelRefillDTO fuelRefillDTO, UUID vehicleId) {
        Vehicle vehicle = vehicleRepository.findById(vehicleId)
                .orElseThrow(() -> new IllegalArgumentException("Veículo não encontrado com ID: " + vehicleId));

        // Usando o padrão Builder
        FuelRefill fuelRefill = FuelRefill.builder()
                .refillDate(fuelRefillDTO.refillDate() != null ? fuelRefillDTO.refillDate() : LocalDateTime.now()) // Default to now if null
                .odometer(fuelRefillDTO.odometer())
                .liters(fuelRefillDTO.liters())
                .totalCost(fuelRefillDTO.totalCost())
                .gasStationName(fuelRefillDTO.gasStationName())
                .fuelType(fuelRefillDTO.fuelType())
                .isCompleteTank(fuelRefillDTO.isCompleteTank())
                .vehicle(vehicle)
                .build();

        // Calcular KM/L se possível
        calculateAndSetKmPerLiter(fuelRefill, vehicle);

        // Atualizar odômetro do veículo
        if (fuelRefill.getOdometer() != null && (vehicle.getKm() == null || fuelRefill.getOdometer() > vehicle.getKm())) {
            vehicle.setKm(fuelRefill.getOdometer());
        }

        // Atualizar métricas agregadas do veículo
        updateVehicleAggregatedMetrics(vehicle, fuelRefill, null); // Pass null for oldRefill on creation

        vehicleRepository.save(vehicle); // Salva veículo com odômetro e métricas atualizadas
        FuelRefill savedFuelRefill = fuelRefillRepository.save(fuelRefill);

        return fuelRefillMapper.toDTO(savedFuelRefill);
    }

    @Transactional(readOnly = true)
    public Page<FuelRefillDTO> getVehicleFuelRefills(UUID vehicleId, Pageable pageable) {
        if (!vehicleRepository.existsById(vehicleId)) {
            throw new IllegalArgumentException("Veículo não encontrado com ID: " + vehicleId);
        }
        return fuelRefillRepository.findByVehicleUuidOrderByRefillDateDesc(vehicleId, pageable).map(fuelRefillMapper::toDTO);
    }

    @Transactional(readOnly = true)
    public FuelRefillDTO getFuelRefillById(UUID fuelRefillId, UUID vehicleId) {
        FuelRefill fuelRefill = fuelRefillRepository.findById(fuelRefillId)
                .orElseThrow(() -> new IllegalArgumentException("Registro de abastecimento não encontrado com ID: " + fuelRefillId));
        if (!fuelRefill.getVehicle().getUuid().equals(vehicleId)) {
            throw new SecurityException("Registro de abastecimento não pertence ao veículo especificado.");
        }
        return fuelRefillMapper.toDTO(fuelRefill);
    }

    @Transactional
    public FuelRefillDTO updateFuelRefill(UUID fuelRefillId, FuelRefillDTO fuelRefillDTO, UUID vehicleIdFromPath) {
        FuelRefill existingFuelRefill = fuelRefillRepository.findById(fuelRefillId)
                .orElseThrow(() -> new IllegalArgumentException("Registro de abastecimento não encontrado com ID: " + fuelRefillId));

        if (!existingFuelRefill.getVehicle().getUuid().equals(vehicleIdFromPath)) {
            throw new SecurityException("Registro de abastecimento não pertence ao veículo especificado na URL.");
        }
        if (fuelRefillDTO.vehicleId() != null && !fuelRefillDTO.vehicleId().equals(existingFuelRefill.getVehicle().getUuid())) {
            throw new IllegalArgumentException("Não é permitido alterar o veículo associado a um registro de abastecimento existente.");
        }

        // Guarda o estado antigo para recalcular métricas do veículo
        FuelRefill oldRefillState = cloneFuelRefill(existingFuelRefill);

        // Atualiza os campos
        existingFuelRefill.setRefillDate(fuelRefillDTO.refillDate());
        existingFuelRefill.setOdometer(fuelRefillDTO.odometer());
        existingFuelRefill.setLiters(fuelRefillDTO.liters());
        existingFuelRefill.setTotalCost(fuelRefillDTO.totalCost());
        existingFuelRefill.setGasStationName(fuelRefillDTO.gasStationName());
        existingFuelRefill.setFuelType(fuelRefillDTO.fuelType());
        existingFuelRefill.setCompleteTank(fuelRefillDTO.isCompleteTank());

        Vehicle vehicle = existingFuelRefill.getVehicle();

        // Recalcular KM/L
        calculateAndSetKmPerLiter(existingFuelRefill, vehicle);

        // Atualizar odômetro do veículo
        if (existingFuelRefill.getOdometer() != null && (vehicle.getKm() == null || existingFuelRefill.getOdometer() > vehicle.getKm())) {
            vehicle.setKm(existingFuelRefill.getOdometer());
        }

        // Atualizar métricas agregadas do veículo (considerando o estado antigo)
        updateVehicleAggregatedMetrics(vehicle, existingFuelRefill, oldRefillState);

        vehicleRepository.save(vehicle);
        FuelRefill updatedFuelRefill = fuelRefillRepository.save(existingFuelRefill);

        // TODO: Considerar recalcular KM/L dos abastecimentos subsequentes se este foi alterado?
        // Por simplicidade, não faremos isso agora, mas pode ser necessário para precisão total.

        return fuelRefillMapper.toDTO(updatedFuelRefill);
    }

    @Transactional
    public void deleteFuelRefill(UUID fuelRefillId, UUID vehicleId) {
        FuelRefill fuelRefill = fuelRefillRepository.findById(fuelRefillId)
                .orElseThrow(() -> new IllegalArgumentException("Registro de abastecimento não encontrado com ID: " + fuelRefillId));
        if (!fuelRefill.getVehicle().getUuid().equals(vehicleId)) {
            throw new SecurityException("Não autorizado a deletar este registro de abastecimento ou ele não pertence ao veículo especificado.");
        }

        Vehicle vehicle = fuelRefill.getVehicle();
        // Reverte o impacto deste abastecimento nas métricas agregadas
        updateVehicleAggregatedMetrics(vehicle, null, fuelRefill); // Pass null for newRefill on deletion
        vehicleRepository.save(vehicle);

        fuelRefillRepository.deleteById(fuelRefillId);

        // TODO: Considerar recalcular KM/L dos abastecimentos subsequentes?
    }

    // --- Métodos Auxiliares ---

    private void calculateAndSetKmPerLiter(FuelRefill currentRefill, Vehicle vehicle) {
        // Encontra o abastecimento anterior (com odômetro menor e mais recente antes deste)
        Optional<FuelRefill> previousRefillOpt = fuelRefillRepository
                .findTopByVehicleAndOdometerLessThanOrderByOdometerDesc(vehicle, currentRefill.getOdometer());

        if (previousRefillOpt.isPresent()) {
            FuelRefill previousRefill = previousRefillOpt.get();
            if (currentRefill.getOdometer() != null && previousRefill.getOdometer() != null && currentRefill.getLiters() != null && currentRefill.getLiters() > 0) {
                double kmDriven = currentRefill.getOdometer() - previousRefill.getOdometer();
                if (kmDriven > 0) {
                    double kmPerLiter = kmDriven / currentRefill.getLiters();
                    currentRefill.setKmPerLiter(kmPerLiter);
                } else {
                    currentRefill.setKmPerLiter(null); // Não é possível calcular
                }
            } else {
                currentRefill.setKmPerLiter(null); // Dados insuficientes
            }
        } else {
            currentRefill.setKmPerLiter(null); // Primeiro abastecimento ou sem anterior válido
        }
    }

    private void updateVehicleAggregatedMetrics(Vehicle vehicle, FuelRefill newRefill, FuelRefill oldRefill) {
        double litersChange = 0;
        double kmChange = 0;
        Double lastKmPerLiter = null;

        // Subtrai o impacto do estado antigo (se for update ou delete)
        if (oldRefill != null) {
            litersChange -= (oldRefill.getLiters() != null ? oldRefill.getLiters() : 0);
            // Km rodados são calculados entre abastecimentos, não somados diretamente aqui
        }

        // Adiciona o impacto do novo estado (se for create ou update)
        if (newRefill != null) {
            litersChange += (newRefill.getLiters() != null ? newRefill.getLiters() : 0);
            lastKmPerLiter = newRefill.getKmPerLiter(); // Pega o último calculado
            // Km rodados são calculados entre abastecimentos
        }

        // Atualiza totais no veículo
        double currentTotalLiters = vehicle.getTotalLitersConsumed() != null ? vehicle.getTotalLitersConsumed() : 0;
        vehicle.setTotalLitersConsumed(currentTotalLiters + litersChange);

        // Recalcula a média de KM/L e o total de KM rodados baseado em TODOS os abastecimentos
        // Isso é mais preciso do que tentar ajustar incrementalmente
        List<FuelRefill> allRefills = fuelRefillRepository.findByVehicleOrderByOdometerAsc(vehicle);
        double totalKmDriven = 0;
        double totalLitersForAvg = 0;
        double weightedSumKmPerLiter = 0;

        if (allRefills.size() > 1) {
            for (int i = 1; i < allRefills.size(); i++) {
                FuelRefill current = allRefills.get(i);
                FuelRefill previous = allRefills.get(i - 1);
                if (current.getOdometer() != null && previous.getOdometer() != null && current.getLiters() != null && current.getLiters() > 0) {
                    double kmSegment = current.getOdometer() - previous.getOdometer();
                    if (kmSegment > 0) {
                        totalKmDriven += kmSegment;
                        totalLitersForAvg += current.getLiters();
                        if (current.getKmPerLiter() != null) {
                            weightedSumKmPerLiter += current.getKmPerLiter() * current.getLiters();
                        }
                    }
                }
            }
        }

        vehicle.setTotalKmDriven(totalKmDriven);
        vehicle.setAverageKmPerLiter(totalLitersForAvg > 0 ? (weightedSumKmPerLiter / totalLitersForAvg) : null);
        // Define o último KM/L como o do abastecimento mais recente (ou o atualizado/criado)
        if (newRefill != null) {
            vehicle.setLastKmPerLiter(newRefill.getKmPerLiter());
        } else if (!allRefills.isEmpty()) {
            // Se deletou, pega o do penúltimo (agora último)
            FuelRefill last = allRefills.get(allRefills.size() - 1);
            calculateAndSetKmPerLiter(last, vehicle); // Recalcula por segurança
            vehicle.setLastKmPerLiter(last.getKmPerLiter());
        } else {
            vehicle.setLastKmPerLiter(null);
        }
    }

    // Helper para clonar o estado antes da atualização
    private FuelRefill cloneFuelRefill(FuelRefill original) {
        return FuelRefill.builder()
                .id(original.getId())
                .refillDate(original.getRefillDate())
                .odometer(original.getOdometer())
                .liters(original.getLiters())
                .totalCost(original.getTotalCost())
                .gasStationName(original.getGasStationName())
                .fuelType(original.getFuelType())
                .isCompleteTank(original.isCompleteTank())
                .kmPerLiter(original.getKmPerLiter())
                .vehicle(original.getVehicle()) // Shallow copy of vehicle is fine here
                .build();
    }

    // Método para obter resumo (exemplo, pode ser movido para DashboardService)
    @Transactional(readOnly = true)
    public FuelRefillSummaryDTO getFuelRefillSummary(UUID vehicleId) {
        Vehicle vehicle = vehicleRepository.findById(vehicleId)
                .orElseThrow(() -> new IllegalArgumentException("Veículo não encontrado com ID: " + vehicleId));

        List<FuelRefill> refills = fuelRefillRepository.findByVehicleOrderByRefillDateDesc(vehicle);

        double totalCost = refills.stream().mapToDouble(FuelRefill::getTotalCost).sum();
        double totalLiters = refills.stream().filter(r -> r.getLiters() != null).mapToDouble(FuelRefill::getLiters).sum();
        Optional<FuelRefill> lastRefill = refills.stream().findFirst(); // Já está ordenado DESC

        return new FuelRefillSummaryDTO(
                vehicleId,
                totalCost,
                totalLiters,
                vehicle.getAverageKmPerLiter(),
                lastRefill.map(FuelRefill::getRefillDate).orElse(null),
                lastRefill.map(FuelRefill::getKmPerLiter).orElse(null)
        );
    }
}
