package com.fatec.backend.DTO.vehicle;

public record ExpenseSummaryDTO(
    double maintenanceCost,
    double fuelCost,
    double totalCost
) {
}
