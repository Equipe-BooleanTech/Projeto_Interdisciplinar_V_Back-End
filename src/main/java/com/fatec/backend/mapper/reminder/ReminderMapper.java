package com.fatec.backend.mapper.reminder;

import com.fatec.backend.DTO.reminder.ReminderDTO;
import com.fatec.backend.model.reminder.Reminder;
import com.fatec.backend.model.User;
import com.fatec.backend.model.vehicle.Vehicle;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

import java.util.UUID;

@Mapper(componentModel = "spring")
public interface ReminderMapper {
    ReminderMapper INSTANCE = Mappers.getMapper(ReminderMapper.class);

    @Mapping(source = "userId", target = "user", qualifiedByName = "uuidToUser")
    @Mapping(source = "vehicleId", target = "vehicle", qualifiedByName = "uuidToVehicle")
    Reminder toEntity(ReminderDTO reminderDTO);

    @Mapping(source = "user.id", target = "userId")
    @Mapping(source = "vehicle.uuid", target = "vehicleId")
    ReminderDTO toDTO(Reminder reminder);

    @Named("uuidToUser")
    default User uuidToUser(UUID userId) {
        if (userId == null) {
            return null;
        }
        User user = new User();
        user.setId(userId);
        return user;
    }

    @Named("uuidToVehicle")
    default Vehicle uuidToVehicle(UUID vehicleId) {
        if (vehicleId == null) {
            return null;
        }
        Vehicle vehicle = new Vehicle();
        vehicle.setUuid(vehicleId);
        return vehicle;
    }
}

