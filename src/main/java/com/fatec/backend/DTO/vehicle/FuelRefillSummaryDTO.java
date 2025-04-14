package com.fatec.backend.DTO.vehicle;

import com.fatec.backend.model.vehicle.FuelRefill;
import org.springframework.data.domain.Page;

public record FuelRefillSummaryDTO(
        Page<FuelRefill> refills,
        double totalLiters,
        double totalCost
) {}