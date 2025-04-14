package com.fatec.backend.model.vehicle;

import com.fatec.backend.model.User.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.util.UUID;

@Table(name = "veiculos")
@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Vehicle {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID uuid;

    @NotBlank(message = "A placa é obrigatória")
    private String plate;
    private String model;
    private String color;
    private String manufacturer;
    private String type;
    private String description;
    private String year;

    @Column(name = "km")
    private Double km;
    private String fuelType;
    private Double fuelCapacity;
    private Double fuelConsumption;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
}
