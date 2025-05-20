package com.fatec.backend.service.dashboard;

import com.fatec.backend.model.User;
import com.fatec.backend.model.vehicle.FuelRefill;
import com.fatec.backend.model.vehicle.Vehicle;
import com.fatec.backend.repository.FuelRefillRepository;
import com.fatec.backend.repository.UserRepository;
import com.fatec.backend.repository.VehicleRespository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DashboardService {
    private final FuelRefillRepository fuelRefillRepository;
    private final MaintenanceRepository maintenanceRepository;
    private final UserRepository userRepository;
    private final VehicleRespository vehicleRepository;

    @Transactional(readOnly = true)
    public ExpenseSummaryDTO getExpenseSummary(UUID userId, String period, UUID vehicleId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + userId));

        LocalDate startDate;
        LocalDate endDate = LocalDate.now();

        switch (period.toLowerCase()) {
            case "weekly":
                startDate = endDate.minusWeeks(1).plusDays(1);
                break;
            case "monthly":
                startDate = endDate.with(TemporalAdjusters.firstDayOfMonth());
                break;
            case "yearly":
                startDate = endDate.with(TemporalAdjusters.firstDayOfYear());
                break;
            default:
                throw new IllegalArgumentException("Invalid period specified. Use 'weekly', 'monthly', or 'yearly'.");
        }

        List<Vehicle> vehicles = vehicleId == null ? vehicleRepository.findByUser(user) :
                List.of(vehicleRepository.findByIdAndUser(vehicleId, user)
                        .orElseThrow(() -> new IllegalArgumentException("Vehicle not found or does not belong to user")));

        double totalExpenses = 0;
        Map<String, Double> expensesByCategory = new HashMap<>();
        Map<String, Double> expensesByVehicle = new HashMap<>();

        for (Vehicle v : vehicles) {
            double vehicleTotalFuelCost = 0;
            double vehicleTotalMaintenanceCost = 0;

            List<FuelRefill> refills = fuelRefillRepository.findAll().stream()
                    .filter(fr -> fr.getVehicle().equals(v) && !fr.getRefillDate().toLocalDate().isBefore(startDate) && !fr.getRefillDate().toLocalDate().isAfter(endDate))
                    .collect(Collectors.toList());

            for (FuelRefill refill : refills) {
                totalExpenses += refill.getTotalCost();
                expensesByCategory.merge("fuel", refill.getTotalCost(), Double::sum);
                vehicleTotalFuelCost += refill.getTotalCost();
            }

            List<Maintenance> maintenances = maintenanceRepository.findAll().stream()
                    .filter(m -> m.getVehicle().equals(v) && !m.getDate().isBefore(startDate) && !m.getDate().isAfter(endDate))
                    .collect(Collectors.toList());

            for (Maintenance maintenance : maintenances) {
                if (maintenance.getCost() != null) {
                    totalExpenses += maintenance.getCost();
                    expensesByCategory.merge("maintenance", maintenance.getCost(), Double::sum);
                    vehicleTotalMaintenanceCost += maintenance.getCost();
                }
            }
            expensesByVehicle.put(v.getUuid().toString(), vehicleTotalFuelCost + vehicleTotalMaintenanceCost);
        }

        return new ExpenseSummaryDTO(period, startDate, endDate, totalExpenses, expensesByCategory, expensesByVehicle);
    }

    @Transactional(readOnly = true)
    public ExpenseTimeseriesDTO getExpenseTimeseries(UUID userId, String period, String groupBy, UUID vehicleId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + userId));

        LocalDate endDate = LocalDate.now();
        LocalDate startDate;
        String periodDescription = period;

        switch (period.toLowerCase()) {
            case "last_6_months":
                startDate = endDate.minusMonths(6).with(TemporalAdjusters.firstDayOfMonth());
                break;
            case "last_12_months":
                startDate = endDate.minusMonths(12).with(TemporalAdjusters.firstDayOfMonth());
                break;
            // case "custom_range": // Needs date parameters from request
            //     throw new UnsupportedOperationException("Custom range not yet implemented without date params");
            default:
                startDate = endDate.minusMonths(6).with(TemporalAdjusters.firstDayOfMonth()); // Default to last 6 months
                periodDescription = "last_6_months";
        }

        List<Vehicle> vehicles = vehicleId == null ? vehicleRepository.findByUser(user) :
                List.of(vehicleRepository.findByIdAndUser(vehicleId, user)
                        .orElseThrow(() -> new IllegalArgumentException("Vehicle not found or does not belong to user")));

        Map<LocalDate, Double> aggregatedExpenses = new HashMap<>();

        for (Vehicle v : vehicles) {
            fuelRefillRepository.findAll().stream()
                    .filter(fr -> fr.getVehicle().equals(v) && !fr.getRefillDate().toLocalDate().isBefore(startDate) && !fr.getRefillDate().toLocalDate().isAfter(endDate))
                    .forEach(fr -> {
                        LocalDate dateKey = getDateKey(fr.getRefillDate().toLocalDate(), groupBy);
                        aggregatedExpenses.merge(dateKey, fr.getTotalCost(), Double::sum);
                    });

            maintenanceRepository.findAll().stream()
                    .filter(m -> m.getVehicle().equals(v) && !m.getDate().isBefore(startDate) && !m.getDate().isAfter(endDate) && m.getCost() != null)
                    .forEach(m -> {
                        LocalDate dateKey = getDateKey(m.getDate(), groupBy);
                        aggregatedExpenses.merge(dateKey, m.getCost(), Double::sum);
                    });
        }

        List<TimeseriesDataPoint> timeseriesDataPoints = aggregatedExpenses.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .map(entry -> new TimeseriesDataPoint(entry.getKey(), entry.getValue()))
                .collect(Collectors.toList());

        return new ExpenseTimeseriesDTO(periodDescription, timeseriesDataPoints);
    }

    private LocalDate getDateKey(LocalDate date, String groupBy) {
        switch (groupBy.toLowerCase()) {
            case "day":
                return date;
            case "week":
                return date.with(TemporalAdjusters.previousOrSame(java.time.DayOfWeek.MONDAY)); // Start of the week
            case "month":
                return date.with(TemporalAdjusters.firstDayOfMonth());
            default:
                return date.with(TemporalAdjusters.firstDayOfMonth()); // Default to month
        }
    }
}
}
