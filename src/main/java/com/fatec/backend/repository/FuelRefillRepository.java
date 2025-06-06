package com.fatec.backend.repository;

import com.fatec.backend.model.vehicle.FuelRefill;
import com.fatec.backend.model.vehicle.Maintenance;
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
    List<FuelRefill> findAllByVehicleIdAndRefillDateBetween(UUID vehicleId, LocalDateTime start, LocalDateTime end);
    Page<FuelRefill> findAllByVehicleId(UUID vehicleId, Pageable pageable);

}
