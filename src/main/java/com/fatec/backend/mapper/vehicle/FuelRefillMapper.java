package com.fatec.backend.mapper.vehicle;

import com.fatec.backend.DTO.vehicle.FuelRefillDTO;
import com.fatec.backend.model.vehicle.FuelRefill;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface FuelRefillMapper {
    FuelRefillMapper INSTANCE = Mappers.getMapper(FuelRefillMapper.class);
    FuelRefill toFuelRefill(FuelRefillDTO fuelRefillDTO);
    FuelRefillDTO toFuelRefillDTO(FuelRefill fuelRefill);
}
