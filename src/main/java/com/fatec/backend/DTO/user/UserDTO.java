package com.fatec.backend.DTO.user;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.time.LocalDate;
import java.util.UUID;

public record UserDTO(UUID id,
                      String email,
                      String username,
                      @JsonIgnore String password,
                      LocalDate createdAt,
                      byte[] image) {
}
