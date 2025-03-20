package com.fatec.backend;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public record UserDTO(UUID id,
                      String email,
                      String username,
                      @JsonIgnore String password,
                      LocalDate createdAt) {
}
