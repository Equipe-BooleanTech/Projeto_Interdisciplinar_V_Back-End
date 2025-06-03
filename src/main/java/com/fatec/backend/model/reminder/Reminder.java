package com.fatec.backend.model.reminder;

import com.fatec.backend.enums.ReminderStatus;
import com.fatec.backend.enums.ReminderType;
import com.fatec.backend.model.User;
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
@Table(name = "lembretes")
public class Reminder {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID uuid;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
    @ManyToOne
    @JoinColumn(name = "vehicle_id")
    private Vehicle vehicle;

    @Column(nullable = false)
    private String title;

    @Lob
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ReminderType type;

    @Column(nullable = false)
    private LocalDate dueDate; // Data de vencimento/realização

    private LocalDate reminderDate; // Data para o sistema notificar (calculada)

    private Integer antecedenceDays; // Dias de antecedência para notificação (default 7)

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ReminderStatus status = ReminderStatus.PENDING;

    private boolean isRecurring = false;
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (antecedenceDays == null) {
            antecedenceDays = 7; // Default antecedence
        }
        if (dueDate != null && reminderDate == null) {
            reminderDate = dueDate.minusDays(antecedenceDays);
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
        if (dueDate != null && antecedenceDays != null) { // Recalculate reminderDate if dueDate or antecedenceDays change
            reminderDate = dueDate.minusDays(antecedenceDays);
        }
    }
}

