package com.fatec.backend.controller.vehicle;



import com.fatec.backend.DTO.vehicle.DateRangeDTO;
import com.fatec.backend.DTO.vehicle.FuelRefillDTO;

import com.fatec.backend.DTO.vehicle.TimeSummaryDTO;
import com.fatec.backend.model.vehicle.FuelRefill;
import com.fatec.backend.model.vehicle.GasStation;
import com.fatec.backend.repository.FuelRefillRepository;

import com.fatec.backend.service.vehicle.FuelRefillService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;


import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
//TODO ajeitar o list all que nao esta retornando os ids do carro o usuário
@RestController
@RequestMapping("/api/vehicle/fuel-refill")
@RequiredArgsConstructor
public class FuelRefillController {

    private final FuelRefillService fuelRefillService;

    @PostMapping("/new-fuel-refill/{vehicleID}/{stationId}")
    public ResponseEntity<UUID> create(@PathVariable UUID vehicleID, @PathVariable UUID stationId, @RequestBody FuelRefillDTO dto) {
        UUID id = fuelRefillService.createFuelRefill(dto, vehicleID, stationId);
        return ResponseEntity.ok(id);
    }

    @GetMapping("/list-all-fuel-refill/{vehicleId}")
    public ResponseEntity<Page<FuelRefillDTO>> listVehicleFuelRefills(
            @PathVariable UUID vehicleId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Page<FuelRefillDTO> fuelRefills = fuelRefillService.listRefills(vehicleId, PageRequest.of(page, size));
        return new ResponseEntity<>(fuelRefills, HttpStatus.OK);
    }

    @PutMapping("/update-fuel-refill/{fuelRefillId}/{vehicleID}")
    public ResponseEntity<?> update(@PathVariable UUID fuelRefillId, @RequestBody FuelRefillDTO dto, @PathVariable UUID vehicleID) {
        fuelRefillService.updateFuelRefill(fuelRefillId, dto, vehicleID);
        return ResponseEntity.status(HttpStatus.OK).body("Atualizado com sucesso!\n" + fuelRefillId);
    }

    @DeleteMapping("/delete-refill/{id}/{vehicleID}")
    public ResponseEntity<?> delete(@PathVariable UUID id, @PathVariable UUID vehicleID) {
        fuelRefillService.deleteFuelRefill(id, vehicleID);
        return ResponseEntity.status(HttpStatus.OK).body(id + "\nDeletado com sucesso!");
    }

    @GetMapping("/find-by-id-fuel-refill/{id}")
    public ResponseEntity<?> findFuelRefillById(@PathVariable UUID id) {
        Optional<FuelRefill> fuelRefill = Optional.ofNullable(fuelRefillService.findById(id));
        if (fuelRefill.isPresent()) {
            return ResponseEntity.status(HttpStatus.OK).body(fuelRefill);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Abastecimento não encontrado");
        }
    }

    @GetMapping("/list-fuel-refill-by-period/{vehicleId}")
    public ResponseEntity<?> getByPeriod(
            @RequestBody DateRangeDTO dateRangeDTO,
            @RequestParam(defaultValue = "monthly") String groupingType,
            @PathVariable UUID vehicleId
    ) {
        TimeSummaryDTO refills = fuelRefillService.listFuelRefillByDateRangeAndVehicle(vehicleId, dateRangeDTO);
        if (refills.data().isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("NENHUM ABASTECIMENTO ENCONTRADO NESTE PERIODO");
        } else {
            return ResponseEntity.status(HttpStatus.OK).body(refills);
        }
    }
}
