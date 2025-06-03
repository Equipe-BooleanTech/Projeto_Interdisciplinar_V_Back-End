package com.fatec.backend.repository;

import com.fatec.backend.model.vehicle.FuelRefill;
import com.fatec.backend.model.vehicle.Vehicle;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface FuelRefillRepository extends JpaRepository<FuelRefill, UUID> {
    Optional<FuelRefill> findById(UUID id);
    Optional<FuelRefill> findTopByVehicleOrderByRefillDateDesc(Vehicle vehicle);
    Page<FuelRefill> findByRefillDateBetween(LocalDateTime start, LocalDateTime end, Pageable pageable);
    Page<FuelRefill> findByVehicleUuidAndRefillDateBetween(UUID uuid, LocalDateTime start, LocalDateTime end, Pageable pageable);
    Page<FuelRefill> findByVehicleUuidAndRefillDateBetweenAndFuelType(UUID uuid, LocalDateTime start, LocalDateTime end, String fuelType, Pageable pageable);
}
