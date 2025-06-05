package com.fatec.backend.mapper.vehicle;

import com.fatec.backend.DTO.vehicle.FuelRefillDTO;
import com.fatec.backend.model.vehicle.FuelRefill;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface FuelRefillMapper {
    FuelRefillMapper INSTANCE = Mappers.getMapper(FuelRefillMapper.class);

    @Mapping(source = "vehicle.id", target = "vehicleId")
    @Mapping(source = "station.id", target = "stationId")
    FuelRefillDTO toFuelRefillDTO(FuelRefill fuelRefill);

    @Mapping(source = "vehicleId", target = "vehicle.id")
    @Mapping(source = "stationId", target = "station.id")
    FuelRefill toFuelRefill(FuelRefillDTO fuelRefillDTO);
}
