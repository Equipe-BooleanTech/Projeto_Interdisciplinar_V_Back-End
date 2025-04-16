package com.fatec.backend.service.vehicle;
import com.fatec.backend.DTO.vehicle.FuelRefillDTO;
import com.fatec.backend.DTO.vehicle.FuelRefillSummaryDTO;
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

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FuelRefillService {

    private final VehicleRespository vehicleRepository;
    private final FuelRefillRepository fuelRefillRepository;
    private final GasStationRepository gasStationRepository;


    public UUID registerFuelRefill(UUID vehicleId, UUID stationId,FuelRefillDTO dto) {
        Vehicle vehicle = vehicleRepository.findById(vehicleId)
                .orElseThrow(() -> new IllegalArgumentException("Vehicle not found"));

        if (dto.kmAtRefill() < vehicle.getKm()) {
            throw new IllegalArgumentException("Refill km must be higher than current vehicle km");
        }

        GasStation station = gasStationRepository.findById(stationId)
                .orElseThrow(() -> new IllegalArgumentException("Gas station not found"));

        FuelRefill refill = FuelRefill.builder()
                .vehicle(vehicle)
                .station(station)
                .liters(dto.liters())
                .pricePerLiter(dto.pricePerLiter())
                .totalCost(dto.liters() * dto.pricePerLiter())
                .kmAtRefill(dto.kmAtRefill())
                .fuelType(dto.fuelType())
                .refillDate(LocalDateTime.now())
                .build();

        vehicle.setKm(dto.kmAtRefill());
        vehicleRepository.save(vehicle);

        return fuelRefillRepository.save(refill).getId();
    }



    public void updateFuelRefill(UUID id, FuelRefillDTO dto) {
        FuelRefill refill = fuelRefillRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Fuel refill not found"));
        Vehicle vehicle = vehicleRepository.findById(refill.getVehicle().getUuid())
                .orElseThrow(() -> new IllegalArgumentException("Vehicle not found"));

        GasStation station = gasStationRepository.findById(refill.getStation().getId())
                .orElseThrow(() -> new IllegalArgumentException("Gas station not found"));


        refill.setKmAtRefill(dto.kmAtRefill());
        refill.setLiters(dto.liters());
        refill.setPricePerLiter(dto.pricePerLiter());
        refill.setTotalCost(dto.liters() * dto.pricePerLiter());
        vehicle.setKm(dto.kmAtRefill());
        vehicleRepository.save(vehicle);

        fuelRefillRepository.save(refill);
    }


    public void deleteFuelRefill(UUID id) {
        fuelRefillRepository.deleteById(id);
    }

    public Page<FuelRefillDTO> getFuelRefills(PageRequest pageRequest) {
        return fuelRefillRepository.findAll(pageRequest)
                .map(FuelRefillMapper.INSTANCE::toFuelRefillDTO);
    }
}