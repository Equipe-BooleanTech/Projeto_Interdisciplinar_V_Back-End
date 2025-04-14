package com.fatec.backend.DTO.vehicle;

import com.fatec.backend.model.User.User;

import java.util.UUID;

public record VehicleDTO(
        UUID id,
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
        User userId
) {
}
