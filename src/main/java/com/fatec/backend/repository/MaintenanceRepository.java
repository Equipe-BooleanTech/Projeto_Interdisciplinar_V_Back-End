package com.fatec.backend.repository;

import com.fatec.backend.model.vehicle.Maintenance;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface MaintenanceRepository extends JpaRepository<Maintenance, UUID> {
    Optional<Maintenance> findById(UUID id);
    Page<Maintenance> findByVehicleUuidAndDateBetween(
            UUID vehicleId,
            LocalDateTime start,
            LocalDateTime end,
            Pageable pageable
    );}

