package com.fatec.backend.DTO.vehicle;

import java.util.UUID;

public record GasStationDTO(
        UUID id,
        String name,
        String address,
        String city,
        String state,
        String description,
        Integer rating
) {
}
