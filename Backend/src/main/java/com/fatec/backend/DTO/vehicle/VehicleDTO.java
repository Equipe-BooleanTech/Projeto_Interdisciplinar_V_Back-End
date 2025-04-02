package com.fatec.backend.DTO.vehicle;

public record VehicleDTO(
        String plate,
         String model,
         String color,
         String manufacturer,
         String type,
         String description,
         String year,
         String Km,
         String fuelType,
         Double fuelCapacity,
         Double fuelConsumption
) {
}
