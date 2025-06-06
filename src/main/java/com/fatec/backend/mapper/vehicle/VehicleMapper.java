package com.fatec.backend.mapper.vehicle;

import com.fatec.backend.DTO.vehicle.VehicleDTO;
import com.fatec.backend.model.vehicle.Vehicle;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface VehicleMapper {
    VehicleMapper INSTANCE = Mappers.getMapper(VehicleMapper.class);
   @Mapping(source = "user.id", target = "userId")
    VehicleDTO ToVehicleDTO(Vehicle vehicle);
    @Mapping(source = "userId", target = "user.id")
    Vehicle ToVehicle(VehicleDTO vehicleDTO);
}
