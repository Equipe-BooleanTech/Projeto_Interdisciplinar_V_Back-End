package com.fatec.backend.service.vehicle;
import com.fatec.backend.DTO.vehicle.FuelRefillDTO;
import com.fatec.backend.mapper.vehicle.FuelRefillMapper;
import com.fatec.backend.model.vehicle.FuelRefill;
import com.fatec.backend.model.vehicle.GasStation;
import com.fatec.backend.model.vehicle.Vehicle;
import com.fatec.backend.repository.FuelRefillRepository;
import com.fatec.backend.repository.GasStationRepository;
import com.fatec.backend.repository.VehicleRespository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FuelRefillService {

    private final VehicleRespository vehicleRepository;
    private final FuelRefillRepository fuelRefillRepository;
    private final GasStationRepository gasStationRepository;

    @Transactional
    public UUID registerFuelRefill(UUID vehicleId, UUID stationId, FuelRefillDTO dto) {
        Vehicle vehicle = vehicleRepository.findById(vehicleId)
                .orElseThrow(() -> new IllegalArgumentException("Vehicle not found"));

        if (vehicle.getKm() != null && dto.kmAtRefill() < vehicle.getKm()) {
            throw new IllegalArgumentException("Refill km must be higher than current vehicle km");
        }

        GasStation station = gasStationRepository.findById(stationId)
                .orElseThrow(() -> new IllegalArgumentException("Gas station not found"));

        FuelRefill newRefill = FuelRefill.builder()
                .vehicle(vehicle)
                .station(station)
                .liters(dto.liters())
                .pricePerLiter(dto.pricePerLiter())
                .totalCost(dto.liters() * dto.pricePerLiter())
                .kmAtRefill(dto.kmAtRefill())
                .fuelType(dto.fuelType())
                .refillDate(LocalDateTime.now())
                .build();

        // Calculate consumption
        Optional<FuelRefill> previousRefillOpt = fuelRefillRepository.findTopByVehicleAndKmAtRefillLessThanOrderByKmAtRefillDesc(vehicle, dto.kmAtRefill());

        if (previousRefillOpt.isPresent()) {
            FuelRefill previousRefill = previousRefillOpt.get();
            double kmDrivenSinceLastRefill = dto.kmAtRefill() - previousRefill.getKmAtRefill();
            if (previousRefill.getLiters() > 0) {
                double kmPerLiterForSegment = kmDrivenSinceLastRefill / previousRefill.getLiters();
                vehicle.setLastKmPerLiter(kmPerLiterForSegment);
            }

            // Initialize totals if null
            vehicle.setTotalLitersConsumed(Optional.ofNullable(vehicle.getTotalLitersConsumed()).orElse(0.0) + previousRefill.getLiters());
            vehicle.setTotalKmDriven(Optional.ofNullable(vehicle.getTotalKmDriven()).orElse(0.0) + kmDrivenSinceLastRefill);

            if (vehicle.getTotalLitersConsumed() > 0) {
                vehicle.setAverageKmPerLiter(vehicle.getTotalKmDriven() / vehicle.getTotalLitersConsumed());
            }
        } else {
            // First refill, initialize driven km if not already set from vehicle creation
            if (vehicle.getTotalKmDriven() == null || vehicle.getTotalKmDriven() == 0) {
                // Assuming kmAtRefill is the starting point or vehicle.getKm() was initial km
                // This part might need refinement based on how initial KM is handled
            }
        }

        vehicle.setKm(dto.kmAtRefill()); // Update current vehicle km
        vehicleRepository.save(vehicle);

        return fuelRefillRepository.save(newRefill).getId();
    }


    @Transactional
    public void updateFuelRefill(UUID id, FuelRefillDTO dto) {
        FuelRefill refill = fuelRefillRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Fuel refill not found"));
        Vehicle vehicle = refill.getVehicle();
        // Recalculation logic for update is more complex and needs careful consideration
        // For now, simple update of refill data and vehicle's current KM.
        // A full recalculation would involve finding refills before and after the updated one.

        // Store old values for potential rollback or complex recalculation
        double oldKmAtRefill = refill.getKmAtRefill();
        double oldLiters = refill.getLiters();

        refill.setKmAtRefill(dto.kmAtRefill());
        refill.setLiters(dto.liters());
        refill.setPricePerLiter(dto.pricePerLiter());
        refill.setTotalCost(dto.liters() * dto.pricePerLiter());
        refill.setFuelType(dto.fuelType());
        // refill.setRefillDate(dto.refillDate()); // Assuming DTO has LocalDate, Model has LocalDateTime. Needs mapping.

        // Update vehicle's current KM if this is the latest refill
        // This logic needs to be robust: check if this updated refill is still the one setting the vehicle's current KM
        vehicle.setKm(dto.kmAtRefill()); // Simplified: assumes this update reflects current KM

        // TODO: Implement robust recalculation of consumption metrics for the vehicle upon update.
        // This is a placeholder for a more complex logic.
        // For now, we are not recalculating historical averages on update to keep it simple.
        // We might need to adjust vehicle.lastKmPerLiter, totalLitersConsumed, totalKmDriven, averageKmPerLiter

        vehicleRepository.save(vehicle);
        fuelRefillRepository.save(refill);
    }

    @Transactional
    public void deleteFuelRefill(UUID id) {
        FuelRefill refillToDelete = fuelRefillRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Fuel refill not found to delete"));
        Vehicle vehicle = refillToDelete.getVehicle();

        // TODO: Implement robust recalculation of consumption metrics for the vehicle upon deletion.
        // This is a placeholder. It would involve finding refills before and after the deleted one
        // and adjusting vehicle.lastKmPerLiter, totalLitersConsumed, totalKmDriven, averageKmPerLiter.
        // For simplicity, we might just nullify lastKmPerLiter if it was derived from this segment,
        // and adjust totals. Average would become less accurate without full history recalculation.

        fuelRefillRepository.deleteById(id);
        // After deletion, the vehicle's KM might need to be set to the KM of the new latest refill.
        // And consumption data needs recalculation.
    }

    public Page<FuelRefillDTO> getFuelRefills(PageRequest pageRequest) {
        return fuelRefillRepository.findAll(pageRequest)
                .map(FuelRefillMapper.INSTANCE::toFuelRefillDTO);
    }

    public FuelRefill getFuelRefill(UUID id) {
        return fuelRefillRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Fuel refill not found"));
    }
}