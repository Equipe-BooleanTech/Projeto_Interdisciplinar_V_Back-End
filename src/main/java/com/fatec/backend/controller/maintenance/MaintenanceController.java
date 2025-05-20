package com.fatec.backend.controller.maintenance;

import com.fatec.backend.DTO.maintenance.MaintenanceDTO;
import com.fatec.backend.service.maintenance.MaintenanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.UUID;

@RestController
@RequestMapping("/api/vehicles/{vehicleId}/maintenances") // Base path per vehicle
@RequiredArgsConstructor
public class MaintenanceController {

    private final MaintenanceService maintenanceService;

    @PostMapping
    public ResponseEntity<MaintenanceDTO> createMaintenance(@PathVariable UUID vehicleId, @Valid @RequestBody MaintenanceDTO maintenanceDTO) {
        // Ensure DTO's vehicleId matches path variable or handle appropriately
        if (maintenanceDTO.vehicleId() != null && !maintenanceDTO.vehicleId().equals(vehicleId)) {
            // Or you could ignore DTO's vehicleId and always use the one from the path
            return ResponseEntity.badRequest().build(); // Or throw an exception
        }
        // If DTO's vehicleId is null, we can set it from path, or ensure service does.
        // For simplicity, assuming service will use vehicleId from path if DTO's is null or matches.
        MaintenanceDTO createdMaintenance = maintenanceService.createMaintenance(maintenanceDTO, vehicleId);
        return new ResponseEntity<>(createdMaintenance, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<Page<MaintenanceDTO>> getVehicleMaintenances(@PathVariable UUID vehicleId, @PageableDefault(size = 10) Pageable pageable) {
        Page<MaintenanceDTO> maintenances = maintenanceService.getVehicleMaintenances(vehicleId, pageable);
        return ResponseEntity.ok(maintenances);
    }

    @GetMapping("/{maintenanceId}")
    public ResponseEntity<MaintenanceDTO> getMaintenanceById(@PathVariable UUID vehicleId, @PathVariable UUID maintenanceId) {
        MaintenanceDTO maintenance = maintenanceService.getMaintenanceById(maintenanceId, vehicleId);
        return ResponseEntity.ok(maintenance);
    }

    @PutMapping("/{maintenanceId}")
    public ResponseEntity<MaintenanceDTO> updateMaintenance(@PathVariable UUID vehicleId,
                                                            @PathVariable UUID maintenanceId,
                                                            @Valid @RequestBody MaintenanceDTO maintenanceDTO) {
        // Ensure DTO's vehicleId matches path variable or handle appropriately
        if (maintenanceDTO.vehicleId() != null && !maintenanceDTO.vehicleId().equals(vehicleId)) {
            return ResponseEntity.badRequest().build();
        }
        MaintenanceDTO updatedMaintenance = maintenanceService.updateMaintenance(maintenanceId, maintenanceDTO, vehicleId);
        return ResponseEntity.ok(updatedMaintenance);
    }

    @DeleteMapping("/{maintenanceId}")
    public ResponseEntity<Void> deleteMaintenance(@PathVariable UUID vehicleId, @PathVariable UUID maintenanceId) {
        maintenanceService.deleteMaintenance(maintenanceId, vehicleId);
        return ResponseEntity.noContent().build();
    }
}

