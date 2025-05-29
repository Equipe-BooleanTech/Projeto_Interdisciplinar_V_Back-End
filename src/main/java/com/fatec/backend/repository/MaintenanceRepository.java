package com.fatec.backend.repository;

import com.fatec.backend.model.maintenance.Maintenance;
import com.fatec.backend.model.vehicle.Vehicle;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface MaintenanceRepository extends JpaRepository<Maintenance, UUID> {
    Page<Maintenance> findByVehicle(Vehicle vehicle, Pageable pageable);
    // Add other query methods if needed, e.g., find by vehicle and type, or date range.
}

