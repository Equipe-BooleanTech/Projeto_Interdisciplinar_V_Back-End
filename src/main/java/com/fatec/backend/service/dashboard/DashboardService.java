package com.fatec.backend.service.dashboard;

import com.fatec.backend.DTO.dashboard.ExpenseSummaryDTO;
import com.fatec.backend.DTO.dashboard.ExpenseTimeseriesDTO;
import com.fatec.backend.DTO.dashboard.TimeseriesDataPoint;
import com.fatec.backend.model.User;
import com.fatec.backend.model.vehicle.FuelRefill;
import com.fatec.backend.model.maintenance.Maintenance;
import com.fatec.backend.repository.FuelRefillRepository;
import com.fatec.backend.repository.MaintenanceRepository;
import com.fatec.backend.repository.UserRepository;
import com.fatec.backend.repository.VehicleRespository;
import com.fatec.backend.model.vehicle.Vehicle;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.temporal.TemporalAdjusters;
import java.time.temporal.WeekFields;
import java.util.*;
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
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado: " + userId));

        LocalDate endDate = LocalDate.now();
        LocalDate startDate = calculateStartDate(endDate, period);

        List<Vehicle> vehicles = getVehiclesForUser(user, vehicleId);
        if (vehicles.isEmpty() && vehicleId != null) {
            throw new IllegalArgumentException("Veículo não encontrado ou não pertence ao usuário.");
        }

        double totalExpenses = 0;
        Map<String, Double> expensesByCategory = new HashMap<>();
        Map<String, Double> expensesByVehicle = new HashMap<>();

        for (Vehicle v : vehicles) {
            // Otimização: Buscar dados filtrados diretamente do repositório
            List<FuelRefill> refills = fuelRefillRepository.findByVehicleAndRefillDateBetween(v, startDate.atStartOfDay(), endDate.atTime(23, 59, 59));
            List<Maintenance> maintenances = maintenanceRepository.findByVehicleAndDateBetween(v, startDate, endDate);

            double vehicleTotalFuelCost = refills.stream().mapToDouble(FuelRefill::getTotalCost).sum();
            double vehicleTotalMaintenanceCost = maintenances.stream().filter(m -> m.getCost() != null).mapToDouble(Maintenance::getCost).sum();

            totalExpenses += vehicleTotalFuelCost + vehicleTotalMaintenanceCost;
            expensesByCategory.merge("Combustível", vehicleTotalFuelCost, Double::sum);
            expensesByCategory.merge("Manutenção", vehicleTotalMaintenanceCost, Double::sum);
            // Usar nome/modelo do veículo como chave em vez de UUID para melhor leitura no frontend
            String vehicleKey = v.getBrand() + " " + v.getModel() + " (" + v.getLicensePlate() + ")";
            expensesByVehicle.put(vehicleKey, vehicleTotalFuelCost + vehicleTotalMaintenanceCost);
        }

        return new ExpenseSummaryDTO(period, startDate, endDate, totalExpenses, expensesByCategory, expensesByVehicle);
    }

    @Transactional(readOnly = true)
    public ExpenseTimeseriesDTO getExpenseTimeseries(UUID userId, String period, String groupBy, UUID vehicleId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado: " + userId));

        LocalDate endDate = LocalDate.now();
        LocalDate startDate = calculateTimeseriesStartDate(endDate, period);
        String periodDescription = validateAndGetPeriodDescription(period);
        String validatedGroupBy = validateAndGetGroupBy(groupBy);

        List<Vehicle> vehicles = getVehiclesForUser(user, vehicleId);
        if (vehicles.isEmpty() && vehicleId != null) {
            throw new IllegalArgumentException("Veículo não encontrado ou não pertence ao usuário.");
        }

        Map<LocalDate, Double> aggregatedExpenses = new HashMap<>();

        for (Vehicle v : vehicles) {
            // Otimização: Buscar dados filtrados diretamente do repositório
            fuelRefillRepository.findByVehicleAndRefillDateBetween(v, startDate.atStartOfDay(), endDate.atTime(23, 59, 59))
                    .forEach(fr -> {
                        LocalDate dateKey = getDateKey(fr.getRefillDate().toLocalDate(), validatedGroupBy);
                        aggregatedExpenses.merge(dateKey, fr.getTotalCost(), Double::sum);
                    });

            maintenanceRepository.findByVehicleAndDateBetween(v, startDate, endDate)
                    .stream().filter(m -> m.getCost() != null)
                    .forEach(m -> {
                        LocalDate dateKey = getDateKey(m.getDate(), validatedGroupBy);
                        aggregatedExpenses.merge(dateKey, m.getCost(), Double::sum);
                    });
        }

        List<TimeseriesDataPoint> timeseriesDataPoints = aggregatedExpenses.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .map(entry -> new TimeseriesDataPoint(entry.getKey(), entry.getValue()))
                .collect(Collectors.toList());

        return new ExpenseTimeseriesDTO(periodDescription, timeseriesDataPoints);
    }

    // --- Métodos auxiliares ---

    private List<Vehicle> getVehiclesForUser(User user, UUID vehicleId) {
        if (vehicleId == null) {
            return vehicleRepository.findByUser(user);
        } else {
            return vehicleRepository.findByIdAndUser(vehicleId, user)
                    .map(List::of)
                    .orElse(Collections.emptyList());
        }
    }

    private LocalDate calculateStartDate(LocalDate endDate, String period) {
        switch (period.toLowerCase()) {
            case "weekly":
                return endDate.minusWeeks(1).plusDays(1);
            case "monthly":
                return endDate.with(TemporalAdjusters.firstDayOfMonth());
            case "yearly":
                return endDate.with(TemporalAdjusters.firstDayOfYear());
            default:
                throw new IllegalArgumentException("Período inválido. Use 'weekly', 'monthly', ou 'yearly'.");
        }
    }

    private LocalDate calculateTimeseriesStartDate(LocalDate endDate, String period) {
        switch (period.toLowerCase()) {
            case "last_6_months":
                return endDate.minusMonths(6).with(TemporalAdjusters.firstDayOfMonth());
            case "last_12_months":
                return endDate.minusMonths(12).with(TemporalAdjusters.firstDayOfMonth());
            default:
                throw new IllegalArgumentException("Período inválido para série temporal. Use 'last_6_months' ou 'last_12_months'.");
        }
    }

    private String validateAndGetPeriodDescription(String period) {
        switch (period.toLowerCase()) {
            case "last_6_months":
            case "last_12_months":
                return period.toLowerCase();
            default:
                throw new IllegalArgumentException("Período inválido para série temporal. Use 'last_6_months' ou 'last_12_months'.");
        }
    }

    private String validateAndGetGroupBy(String groupBy) {
        switch (groupBy.toLowerCase()) {
            case "day":
            case "week":
            case "month":
                return groupBy.toLowerCase();
            default:
                throw new IllegalArgumentException("Agrupamento inválido. Use 'day', 'week', ou 'month'.");
        }
    }

    private LocalDate getDateKey(LocalDate date, String groupBy) {
        switch (groupBy) {
            case "day":
                return date;
            case "week":
                // Usa a definição ISO 8601 da semana (segunda a domingo)
                return date.with(TemporalAdjusters.previousOrSame(java.time.DayOfWeek.MONDAY));
            case "month":
                return date.with(TemporalAdjusters.firstDayOfMonth());
            default:
                // Default case should not be reached due to validation
                return date.with(TemporalAdjusters.firstDayOfMonth());
        }
    }
}
