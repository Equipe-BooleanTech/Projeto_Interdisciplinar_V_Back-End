package com.fatec.backend.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Table(name="usuarios")
@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;
    private String email;
    @Column(unique = true, nullable = false)
    private String username;
    private String password;
    private String name;
    private String lastname;
    private String Phone;

}
