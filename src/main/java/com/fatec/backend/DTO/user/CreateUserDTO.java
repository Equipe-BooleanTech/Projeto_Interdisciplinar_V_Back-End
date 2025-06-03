package com.fatec.backend.DTO.user;

import com.fatec.backend.DTO.vehicle.VehicleDTO;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record CreateUserDTO (
        UUID id,
        String email,
        String username,
        String password,
        String name,
        String lastname,
        String phone,
        String birthdate,
        List<VehicleDTO> vehicles,
        LocalDateTime createdAt
) {
}