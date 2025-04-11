package com.fatec.backend.mapper.vehicle;

import com.fatec.backend.DTO.vehicle.VehicleDTO;
import com.fatec.backend.model.Vehicle;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface VehicleMapper {
    VehicleMapper INSTANCE = Mappers.getMapper(VehicleMapper.class);

    VehicleDTO ToVehicleDTO(Vehicle vehicle);
    Vehicle ToVehicle(VehicleDTO vehicleDTO);
}
