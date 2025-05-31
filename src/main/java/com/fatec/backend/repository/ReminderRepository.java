package com.fatec.backend.repository;

import com.fatec.backend.enums.ReminderStatus;
import com.fatec.backend.model.reminder.Reminder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface ReminderRepository extends JpaRepository<Reminder, UUID> {

    List<Reminder> findByReminderDateLessThanEqualAndStatus(LocalDate today, ReminderStatus reminderStatus);

   Page<Reminder> findByVehicleUuidAndReminderDateBetween(UUID uuid, LocalDateTime startDate, LocalDateTime endDate, PageRequest pageRequest);
}

