package com.fatec.backend.service.vehicle;

import com.fatec.backend.DTO.vehicle.ExpenseSummaryDTO;
import com.fatec.backend.model.UserDetailsImpl;
import com.fatec.backend.model.vehicle.FuelRefill;
import com.fatec.backend.model.vehicle.Maintenance;
import com.fatec.backend.model.vehicle.Vehicle;
import com.fatec.backend.repository.FuelRefillRepository;
import com.fatec.backend.repository.MaintenanceRepository;
import com.fatec.backend.repository.VehicleRespository;
import lombok.AllArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;
@AllArgsConstructor
@Service
public class VehicleExpenseService {
    private final MaintenanceRepository maintenanceRepository;
    private final FuelRefillRepository fuelRefillRepository;
    private final VehicleRespository vehicleRepository;

    public ExpenseSummaryDTO calculateVehicleExpenses(UUID vehicleId, LocalDate startDate, LocalDate endDate) {
        LocalDateTime start = startDate.atStartOfDay();
        LocalDateTime end = endDate.atTime(LocalTime.MAX);

        Vehicle vehicle = vehicleRepository.findById(vehicleId)
                .orElseThrow(() -> new IllegalArgumentException("Vehicle not found"));

        UUID userId = getCurrentUserId();
        if (!vehicle.getUser().getId().equals(userId)) {
            throw new AccessDeniedException("You are not allowed to access this vehicle's data.");
        }

        List<Maintenance> maintenances = maintenanceRepository.findByVehicleIdAndDateBetween(vehicleId, start, end);
        List<FuelRefill> refuels = fuelRefillRepository.findByVehicleIdAndRefillDateBetween(vehicleId, start, end);

        double maintenanceCost = maintenances.stream()
                .mapToDouble(m -> m.getCost() != null ? m.getCost() : 0.0)
                .sum();

        double fuelCost = refuels.stream()
                .mapToDouble(FuelRefill::getTotalCost)
                .sum();

        return new ExpenseSummaryDTO(maintenanceCost, fuelCost, maintenanceCost + fuelCost);
    }

    private UUID getCurrentUserId() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        var principal = authentication.getPrincipal();

        if (principal instanceof UserDetailsImpl userDetails) {
            return userDetails.getUserId();
        }

        throw new AccessDeniedException("Unauthorized: invalid principal");
    }

}
