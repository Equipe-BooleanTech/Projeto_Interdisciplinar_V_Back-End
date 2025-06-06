package com.fatec.backend.service.vehicle;

import com.fatec.backend.DTO.vehicle.DateRangeDTO;
import com.fatec.backend.DTO.vehicle.FuelRefillDTO;
import com.fatec.backend.DTO.vehicle.MaintenanceDTO;
import com.fatec.backend.DTO.vehicle.TimeSummaryDTO;
import com.fatec.backend.mapper.maintenance.MaintenanceMapper;
import com.fatec.backend.mapper.vehicle.FuelRefillMapper;
import com.fatec.backend.model.vehicle.FuelRefill;
import com.fatec.backend.model.vehicle.GasStation;
import com.fatec.backend.model.vehicle.Maintenance;
import com.fatec.backend.model.vehicle.Vehicle;
import com.fatec.backend.repository.FuelRefillRepository;
import com.fatec.backend.repository.GasStationRepository;
import com.fatec.backend.repository.VehicleRespository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;


import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FuelRefillService {

    private final FuelRefillRepository fuelRefillRepository;
    private final VehicleRespository vehicleRepository;
    private final GasStationRepository gasStationRepository;

    public UUID createFuelRefill(FuelRefillDTO fuelRefillDTO, UUID vehicleId, UUID stationId) {
        Vehicle vehicle = vehicleRepository.findById(vehicleId)
                .orElseThrow(() -> new IllegalArgumentException("Veículo não encontrado com ID: " + vehicleId));
        GasStation station = gasStationRepository.findById(stationId)
                .orElseThrow(() -> new IllegalArgumentException("Posto de gasolina não encontrado" + stationId));

        FuelRefill fuelRefill = FuelRefill.builder()
                .vehicle(vehicle)
                .station(station)
                .liters(fuelRefillDTO.liters())
                .pricePerLiter(fuelRefillDTO.pricePerLiter())
                .totalCost(fuelRefillDTO.pricePerLiter() * fuelRefillDTO.liters())
                .kmAtRefill(fuelRefillDTO.kmAtRefill())
                .isCompleteTank(fuelRefillDTO.isCompleteTank())
                .fuelType(fuelRefillDTO.fuelType())
                .refillDate(LocalDateTime.now())
                .build();

        calculateAndSetKmPerLiter(fuelRefill, vehicle);

        if (fuelRefill.getKmAtRefill() > vehicle.getOdometer()) {
            vehicle.setOdometer(fuelRefill.getKmAtRefill());
        }
        vehicleRepository.save(vehicle);
        return fuelRefillRepository.save(fuelRefill).getId();
    }

    public void updateFuelRefill(UUID fuelRefillId, FuelRefillDTO fuelRefillDTO, UUID vehicleId) {
        FuelRefill existingFuelRefill = fuelRefillRepository.findById(fuelRefillId)
                .orElseThrow(() -> new IllegalArgumentException("Registro de abastecimento não encontrado com ID: " + fuelRefillId));

        if (!existingFuelRefill.getVehicle().getId().equals(vehicleId)) {
            throw new SecurityException("Registro de abastecimento não pertence ao veículo especificado na URL.");
        }
        if (fuelRefillDTO.vehicleId() != null && !fuelRefillDTO.vehicleId().equals(existingFuelRefill.getVehicle().getId())) {
            throw new IllegalArgumentException("Não é permitido alterar o veículo associado a um registro de abastecimento existente.");
        }
        GasStation station = gasStationRepository.findById(fuelRefillDTO.stationId()).orElseThrow(() -> new IllegalArgumentException("Posto de gasolina nao encontrado"));

        Vehicle vehicle = existingFuelRefill.getVehicle();
        existingFuelRefill.setStation(station);
        existingFuelRefill.setLiters(fuelRefillDTO.liters());
        existingFuelRefill.setPricePerLiter(fuelRefillDTO.pricePerLiter());
        existingFuelRefill.setTotalCost(fuelRefillDTO.liters() * fuelRefillDTO.pricePerLiter());
        existingFuelRefill.setKmAtRefill(fuelRefillDTO.kmAtRefill());
        existingFuelRefill.setIsCompleteTank(fuelRefillDTO.isCompleteTank());
        existingFuelRefill.setFuelType(fuelRefillDTO.fuelType());
        existingFuelRefill.setRefillDate(fuelRefillDTO.refillDate());

        calculateAndSetKmPerLiter(existingFuelRefill, vehicle);

        if (existingFuelRefill.getKmAtRefill() > vehicle.getOdometer()) {
            vehicle.setOdometer(existingFuelRefill.getKmAtRefill());
        }
        vehicleRepository.save(vehicle);
        fuelRefillRepository.save(existingFuelRefill);
    }

    public void deleteFuelRefill(UUID fuelRefillId, UUID vehicleId) {
        FuelRefill fuelRefill = fuelRefillRepository.findById(fuelRefillId)
                .orElseThrow(() -> new IllegalArgumentException("Registro de abastecimento não encontrado com ID: " + fuelRefillId));
        if (!fuelRefill.getVehicle().getId().equals(vehicleId)) {
            throw new SecurityException("Não autorizado a deletar este registro de abastecimento ou ele não pertence ao veículo especificado.");
        }

        Vehicle vehicle = fuelRefill.getVehicle();
        vehicleRepository.save(vehicle);

        fuelRefillRepository.deleteById(fuelRefillId);

    }



    public FuelRefill findById(UUID id) {
        return fuelRefillRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Vehicle not found"));
    }
    public Page<FuelRefillDTO> listRefills(UUID vehicleId, PageRequest pageRequest) {
        return fuelRefillRepository.findAllByVehicleId(vehicleId, pageRequest)
                .map(FuelRefillMapper.INSTANCE::toFuelRefillDTO);
    }

    public TimeSummaryDTO listFuelRefillByDateRangeAndVehicle(UUID vehicleId, DateRangeDTO dateRangeDTO) {
        List<FuelRefill> data = fuelRefillRepository.findAllByVehicleIdAndRefillDateBetween(
                vehicleId, dateRangeDTO.startDate(), dateRangeDTO.endDate()
        );

        List<FuelRefillDTO> refills = data.stream()
                .map(FuelRefillMapper.INSTANCE::toFuelRefillDTO)
                .toList();

        return new TimeSummaryDTO(Collections.singletonList(refills), refills.size());
    }

    private void calculateAndSetKmPerLiter(FuelRefill currentRefill, Vehicle vehicle) {
        Optional<FuelRefill> previousRefillOpt = fuelRefillRepository.findTopByVehicleOrderByRefillDateDesc(vehicle);

        if (previousRefillOpt.isPresent()) {
            FuelRefill previousRefill = previousRefillOpt.get();
            double kmDriven = currentRefill.getKmAtRefill() - previousRefill.getKmAtRefill();
            if (kmDriven > 0) {
                double kmPerLiter = kmDriven / currentRefill.getLiters();
                vehicle.setFuelConsumption(kmPerLiter);
            }
        }
    }
}


