package com.fatec.backend.mapper.reminder;

import com.fatec.backend.DTO.reminder.ReminderDTO;
import com.fatec.backend.DTO.vehicle.MaintenanceDTO;
import com.fatec.backend.mapper.maintenance.MaintenanceMapper;
import com.fatec.backend.model.reminder.Reminder;
import com.fatec.backend.model.User;
import com.fatec.backend.model.vehicle.Maintenance;
import com.fatec.backend.model.vehicle.Vehicle;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

import java.util.UUID;

@Mapper(componentModel = "spring")
public interface ReminderMapper {
    ReminderMapper INSTANCE = Mappers.getMapper(ReminderMapper.class);

    ReminderDTO ToReminderDTO(Reminder reminder);
    Reminder ToReminder(ReminderDTO reminderDTO);
}

