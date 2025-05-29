package com.fatec.backend.model.maintenance;

import com.fatec.backend.model.vehicle.Vehicle;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "maintenances")
public class Maintenance {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vehicle_id", nullable = false)
    private Vehicle vehicle;

    @Column(nullable = false)
    private LocalDate date; // Data da manutenção

    @Column(nullable = false)
    private Double odometer; // Quilometragem no momento da manutenção

    @Column(nullable = false)
    private String type; // Tipo de manutenção (ex: "Troca de Óleo", "Revisão")

    @Lob
    private String description; // Descrição detalhada dos serviços/peças

    private Double cost; // Custo total da manutenção

    private String workshopName; // Nome da oficina (opcional)

    // Campos para registrar a próxima manutenção recomendada (opcional)
    private LocalDate nextDueDate;
    private Double nextDueOdometer;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}

