package com.fatec.backend;


import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.List;
import java.util.UUID;


public record CreateUserDTO (
        String email,
        String username,
        @JsonIgnore String password){
}
