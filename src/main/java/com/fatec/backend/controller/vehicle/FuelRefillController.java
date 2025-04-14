package com.fatec.backend.controller.vehicle;



import com.fatec.backend.DTO.vehicle.FuelRefillDTO;
import com.fatec.backend.DTO.vehicle.FuelRefillSummaryDTO;
import com.fatec.backend.model.vehicle.FuelRefill;
import com.fatec.backend.repository.FuelRefillRepository;

import com.fatec.backend.service.vehicle.FuelRefillService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/vehicle/fuel-refill")
@RequiredArgsConstructor
public class FuelRefillController {

    private final FuelRefillService fuelRefillService;
    private final FuelRefillRepository fuelRefillRepository;

    @PostMapping("/new-fuel-refill")
    public ResponseEntity<UUID> create(@RequestBody FuelRefillDTO dto) {
        UUID id = fuelRefillService.registerFuelRefill(dto);
        return ResponseEntity.ok(id);
    }

    @GetMapping("/list-all")
    public ResponseEntity<List<FuelRefill>> list() {
        return ResponseEntity.ok(fuelRefillRepository.findAll());
    }

    @GetMapping("/vehicle/{vehicleId}")
    public ResponseEntity<FuelRefillSummaryDTO> getByVehicle(@PathVariable UUID vehicleId, Pageable pageable) {
        return ResponseEntity.ok(fuelRefillService.getRefillsByVehicle(vehicleId, pageable));
    }

    @GetMapping("/vehicle/{vehicleId}/period")
    public ResponseEntity<FuelRefillSummaryDTO> getByVehicleAndPeriod(
            @PathVariable UUID vehicleId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end,
            Pageable pageable
    ) {
        return ResponseEntity.ok(fuelRefillService.getRefillsByVehicleAndPeriod(vehicleId, start, end, pageable));
    }

    @DeleteMapping("/delete-refill/{refillId}")
    public ResponseEntity<UUID> delete(@PathVariable UUID refillId) {
        fuelRefillService.deleteFuelRefill(refillId);
        return ResponseEntity.ok(refillId);
    }
}
