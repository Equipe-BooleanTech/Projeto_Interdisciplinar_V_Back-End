package com.fatec.backend.model;

import com.fatec.backend.model.vehicle.Vehicle;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;
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
    private String birthdate;
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Vehicle> vehicles;
}
