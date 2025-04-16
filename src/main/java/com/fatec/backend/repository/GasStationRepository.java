package com.fatec.backend.repository;

import com.fatec.backend.model.vehicle.GasStation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface GasStationRepository extends JpaRepository<GasStation, UUID> {
}
