package com.fatec.backend.DTO.user;

import jakarta.validation.constraints.Size;

// DTO for updating user information
// Fields are optional, only provided fields will be updated.
public record UserUpdateDTO(
        @Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters")
        String username,

        // For password change, typically a newPassword and currentPassword would be required.
        // Simple password update here for now, consider a more secure flow if needed.
        @Size(min = 8, message = "Password must be at least 8 characters long")
        String password, // New password

        @Size(min = 1, max = 100, message = "Name must be between 1 and 100 characters")
        String name,

        @Size(min = 1, max = 100, message = "Lastname must be between 1 and 100 characters")
        String lastname,

        @Size(min = 10, max = 20, message = "Phone number must be between 10 and 20 characters")
        String phone
) {}

