package com.fatec.backend;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
class LoginResponseDTO {
    private UUID userId;
    private String username;
    private String token;

    public LoginResponseDTO(UUID userId, String username, String token) {
        this.userId = userId;
        this.username = username;
        this.token = token;
    }
}
