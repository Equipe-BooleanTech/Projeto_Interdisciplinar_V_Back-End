package com.fatec.backend.DTO.auth;
import java.util.UUID;

public record LoginResponseDTO(UUID id,
                               String fullName,
                               String token) {
}