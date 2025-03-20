package com.fatec.backend;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
class LoginUserDTO {
    @NotBlank(message = "O email não pode estar vazio")
    @Email(message = "Formato de email inválido")
    private String email;

    @NotBlank(message = "A senha não pode estar vazia")
    private String password;

    @NotBlank(message="O campo username não pode estar vazio")
    private String username;
}
