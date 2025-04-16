package com.fatec.backend.service.vehicle;
import com.fatec.backend.DTO.vehicle.FuelRefillDTO;
import com.fatec.backend.DTO.vehicle.FuelRefillSummaryDTO;
import com.fatec.backend.model.vehicle.FuelRefill;
import com.fatec.backend.model.vehicle.GasStation;
import com.fatec.backend.model.vehicle.Vehicle;
import com.fatec.backend.repository.FuelRefillRepository;
import com.fatec.backend.repository.GasStationRepository;
import com.fatec.backend.repository.VehicleRespository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FuelRefillService {

    private final VehicleRespository vehicleRepository;
    private final FuelRefillRepository fuelRefillRepository;
    private final GasStationRepository gasStationRepository;


    public UUID registerFuelRefill(FuelRefillDTO dto) {
        Vehicle vehicle = vehicleRepository.findById(dto.vehicleId())
                .orElseThrow(() -> new IllegalArgumentException("Vehicle not found"));

        if (dto.kmAtRefill() < vehicle.getKm()) {
            throw new IllegalArgumentException("Refill km must be higher than current vehicle km");
        }

        GasStation station = gasStationRepository.findById(dto.stationId())
                .orElseThrow(() -> new IllegalArgumentException("Gas station not found"));

        FuelRefill refill = FuelRefill.builder()
                .vehicle(vehicle)
                .station(station)
                .liters(dto.liters())
                .pricePerLiter(dto.pricePerLiter())
                .totalCost(dto.liters() * dto.pricePerLiter())
                .kmAtRefill(dto.kmAtRefill())
                .fuelType(dto.fuelType())
                .date(LocalDateTime.now())
                .build();

        vehicle.setKm(dto.kmAtRefill());
        vehicleRepository.save(vehicle);

        return fuelRefillRepository.save(refill).getId();
    }

    private FuelRefillSummaryDTO summarize(Page<FuelRefill> page) {
        double totalLiters = page.stream().mapToDouble(FuelRefill::getLiters).sum();
        double totalCost = page.stream().mapToDouble(FuelRefill::getTotalCost).sum();
        return new FuelRefillSummaryDTO(page, totalLiters, totalCost);
    }


    public UUID updateFuelRefill(UUID id,FuelRefillDTO dto) {
        Vehicle vehicle = vehicleRepository.findById(dto.vehicleId())
                .orElseThrow(() -> new IllegalArgumentException("Vehicle not found"));

        GasStation station = gasStationRepository.findById(dto.stationId())
                .orElseThrow(() -> new IllegalArgumentException("Gas station not found"));

        FuelRefill refill = fuelRefillRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Fuel refill not found"));
        refill.setKmAtRefill(dto.kmAtRefill());
        refill.setLiters(dto.liters());
        refill.setPricePerLiter(dto.pricePerLiter());
        refill.setTotalCost(dto.liters() * dto.pricePerLiter());
        vehicle.setKm(dto.kmAtRefill());
        vehicleRepository.save(vehicle);

        return fuelRefillRepository.save(refill).getId();
    }


    public void deleteFuelRefill(UUID id) {
        fuelRefillRepository.deleteById(id);
    }

}