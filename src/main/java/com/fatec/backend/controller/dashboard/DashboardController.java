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

    // TODO: Substituir mockUserId pela obtenção real do ID do usuário autenticado
    private final UUID mockUserId = UUID.fromString("d0f6e2d9-4f8c-4f8b-9b8b-0e2f0c6f0a1b");

    /**
     * Retorna um resumo de despesas (combustível e manutenção) para o usuário autenticado,
     * permitindo filtrar por período (semanal, mensal, anual) e opcionalmente por veículo.
     *
     * @param period    Período para o resumo ("weekly", "monthly", "yearly"). Default: "monthly".
     * @param vehicleId (Opcional) UUID do veículo para filtrar despesas.
     * @return ResponseEntity com ExpenseSummaryDTO.
     */
    @GetMapping("/expenses/summary")
    public ResponseEntity<ExpenseSummaryDTO> getExpenseSummary(
            @RequestParam(defaultValue = "monthly") String period,
            @RequestParam(required = false) UUID vehicleId) {
        // A validação dos parâmetros 'period' deve ocorrer no service
        ExpenseSummaryDTO summary = dashboardService.getExpenseSummary(mockUserId, period, vehicleId);
        return ResponseEntity.ok(summary);
    }

    /**
     * Retorna uma série temporal de despesas para o usuário autenticado,
     * permitindo filtrar por período (últimos 6 ou 12 meses), agrupar (dia, semana, mês)
     * e opcionalmente por veículo.
     *
     * @param period    Período da série temporal ("last_6_months", "last_12_months"). Default: "last_6_months".
     * @param groupBy   Agrupamento dos dados ("day", "week", "month"). Default: "month".
     * @param vehicleId (Opcional) UUID do veículo para filtrar despesas.
     * @return ResponseEntity com ExpenseTimeseriesDTO.
     */
    @GetMapping("/expenses/timeseries")
    public ResponseEntity<ExpenseTimeseriesDTO> getExpenseTimeseries(
            @RequestParam(defaultValue = "last_6_months") String period,
            @RequestParam(defaultValue = "month") String groupBy,
            @RequestParam(required = false) UUID vehicleId) {
        // A validação dos parâmetros 'period' e 'groupBy' deve ocorrer no service
        ExpenseTimeseriesDTO timeseries = dashboardService.getExpenseTimeseries(mockUserId, period, groupBy, vehicleId);
        return ResponseEntity.ok(timeseries);
    }
}
