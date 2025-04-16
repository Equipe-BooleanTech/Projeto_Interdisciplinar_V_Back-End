package com.fatec.backend.repository;

import com.fatec.backend.model.vehicle.FuelRefill;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface FuelRefillRepository extends JpaRepository<FuelRefill, UUID> {
}
