package com.fatec.backend.DTO.vehicle;

public record GasStationDTO(
        String name,
        String address,
        String city,
        String state,
        String description,
        Integer rating
) {
}
