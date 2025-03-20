package com.fatec.backend;

import jakarta.persistence.*;
        import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Entity
@Getter
@Setter
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private UUID id;


    @Column(unique = true, nullable = false)
    private String username;

    private String password;

    private String email;
}
