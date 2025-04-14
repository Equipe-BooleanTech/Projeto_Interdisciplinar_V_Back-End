package com.fatec.backend.mapper.vehicle;

import com.fatec.backend.DTO.vehicle.GasStationDTO;
import com.fatec.backend.model.vehicle.GasStation;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface GasStationMapper {
    GasStationMapper INSTANCE = Mappers.getMapper( GasStationMapper.class );
    GasStation toGasStation(GasStationDTO dto);
    GasStationDTO toGasStationDTO(GasStation gasStation);
}
