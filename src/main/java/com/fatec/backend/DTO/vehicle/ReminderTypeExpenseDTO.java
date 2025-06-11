package com.fatec.backend.DTO.vehicle;

import com.fatec.backend.enums.ReminderType;

public record ReminderTypeExpenseDTO(ReminderType reminderType,
                                     double totalCost ){
}
