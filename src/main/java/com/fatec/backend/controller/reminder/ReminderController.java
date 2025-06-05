package com.fatec.backend.controller.reminder;

import com.fatec.backend.DTO.reminder.ReminderDTO;
import com.fatec.backend.DTO.vehicle.DateRangeDTO;
import com.fatec.backend.DTO.vehicle.FuelRefillDTO;
import com.fatec.backend.DTO.vehicle.TimeSummaryDTO;
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

    @GetMapping("/list-all-reminders/{userId}")
    public ResponseEntity<Page<ReminderDTO>> listReminders(@PathVariable UUID vehicleId,@PathVariable UUID userId,
                                                           @RequestParam(defaultValue = "0") int page,
                                                           @RequestParam(defaultValue = "10") int size) {
        Page<ReminderDTO> reminderDTOS = reminderService.listReminders(userId,vehicleId, PageRequest.of(page, size));
        return new ResponseEntity<>(reminderDTOS, HttpStatus.OK);
    }

    @GetMapping("/list-reminder-by-period")
    public ResponseEntity<?> getByPeriod(
            @RequestBody DateRangeDTO dateRangeDTO,
            @RequestParam(defaultValue = "monthly") String groupingType,
            @PathVariable UUID vehicleId
    ){
        TimeSummaryDTO reminders = reminderService.ListRemindersByDateRange(vehicleId,dateRangeDTO);
        if(reminders.data().isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("NENHUM LEMBRETE ENCONTRADO NESTE PERIODO");
        }else {
            return ResponseEntity.status(HttpStatus.OK).body(reminders);
        }
    }

    @PostMapping("/check-pending")
    public ResponseEntity<Void> checkAndNotify() {
        reminderService.checkAndProcessPendingReminders();
        return ResponseEntity.ok().build();
    }
}