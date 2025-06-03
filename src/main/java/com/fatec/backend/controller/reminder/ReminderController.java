package com.fatec.backend.controller.reminder;

import com.fatec.backend.DTO.reminder.ReminderDTO;
import com.fatec.backend.model.reminder.Reminder;
import com.fatec.backend.service.reminder.ReminderService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/vehicle/{vehicleId}/reminders")
@RequiredArgsConstructor
public class ReminderController {

    private final ReminderService reminderService;

    @PostMapping("/create-reminder/{userId}")
    public ResponseEntity<UUID> createReminder(@RequestBody ReminderDTO dto,
                                               @PathVariable UUID vehicleId,
                                               @PathVariable UUID userId) {
        UUID id = reminderService.createReminder(dto, vehicleId, userId);
        return ResponseEntity.ok(id);
    }

    @PutMapping("/update-reminder{reminderId}")
    public ResponseEntity<Void> updateReminder(@PathVariable UUID reminderId,
                                               @RequestBody ReminderDTO dto,
                                               @PathVariable UUID vehicleId) {
        reminderService.updateReminder(reminderId, dto, vehicleId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/delete-reminder{reminderId}")
    public ResponseEntity<Void> deleteReminder(@PathVariable UUID reminderId,
                                               @PathVariable UUID vehicleId) {
        reminderService.deleteReminder(reminderId, vehicleId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/find-by-id-reminder/{id}")
    public ResponseEntity<?> findReminderById(@PathVariable UUID id) {
        Optional<Reminder> reminder = Optional.ofNullable(reminderService.findById(id));
        if (reminder.isPresent()) {
            return ResponseEntity.status(HttpStatus.OK).body(reminder);
        }else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Lembrete não encontrado");
        }
    }

    @GetMapping("/list-all-reminders")
    public ResponseEntity<Page<ReminderDTO>> listReminders(@RequestParam int page,
                                                           @RequestParam int size) {
        return ResponseEntity.ok(reminderService.listReminders(PageRequest.of(page, size)));
    }

    @GetMapping("/date-range")
    public ResponseEntity<Page<ReminderDTO>> listByVehicleAndDate(@PathVariable UUID vehicleId,
                                                                  @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
                                                                  @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end,
                                                                  @RequestParam int page,
                                                                  @RequestParam int size) {
        return ResponseEntity.ok(reminderService.listRemindersByVehicleAndDate(vehicleId, start, end, PageRequest.of(page, size)));
    }

    @PostMapping("/check-pending")
    public ResponseEntity<Void> checkAndNotify() {
        reminderService.checkAndProcessPendingReminders();
        return ResponseEntity.ok().build();
    }
}