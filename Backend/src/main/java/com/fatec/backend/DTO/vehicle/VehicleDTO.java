package com.fatec.backend.DTO.vehicle;

import java.util.UUID;

public record VehicleDTO(
        String plate,
        String model,
        String color,
        String manufacturer,
        String type,
        String description,
        String year,
        String km,
        String fuelType,
        Double fuelCapacity,
        Double fuelConsumption,
        UUID userId
) {
}
