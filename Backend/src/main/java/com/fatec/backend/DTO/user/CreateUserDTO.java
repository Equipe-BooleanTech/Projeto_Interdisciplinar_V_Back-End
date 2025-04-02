package com.fatec.backend.DTO.user;


import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Column;

import java.util.UUID;


public record CreateUserDTO (
        UUID id,
        String email,
        String username,
        String password,
        String name,
        String lastname,
        String Phone){
}
