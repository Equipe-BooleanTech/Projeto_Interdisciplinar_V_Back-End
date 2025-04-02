package com.fatec.backend.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;
@Table(name="veiculos")
@Entity
@Getter
@Setter
@AllArgsConstructor
@Builder
@Data
@NoArgsConstructor
public class Vehicle {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID uuid;
    @Column(unique = true)
    private String plate;
    private String model;
    private String color;
    private String manufacturer;
    private String type;
    private String description;
    private String year;
    private String Km;
    private String fuelType;
    private Double fuelCapacity;
    private Double fuelConsumption;
}
