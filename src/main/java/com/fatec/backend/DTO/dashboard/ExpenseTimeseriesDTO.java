package com.fatec.backend.DTO.dashboard;

import java.time.LocalDate;
import java.util.List;

// DTO for expense timeseries data
public record ExpenseTimeseriesDTO(
        String periodDescription, // e.g., "last_6_months"
        List<TimeseriesDataPoint> timeseries
) {}