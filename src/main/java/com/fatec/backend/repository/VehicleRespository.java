package com.fatec.backend.repository;

import com.fatec.backend.model.vehicle.Vehicle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;
@Repository
public interface VehicleRespository extends JpaRepository<Vehicle, UUID> {
    Optional<Vehicle> findByPlate(String plate);

}

