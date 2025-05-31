package com.fatec.backend.model.vehicle;

import com.fatec.backend.enums.FuelType;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "abastecimento")
public class FuelRefill {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @ManyToOne
    private Vehicle vehicle;

    @ManyToOne
    private GasStation station;

    private double liters;
    private double pricePerLiter;
    private double totalCost;
    private double kmAtRefill;
    private Boolean isCompleteTank;
    @Enumerated(EnumType.STRING)
    private FuelType fuelType;
    private LocalDateTime refillDate;

}
