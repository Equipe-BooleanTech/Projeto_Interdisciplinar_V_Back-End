package com.fatec.backend.DTO.dashboard;

import java.time.LocalDate;
import java.util.Map;

// DTO for expense summary
public record ExpenseSummaryDTO(
        String period,
        LocalDate startDate,
        LocalDate endDate,
        Double totalExpenses,
        Map<String, Double> expensesByCategory, // e.g., {"fuel": 100.0, "maintenance": 50.0}
        Map<String, Double> expensesByVehicle // e.g., {"vehicle_uuid_1": 80.0, "vehicle_uuid_2": 70.0}
) {}

