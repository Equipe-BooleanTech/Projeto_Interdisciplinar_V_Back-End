package com.fatec.backend.repository;

import com.fatec.backend.model.vehicle.FuelRefill;
import com.fatec.backend.model.vehicle.Vehicle;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface FuelRefillRepository extends JpaRepository<FuelRefill, UUID> {
    Optional<FuelRefill> findTopByVehicleAndKmAtRefillLessThanOrderByKmAtRefillDesc(Vehicle vehicle, Double kmAtRefill);
}
