package com.fatec.backend.DTO.vehicle;

import com.fatec.backend.enums.FuelType;
import java.util.UUID;

public record FuelRefillDTO(
        UUID vehicleId,
        UUID stationId,
        double liters,
        double pricePerLiter,
        double kmAtRefill,
        FuelType fuelType
) {}