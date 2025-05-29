package com.fatec.backend.DTO.reminder;

import com.fatec.backend.enums.ReminderStatus;
import com.fatec.backend.enums.ReminderType;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.util.UUID;

public record ReminderDTO(
        UUID id,
        UUID userId, // Will be set from authenticated user in service layer
        UUID vehicleId, // Optional

        @NotBlank(message = "Title is mandatory")
        @Size(min = 3, max = 100, message = "Title must be between 3 and 100 characters")
        String title,

        @Size(max = 500, message = "Description can have a maximum of 500 characters")
        String description,

        @NotNull(message = "Reminder type is mandatory")
        ReminderType type,

        @NotNull(message = "Due date is mandatory")
        @FutureOrPresent(message = "Due date must be in the present or future")
        LocalDate dueDate,

        Integer antecedenceDays, // Optional, default will be set in entity or service
        ReminderStatus status, // Usually set by the system, but can be part of update
        boolean isRecurring
        // Add recurrenceInterval and customRecurrenceDays if needed for creation/update
) {}

