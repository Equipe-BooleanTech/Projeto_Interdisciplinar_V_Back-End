package com.fatec.backend.DTO.vehicle;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record DateRangeDTO (LocalDateTime startDate,
                            LocalDateTime endDate){
}
