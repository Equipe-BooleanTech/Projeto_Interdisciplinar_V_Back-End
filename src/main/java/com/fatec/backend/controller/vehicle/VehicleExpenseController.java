package com.fatec.backend.controller.vehicle;

import com.fatec.backend.DTO.vehicle.ExpenseSummaryDTO;
import com.fatec.backend.DTO.vehicle.FuelTypeExpenseDTO;
import com.fatec.backend.DTO.vehicle.ReminderTypeExpenseDTO;
import com.fatec.backend.service.vehicle.VehicleExpenseService;
import lombok.AllArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@RestController
@AllArgsConstructor
@RequestMapping("/api/vehicle")
public class VehicleExpenseController {
    private final VehicleExpenseService vehicleExpenseService;

    @GetMapping("/expenses/{vehicleId}")
    public ExpenseSummaryDTO getVehicleExpenses(
            @PathVariable UUID vehicleId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return vehicleExpenseService.calculateVehicleExpenses(vehicleId, startDate, endDate);
    }

    @GetMapping("/expenses/{vehicleId}/by-fuel-type")
    public List<FuelTypeExpenseDTO> getFuelExpensesByType(
            @PathVariable UUID vehicleId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return vehicleExpenseService.calculateFuelExpensesByType(vehicleId, startDate, endDate);
    }

    @GetMapping("/expenses/{vehicleId}/by-reminder-type")
    public List<ReminderTypeExpenseDTO> getReminderExpensesByType(
            @PathVariable UUID vehicleId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return vehicleExpenseService.calculateReminderExpensesByType(vehicleId, startDate, endDate);
    }
}
