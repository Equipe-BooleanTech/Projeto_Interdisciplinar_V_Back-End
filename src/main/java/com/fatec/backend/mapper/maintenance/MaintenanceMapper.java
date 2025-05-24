package com.fatec.backend.mapper.maintenance;

import com.fatec.backend.DTO.maintenance.MaintenanceDTO;
import com.fatec.backend.model.maintenance.Maintenance;
import com.fatec.backend.model.vehicle.Vehicle;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

import java.util.UUID;

@Mapper(componentModel = "spring")
public interface MaintenanceMapper {
    MaintenanceMapper INSTANCE = Mappers.getMapper(MaintenanceMapper.class);

    @Mapping(source = "vehicleId", target = "vehicle", qualifiedByName = "uuidToVehicleForMaintenance")
    Maintenance toEntity(MaintenanceDTO maintenanceDTO);

    @Mapping(source = "vehicle.uuid", target = "vehicleId")
    MaintenanceDTO toDTO(Maintenance maintenance);

    // Reusing the uuidToVehicle logic, but named differently to avoid conflicts if ReminderMapper is in the same context
    // and MapStruct gets confused. Or ensure they are in different packages and component scanning is specific.
    @Named("uuidToVehicleForMaintenance")
    default Vehicle uuidToVehicleForMaintenance(UUID vehicleId) {
        if (vehicleId == null) {
            return null;
        }
        Vehicle vehicle = new Vehicle();
        vehicle.setUuid(vehicleId);
        return vehicle;
    }
}

