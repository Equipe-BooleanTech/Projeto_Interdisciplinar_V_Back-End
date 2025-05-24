package com.fatec.backend.controller.dashboard;

import com.fatec.backend.DTO.dashboard.ExpenseSummaryDTO;
import com.fatec.backend.DTO.dashboard.ExpenseTimeseriesDTO;
import com.fatec.backend.service.dashboard.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;

    // Placeholder for getting authenticated user ID. In a real app, this would come from security context.
    private UUID getCurrentUserId() {
        // This is a MOCK. Replace with actual user ID retrieval from security context.
        System.out.println("Warning: getCurrentUserId() in DashboardController is a placeholder.");
        // For testing purposes, returning a hardcoded UUID. THIS IS NOT FOR PRODUCTION.
        return UUID.fromString("d0f6e2d9-4f8c-4f8b-9b8b-0e2f0c6f0a1b"); // Example User ID
    }

    @GetMapping("/expenses/summary")
    public ResponseEntity<ExpenseSummaryDTO> getExpenseSummary(
            @RequestParam(defaultValue = "monthly") String period, // weekly, monthly, yearly
            @RequestParam(required = false) UUID vehicleId) {
        UUID userId = getCurrentUserId(); // Replace with actual user ID retrieval
        ExpenseSummaryDTO summary = dashboardService.getExpenseSummary(userId, period, vehicleId);
        return ResponseEntity.ok(summary);
    }

    @GetMapping("/expenses/timeseries")
    public ResponseEntity<ExpenseTimeseriesDTO> getExpenseTimeseries(
            @RequestParam(defaultValue = "last_6_months") String period, // last_6_months, last_12_months
            @RequestParam(defaultValue = "month") String groupBy, // day, week, month
            @RequestParam(required = false) UUID vehicleId) {
        UUID userId = getCurrentUserId(); // Replace with actual user ID retrieval
        ExpenseTimeseriesDTO timeseries = dashboardService.getExpenseTimeseries(userId, period, groupBy, vehicleId);
        return ResponseEntity.ok(timeseries);
    }
}

