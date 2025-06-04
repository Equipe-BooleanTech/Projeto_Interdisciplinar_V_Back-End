package com.fatec.backend.DTO.vehicle;

import com.fatec.backend.enums.FuelType;
import com.fatec.backend.model.User;

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
        String odometer,
        FuelType fuelType,
        Double fuelCapacity,
        Double fuelConsumption,
        User userId
) {
}
