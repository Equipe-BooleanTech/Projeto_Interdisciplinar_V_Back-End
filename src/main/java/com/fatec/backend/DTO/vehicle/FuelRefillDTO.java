package com.fatec.backend.DTO.vehicle;

import com.fatec.backend.enums.FuelType;

import java.time.LocalDate;
import java.util.UUID;

public record FuelRefillDTO(
        UUID id,
        UUID vehicleId,
        UUID stationId,
        double liters,
        double pricePerLiter,
        double totalCost,
        double kmAtRefill,
        FuelType fuelType,
        LocalDate refillDate
) {}