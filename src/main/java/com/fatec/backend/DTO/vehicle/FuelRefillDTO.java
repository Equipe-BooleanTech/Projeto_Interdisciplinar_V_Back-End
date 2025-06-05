package com.fatec.backend.DTO.vehicle;

import com.fatec.backend.enums.FuelType;
import com.fatec.backend.model.vehicle.GasStation;
import com.fatec.backend.model.vehicle.Vehicle;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

public record FuelRefillDTO(
        UUID id,
        UUID vehicleId,
        UUID stationId,
        double liters,
        double pricePerLiter,
        double totalCost,
        double kmAtRefill,
        Boolean isCompleteTank,
        FuelType fuelType,
        LocalDateTime refillDate
) {}