package com.fatec.backend.repository;

import com.fatec.backend.model.reminder.Reminder;
import com.fatec.backend.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Repository
public interface ReminderRepository extends JpaRepository<Reminder, UUID> {
    Page<Reminder> findByUser(User user, Pageable pageable);
    // Potentially add methods to find reminders by status, type, vehicle, or due date ranges
    // List<Reminder> findByReminderDateAndStatus(LocalDate reminderDate, ReminderStatus status);
}

