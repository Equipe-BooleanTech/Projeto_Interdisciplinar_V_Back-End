package com.fatec.backend.DTO;

import com.fatec.backend.model.User;

import java.util.UUID;

// DTO para resposta ao buscar detalhes do usuário
record UserResponseDTO(UUID id, String fullName, String username, String email, String phone) {
    public static UserResponseDTO fromUser(User user) {
        // Tratamento para evitar NullPointerException se name ou lastname forem nulos
        String name = user.getName() != null ? user.getName() : "";
        String lastname = user.getLastname() != null ? user.getLastname() : "";
        String fullName = (name + " " + lastname).trim();
        return new UserResponseDTO(user.getId(), fullName, user.getUsername(), user.getEmail(), user.getPhone());
    }
}
