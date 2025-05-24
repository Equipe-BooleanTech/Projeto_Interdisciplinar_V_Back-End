package com.fatec.backend.service.reminder;

import com.fatec.backend.DTO.reminder.ReminderDTO;
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

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ReminderService {

    private final ReminderRepository reminderRepository;
    private final UserRepository userRepository; // Assuming you have a UserRepository
    private final VehicleRespository vehicleRepository; // Assuming you have a VehicleRepository
    private final ReminderMapper reminderMapper;

    @Transactional
    public ReminderDTO createReminder(ReminderDTO reminderDTO, UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + userId));

        Vehicle vehicle = null;
        if (reminderDTO.vehicleId() != null) {
            vehicle = vehicleRepository.findById(reminderDTO.vehicleId())
                    .orElseThrow(() -> new IllegalArgumentException("Vehicle not found with ID: " + reminderDTO.vehicleId()));
        }

        Reminder reminder = reminderMapper.toEntity(reminderDTO);
        reminder.setUser(user);
        reminder.setVehicle(vehicle); // Can be null

        Reminder savedReminder = reminderRepository.save(reminder);
        return reminderMapper.toDTO(savedReminder);
    }

    @Transactional(readOnly = true)
    public Page<ReminderDTO> getUserReminders(UUID userId, Pageable pageable) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + userId));
        return reminderRepository.findByUser(user, pageable).map(reminderMapper::toDTO);
    }

    @Transactional(readOnly = true)
    public ReminderDTO getReminderById(UUID reminderId, UUID userId) {
        Reminder reminder = reminderRepository.findById(reminderId)
                .orElseThrow(() -> new IllegalArgumentException("Reminder not found with ID: " + reminderId));
        if (!reminder.getUser().getId().equals(userId)) {
            throw new SecurityException("User not authorized to access this reminder");
        }
        return reminderMapper.toDTO(reminder);
    }

    @Transactional
    public ReminderDTO updateReminder(UUID reminderId, ReminderDTO reminderDTO, UUID userId) {
        Reminder existingReminder = reminderRepository.findById(reminderId)
                .orElseThrow(() -> new IllegalArgumentException("Reminder not found with ID: " + reminderId));

        if (!existingReminder.getUser().getId().equals(userId)) {
            throw new SecurityException("User not authorized to update this reminder");
        }

        // Update fields from DTO
        existingReminder.setTitle(reminderDTO.title());
        existingReminder.setDescription(reminderDTO.description());
        existingReminder.setType(reminderDTO.type());
        existingReminder.setDueDate(reminderDTO.dueDate());
        existingReminder.setAntecedenceDays(reminderDTO.antecedenceDays() != null ? reminderDTO.antecedenceDays() : existingReminder.getAntecedenceDays());
        existingReminder.setStatus(reminderDTO.status() != null ? reminderDTO.status() : existingReminder.getStatus());
        existingReminder.setRecurring(reminderDTO.isRecurring());

        if (reminderDTO.vehicleId() != null) {
            Vehicle vehicle = vehicleRepository.findById(reminderDTO.vehicleId())
                    .orElseThrow(() -> new IllegalArgumentException("Vehicle not found with ID: " + reminderDTO.vehicleId()));
            existingReminder.setVehicle(vehicle);
        } else {
            existingReminder.setVehicle(null);
        }
        // PreUpdate in Reminder entity will handle reminderDate and updatedAt
        Reminder updatedReminder = reminderRepository.save(existingReminder);
        return reminderMapper.toDTO(updatedReminder);
    }

    @Transactional
    public void deleteReminder(UUID reminderId, UUID userId) {
        Reminder reminder = reminderRepository.findById(reminderId)
                .orElseThrow(() -> new IllegalArgumentException("Reminder not found with ID: " + reminderId));
        if (!reminder.getUser().getId().equals(userId)) {
            throw new SecurityException("User not authorized to delete this reminder");
        }
        reminderRepository.deleteById(reminderId);
    }

    // Conceptual logic for notification - actual cron job/scheduler is external
    public void checkAndProcessPendingReminders() {
        // This method would be conceptually called by a scheduler.
        // LocalDate today = LocalDate.now();
        // List<Reminder> pendingNotifications = reminderRepository.findByReminderDateAndStatus(today, ReminderStatus.PENDING);
        // for (Reminder reminder : pendingNotifications) {
        //     System.out.println("Sending notification for reminder: " + reminder.getTitle());
        //     // Actual notification logic (email, push, etc.) would go here.
        //     // Potentially update reminder status if it's a one-time notification sent.
        // }
        System.out.println("Conceptual check for pending reminders. Actual scheduled tasks are disabled in this environment.");
    }
}
