package com.fatec.backend.DTO.maintenance;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import java.util.UUID;

public record MaintenanceDTO(
        UUID id,
        @NotNull(message = "Vehicle ID is mandatory")
        UUID vehicleId,

        @NotNull(message = "Maintenance date is mandatory")
        LocalDate date,

        @NotNull(message = "Odometer reading is mandatory")
        @PositiveOrZero(message = "Odometer reading must be positive or zero")
        Double odometer,

        @NotBlank(message = "Maintenance type is mandatory")
        @Size(min = 3, max = 100, message = "Maintenance type must be between 3 and 100 characters")
        String type,

        @Size(max = 1000, message = "Description can have a maximum of 1000 characters")
        String description,

        @PositiveOrZero(message = "Cost must be positive or zero")
        Double cost,

        @Size(max = 100, message = "Workshop name can have a maximum of 100 characters")
        String workshopName,

        @FutureOrPresent(message = "Next due date must be in the present or future, if provided")
        LocalDate nextDueDate, // Optional

        @PositiveOrZero(message = "Next due odometer must be positive or zero, if provided")
        Double nextDueOdometer // Optional
) {}

