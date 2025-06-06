package com.fatec.backend.service.reminder;

import com.fatec.backend.DTO.reminder.ReminderDTO;
import com.fatec.backend.DTO.vehicle.DateRangeDTO;
import com.fatec.backend.DTO.vehicle.FuelRefillDTO;
import com.fatec.backend.DTO.vehicle.MaintenanceDTO;
import com.fatec.backend.DTO.vehicle.TimeSummaryDTO;
import com.fatec.backend.enums.ReminderStatus;
import com.fatec.backend.mapper.maintenance.MaintenanceMapper;
import com.fatec.backend.mapper.reminder.ReminderMapper;
import com.fatec.backend.mapper.vehicle.FuelRefillMapper;
import com.fatec.backend.model.User;
import com.fatec.backend.model.reminder.Reminder;
import com.fatec.backend.model.vehicle.FuelRefill;
import com.fatec.backend.model.vehicle.Maintenance;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.mail.javamail.JavaMailSender;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import com.fatec.backend.model.vehicle.Vehicle;
import com.fatec.backend.repository.ReminderRepository;
import com.fatec.backend.repository.UserRepository;
import com.fatec.backend.repository.VehicleRespository;
import lombok.RequiredArgsConstructor;
import org.thymeleaf.TemplateEngine;
import org.springframework.mail.javamail.MimeMessageHelper;
import jakarta.mail.internet.MimeMessage;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ReminderService {

    private final ReminderRepository reminderRepository;
    private final UserRepository userRepository;
    private final VehicleRespository vehicleRepository;
    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;

    @Transactional
    public UUID createReminder(ReminderDTO reminderDTO, UUID vehicleId, UUID userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + userId));
        Vehicle vehicle = vehicleRepository.findById(vehicleId)
                .orElseThrow(() -> new IllegalArgumentException("Veículo não encontrado com ID: " + vehicleId));

        Reminder reminder = Reminder.builder()
                .title(reminderDTO.title())
                .description(reminderDTO.description())
                .type(reminderDTO.type())
                .dueDate(reminderDTO.dueDate())
                .antecedenceDays(reminderDTO.antecedenceDays() != null ? reminderDTO.antecedenceDays() : 0) // Default 0 if null
                .status(ReminderStatus.PENDING) // Default status PENDING
                .isRecurring(reminderDTO.isRecurring())
                .vehicle(vehicle)
                .user(user)
                .createdAt(LocalDateTime.now())
                .build();

        return reminderRepository.save(reminder).getUuid();
    }

    public void updateReminder(UUID reminderId, ReminderDTO reminderDTO, UUID vehicleId) {
        Reminder existingReminder = reminderRepository.findById(reminderId)
                .orElseThrow(() -> new IllegalArgumentException("Reminder not found with ID: " + reminderId));

        if (!existingReminder.getVehicle().getId().equals(vehicleId)) {
            throw new SecurityException("Registro de lembrete não pertence ao veículo especificado na URL.");
        }
        if (reminderDTO.vehicleId() != null && !reminderDTO.vehicleId().equals(existingReminder.getVehicle().getId())) {
            throw new IllegalArgumentException("Não é permitido alterar o veículo associado a um registro de lembrete existente.");
        }
        existingReminder.setTitle(reminderDTO.title());
        existingReminder.setDescription(reminderDTO.description());
        existingReminder.setType(reminderDTO.type());
        existingReminder.setDueDate(reminderDTO.dueDate());
        existingReminder.setAntecedenceDays(reminderDTO.antecedenceDays() != null ? reminderDTO.antecedenceDays() : existingReminder.getAntecedenceDays());
        existingReminder.setStatus(reminderDTO.status() != null ? reminderDTO.status() : existingReminder.getStatus());
        existingReminder.setRecurring(reminderDTO.isRecurring());

        Vehicle vehicle = null;
        if (reminderDTO.vehicleId() != null) {
            vehicle = vehicleRepository.findById(reminderDTO.vehicleId())
                    .orElseThrow(() -> new IllegalArgumentException("Vehicle not found with ID: " + reminderDTO.vehicleId()));
        }
        existingReminder.setVehicle(vehicle);

        reminderRepository.save(existingReminder);
    }

    public void deleteReminder(UUID reminderId, UUID vehicleId) {
        Reminder reminder = reminderRepository.findById(reminderId)
                .orElseThrow(() -> new IllegalArgumentException("Registro de lembrete não encontrado com ID: " + reminderId));
        if (!reminder.getVehicle().getId().equals(vehicleId)) {
            throw new SecurityException("Não autorizado a deletar este registro de lembrete ou ele não pertence ao veículo especificado.");
        }
        Vehicle vehicle = reminder.getVehicle();
        vehicleRepository.save(vehicle);

        reminderRepository.deleteById(reminderId);
    }

    public Reminder findById(UUID id) {
        return reminderRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Reminder not found"));
    }
    public Page<ReminderDTO> listReminders(UUID userId,UUID vehicleId, PageRequest pageRequest) {
        return reminderRepository.findAllByUserIdAndVehicleId(userId,vehicleId,pageRequest)
                .map(ReminderMapper.INSTANCE::ToReminderDTO);
    }

    public TimeSummaryDTO ListRemindersByDateRange(UUID vehicleId, DateRangeDTO dateRangeDTO) {
        List<Reminder> data = reminderRepository.findAllByVehicleIdAndCreatedAtBetween(vehicleId,dateRangeDTO.startDate(), dateRangeDTO.endDate());
        List<ReminderDTO> reminderDTOList = data.stream()
                .map(ReminderMapper.INSTANCE::ToReminderDTO)
                .toList();
        return new TimeSummaryDTO(Collections.singletonList(reminderDTOList), reminderDTOList.size());
    }

    public void checkAndProcessPendingReminders() {
        LocalDate today = LocalDate.now();
        List<Reminder> pendingNotifications = reminderRepository
                .findByReminderDateLessThanEqualAndStatus(today, ReminderStatus.PENDING);

        if (pendingNotifications.isEmpty()) {
            System.out.println("No pending reminders to process today.");
            return;
        }

        for (Reminder reminder : pendingNotifications) {
            try {
                sendNotification(reminder);
                reminder.setStatus(ReminderStatus.NOTIFIED);
                reminderRepository.save(reminder);
            } catch (Exception e) {
                System.err.println("Erro ao enviar notificação: " + reminder.getTitle() + " - " + e.getMessage());
            }
        }
        System.out.println("All pending reminders processed.");
    }

    private void sendNotification(Reminder reminder) throws Exception {
        String senderEmail = reminder.getUser() != null ? reminder.getUser().getEmail() : null;
        if (senderEmail == null || senderEmail.isBlank()) {
            throw new IllegalArgumentException("Reminder creator's email is missing.");
        }

        String recipientEmail = reminder.getUser().getEmail();
        if (recipientEmail.isBlank()) {
            throw new IllegalArgumentException("Reminder recipient's email is missing.");
        }

        Context context = new Context();
        context.setVariable("title", reminder.getTitle());
        context.setVariable("dueDate", reminder.getDueDate());

        String htmlBody = templateEngine.process("reminder-email", context);

        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
        helper.setFrom(senderEmail);
        helper.setTo(recipientEmail);
        helper.setSubject("Lembrete: " + reminder.getTitle());
        helper.setText(htmlBody, true);

        mailSender.send(message);
    }
}
