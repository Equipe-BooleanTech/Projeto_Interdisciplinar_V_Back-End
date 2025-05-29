package com.fatec.backend.service.reminder;

import com.fatec.backend.DTO.reminder.ReminderDTO;
import com.fatec.backend.enums.ReminderStatus; // Importar ReminderStatus
import com.fatec.backend.mapper.reminder.ReminderMapper;
import com.fatec.backend.model.reminder.Reminder;
import com.fatec.backend.model.User;
import com.fatec.backend.model.vehicle.Vehicle;
import com.fatec.backend.repository.ReminderRepository;
import com.fatec.backend.repository.UserRepository;
import com.fatec.backend.repository.VehicleRespository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate; // Importar LocalDate
import java.util.List; // Importar List
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ReminderService {

    private final ReminderRepository reminderRepository;
    private final UserRepository userRepository;
    private final VehicleRespository vehicleRepository;
    private final ReminderMapper reminderMapper;

    @Transactional
    public ReminderDTO createReminder(ReminderDTO reminderDTO, UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + userId));

        Vehicle vehicle = null;
        if (reminderDTO.vehicleId() != null) {
            vehicle = vehicleRepository.findById(reminderDTO.vehicleId())
                    .orElseThrow(() -> new IllegalArgumentException("Vehicle not found with ID: " + reminderDTO.vehicleId()));
            // Validação adicional: garantir que o veículo pertence ao usuário
            if (!vehicle.getUser().getId().equals(userId)) {
                throw new SecurityException("Vehicle does not belong to the user");
            }
        }

        // Usando o padrão Builder para consistência
        Reminder reminder = Reminder.builder()
                .title(reminderDTO.title())
                .description(reminderDTO.description())
                .type(reminderDTO.type())
                .dueDate(reminderDTO.dueDate())
                .antecedenceDays(reminderDTO.antecedenceDays() != null ? reminderDTO.antecedenceDays() : 0) // Default 0 if null
                .status(ReminderStatus.PENDING) // Default status PENDING
                .isRecurring(reminderDTO.isRecurring())
                .user(user)
                .vehicle(vehicle)
                .build();
        // A lógica @PrePersist na entidade Reminder deve calcular reminderDate e createdAt

        Reminder savedReminder = reminderRepository.save(reminder);
        return reminderMapper.toDTO(savedReminder);
    }

    @Transactional(readOnly = true)
    public Page<ReminderDTO> getUserReminders(UUID userId, Pageable pageable) {
        // A busca já filtra por usuário, não precisa buscar o User antes se a query for direta
        // No entanto, manter a busca do User pode ser útil para validação inicial
        if (!userRepository.existsById(userId)) {
            throw new IllegalArgumentException("User not found with ID: " + userId);
        }
        // A query findByUser garante que só retornará lembretes do usuário correto.
        return reminderRepository.findByUserId(userId, pageable).map(reminderMapper::toDTO);
    }

    @Transactional(readOnly = true)
    public ReminderDTO getReminderById(UUID reminderId, UUID userId) {
        Reminder reminder = reminderRepository.findById(reminderId)
                .orElseThrow(() -> new IllegalArgumentException("Reminder not found with ID: " + reminderId));
        // Validação para garantir que o lembrete pertence ao usuário
        if (!reminder.getUser().getId().equals(userId)) {
            throw new SecurityException("User not authorized to access this reminder");
        }
        return reminderMapper.toDTO(reminder);
    }

    @Transactional
    public ReminderDTO updateReminder(UUID reminderId, ReminderDTO reminderDTO, UUID userId) {
        Reminder existingReminder = reminderRepository.findById(reminderId)
                .orElseThrow(() -> new IllegalArgumentException("Reminder not found with ID: " + reminderId));

        // Validação para garantir que o lembrete pertence ao usuário
        if (!existingReminder.getUser().getId().equals(userId)) {
            throw new SecurityException("User not authorized to update this reminder");
        }

        // Atualiza os campos permitidos
        existingReminder.setTitle(reminderDTO.title());
        existingReminder.setDescription(reminderDTO.description());
        existingReminder.setType(reminderDTO.type());
        existingReminder.setDueDate(reminderDTO.dueDate());
        existingReminder.setAntecedenceDays(reminderDTO.antecedenceDays() != null ? reminderDTO.antecedenceDays() : existingReminder.getAntecedenceDays());
        existingReminder.setStatus(reminderDTO.status() != null ? reminderDTO.status() : existingReminder.getStatus());
        existingReminder.setRecurring(reminderDTO.isRecurring());

        // Atualiza o veículo associado, se informado
        Vehicle vehicle = null;
        if (reminderDTO.vehicleId() != null) {
            vehicle = vehicleRepository.findById(reminderDTO.vehicleId())
                    .orElseThrow(() -> new IllegalArgumentException("Vehicle not found with ID: " + reminderDTO.vehicleId()));
            // Validação adicional: garantir que o veículo pertence ao usuário
            if (!vehicle.getUser().getId().equals(userId)) {
                throw new SecurityException("Vehicle does not belong to the user");
            }
        }
        existingReminder.setVehicle(vehicle);

        // A lógica @PreUpdate na entidade Reminder deve atualizar reminderDate e updatedAt
        Reminder updatedReminder = reminderRepository.save(existingReminder);
        return reminderMapper.toDTO(updatedReminder);
    }

    @Transactional
    public void deleteReminder(UUID reminderId, UUID userId) {
        Reminder reminder = reminderRepository.findById(reminderId)
                .orElseThrow(() -> new IllegalArgumentException("Reminder not found with ID: " + reminderId));
        // Validação para garantir que o lembrete pertence ao usuário
        if (!reminder.getUser().getId().equals(userId)) {
            throw new SecurityException("User not authorized to delete this reminder");
        }
        reminderRepository.deleteById(reminderId);
    }

    // Lógica conceitual para notificação - scheduler real é externo
    public void checkAndProcessPendingReminders() {
        LocalDate today = LocalDate.now();
        // Busca lembretes PENDING cuja data de lembrete (calculada) é hoje ou anterior
        List<Reminder> pendingNotifications = reminderRepository.findByReminderDateLessThanEqualAndStatus(today, ReminderStatus.PENDING);

        if (pendingNotifications.isEmpty()) {
            System.out.println("No pending reminders to process today.");
            return;
        }

        System.out.println("Processing " + pendingNotifications.size() + " pending reminders...");
        for (Reminder reminder : pendingNotifications) {
            System.out.println("Sending notification for reminder: " + reminder.getTitle() + " (Due: " + reminder.getDueDate() + ")");
            // Aqui entraria a lógica real de envio de notificação (email, push, etc.)

            // Opcional: Atualizar status para NOTIFIED após envio (se aplicável)
            // reminder.setStatus(ReminderStatus.NOTIFIED);
            // reminderRepository.save(reminder);
        }
        System.out.println("Conceptual check for pending reminders finished. Actual scheduled tasks are disabled in this environment.");
    }
}
