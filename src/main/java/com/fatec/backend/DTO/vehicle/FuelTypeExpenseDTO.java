package com.fatec.backend.DTO.vehicle;

import com.fatec.backend.enums.FuelType;

public record FuelTypeExpenseDTO(FuelType fuelType,
                                 double totalCost) {
}
