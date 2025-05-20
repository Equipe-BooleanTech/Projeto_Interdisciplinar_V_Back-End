package com.fatec.backend.controller.reminder;

import com.fatec.backend.DTO.reminder.ReminderDTO;
import com.fatec.backend.service.reminder.ReminderService;
// Assuming a way to get current user's ID, e.g., from Spring Security or a token
// For simplicity, let's assume it's passed or can be retrieved.
// import org.springframework.security.core.annotation.AuthenticationPrincipal;
// import org.springframework.security.oauth2.jwt.Jwt;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.UUID;

@RestController
@RequestMapping("/api/reminders")
@RequiredArgsConstructor
public class ReminderController {

    private final ReminderService reminderService;

    // Placeholder for getting authenticated user ID. In a real app, this would come from security context.
    private UUID getCurrentUserId() {
        // This is a MOCK. Replace with actual user ID retrieval from security context.
        // For example, if using Spring Security with JWT, you might decode the token.
        // Or, if user ID is part of the request path or a custom header, retrieve it there.
        // For now, let's throw an exception to indicate it needs proper implementation.
        // throw new UnsupportedOperationException("Current user ID retrieval not implemented yet. This is a placeholder.");
        // For testing purposes, returning a hardcoded UUID. THIS IS NOT FOR PRODUCTION.
        // You'll need to integrate with your authentication mechanism.
        // Example: return UUID.fromString("some-fixed-user-id-for-testing");
        // For now, we will expect it to be passed if needed or handled by service if it can get it.
        // Let's assume for now the service layer will require it as a parameter if not globally available.
        // This controller will need to be adapted based on how user auth is handled.
        // For the purpose of this generation, we will assume the service layer methods that need userId will receive it.
        // If your service methods are designed to get it from SecurityContextHolder, this method is not needed here.
        System.out.println("Warning: getCurrentUserId() is a placeholder and needs proper implementation based on auth.");
        // A common pattern is to have a @CurrentUser annotation or similar to inject the User object or ID.
        // Or extract from @AuthenticationPrincipal Jwt jwt -> jwt.getSubject()
        return null; // Let service handle or require it explicitly
    }

    @PostMapping
    public ResponseEntity<ReminderDTO> createReminder(@Valid @RequestBody ReminderDTO reminderDTO) {
        // Assuming the userId will be extracted from the security context within the service or passed explicitly.
        // For this example, let's assume a fixed user ID for demonstration or that service handles it.
        // UUID userId = getCurrentUserId(); // This would be the ideal way if implemented.
        // Hardcoding for now, THIS IS BAD PRACTICE FOR PRODUCTION
        UUID mockUserId = UUID.fromString("d0f6e2d9-4f8c-4f8b-9b8b-0e2f0c6f0a1b"); // Example User ID
        ReminderDTO createdReminder = reminderService.createReminder(reminderDTO, mockUserId);
        return new ResponseEntity<>(createdReminder, HttpStatus.CREATED);
    }

    @GetMapping("/user")
    public ResponseEntity<Page<ReminderDTO>> getUserReminders(@PageableDefault(size = 10) Pageable pageable) {
        // UUID userId = getCurrentUserId(); // Ideal
        UUID mockUserId = UUID.fromString("d0f6e2d9-4f8c-4f8b-9b8b-0e2f0c6f0a1b"); // Example User ID
        Page<ReminderDTO> reminders = reminderService.getUserReminders(mockUserId, pageable);
        return ResponseEntity.ok(reminders);
    }

    @GetMapping("/{reminderId}")
    public ResponseEntity<ReminderDTO> getReminderById(@PathVariable UUID reminderId) {
        // UUID userId = getCurrentUserId(); // Ideal
        UUID mockUserId = UUID.fromString("d0f6e2d9-4f8c-4f8b-9b8b-0e2f0c6f0a1b"); // Example User ID
        ReminderDTO reminder = reminderService.getReminderById(reminderId, mockUserId);
        return ResponseEntity.ok(reminder);
    }

    @PutMapping("/{reminderId}")
    public ResponseEntity<ReminderDTO> updateReminder(@PathVariable UUID reminderId, @Valid @RequestBody ReminderDTO reminderDTO) {
        // UUID userId = getCurrentUserId(); // Ideal
        UUID mockUserId = UUID.fromString("d0f6e2d9-4f8c-4f8b-9b8b-0e2f0c6f0a1b"); // Example User ID
        ReminderDTO updatedReminder = reminderService.updateReminder(reminderId, reminderDTO, mockUserId);
        return ResponseEntity.ok(updatedReminder);
    }

    @DeleteMapping("/{reminderId}")
    public ResponseEntity<Void> deleteReminder(@PathVariable UUID reminderId) {
        // UUID userId = getCurrentUserId(); // Ideal
        UUID mockUserId = UUID.fromString("d0f6e2d9-4f8c-4f8b-9b8b-0e2f0c6f0a1b"); // Example User ID
        reminderService.deleteReminder(reminderId, mockUserId);
        return ResponseEntity.noContent().build();
    }

    // Conceptual endpoint for triggering reminder check (not for production scheduler)
    @PostMapping("/check-pending")
    public ResponseEntity<String> checkPendingReminders() {
        reminderService.checkAndProcessPendingReminders();
        return ResponseEntity.ok("Conceptual check for pending reminders initiated. Note: Actual scheduled tasks are disabled in this environment.");
    }
}

