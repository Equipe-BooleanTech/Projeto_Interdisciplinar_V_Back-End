package com.fatec.backend.mapper.maintenance;

import com.fatec.backend.DTO.vehicle.MaintenanceDTO;
import com.fatec.backend.model.vehicle.Maintenance;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface MaintenanceMapper {
    MaintenanceMapper INSTANCE = Mappers.getMapper(MaintenanceMapper.class);

    MaintenanceDTO ToMaintenanceDTO(Maintenance maintenance);
    Maintenance ToMaintenance(MaintenanceDTO maintenanceDTO);

}

