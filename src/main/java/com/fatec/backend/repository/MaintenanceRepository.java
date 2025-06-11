package com.fatec.backend.repository;

import com.fatec.backend.model.vehicle.FuelRefill;
import com.fatec.backend.model.vehicle.Maintenance;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface MaintenanceRepository extends JpaRepository<Maintenance, UUID> {
    Optional<Maintenance> findById(UUID id);
    List<Maintenance> findAllByVehicleIdAndDateBetween(UUID vehicleId, LocalDateTime start, LocalDateTime end);
    Page<Maintenance> findAllByVehicleId(UUID vehicleId, Pageable pageable);
    List<Maintenance> findByVehicleIdAndDateBetween(UUID vehicleId, LocalDateTime start, LocalDateTime end);
    List<Maintenance> findByVehicleIdAndTypeAndDateBetween(UUID vehicleId, Maintenance type, LocalDateTime start, LocalDateTime end);
    }

