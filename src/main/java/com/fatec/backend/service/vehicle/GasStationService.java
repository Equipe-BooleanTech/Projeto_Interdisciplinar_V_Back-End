package com.fatec.backend.service.vehicle;

import com.fatec.backend.DTO.vehicle.GasStationDTO;
import com.fatec.backend.mapper.vehicle.GasStationMapper;
import com.fatec.backend.model.vehicle.GasStation;
import com.fatec.backend.repository.GasStationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class GasStationService {
    private final GasStationRepository gasStationRepository;

    public UUID createGasStation(GasStationDTO dto) {
        GasStation gasStation = GasStation.builder()
                .name(dto.name())
                .state(dto.state())
                .city(dto.city())
                .description(dto.description())
                .rating(dto.rating())
                .build();

        return gasStationRepository.save(gasStation).getId();
    }
    public void updateGasStation(UUID id, GasStationDTO dto) {
        GasStation gasStation = gasStationRepository.findById(id).orElseThrow(() -> new RuntimeException("GasStation not found"));
        gasStation.setName(dto.name());
        gasStation.setState(dto.state());
        gasStation.setCity(dto.city());
        gasStation.setDescription(dto.description());
        gasStation.setRating(dto.rating());
        gasStationRepository.save(gasStation);
    }
    public void deleteGasStation(UUID gasStationId) {
        gasStationRepository.deleteById(gasStationId);
    }

    public Page<GasStationDTO> listGasStations(PageRequest pageRequest) {
        return gasStationRepository.findAll(pageRequest)
                .map(GasStationMapper.INSTANCE::toGasStationDTO);
    }

    public GasStation findById(UUID id) {
        return gasStationRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("GasStation not found"));
    }

}
