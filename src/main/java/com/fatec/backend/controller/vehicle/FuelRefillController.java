package com.fatec.backend.controller.vehicle;



import com.fatec.backend.DTO.vehicle.FuelRefillDTO;

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


import java.util.List;
import java.util.Optional;
import java.util.UUID;
//TODO ajeitar o list all que nao esta retornando os ids do carro o usuario
@RestController
@RequestMapping("/api/vehicle/fuel-refill")
@RequiredArgsConstructor
public class FuelRefillController {

    private final FuelRefillService fuelRefillService;
    private final FuelRefillRepository fuelRefillRepository;

    @PostMapping("/new-fuel-refill/{vehicleID}/{stationId}")
    public ResponseEntity<UUID> create(@PathVariable UUID vehicleID,@PathVariable UUID stationId ,@RequestBody FuelRefillDTO dto) {
        UUID id = fuelRefillService.registerFuelRefill(vehicleID, stationId, dto);
        return ResponseEntity.ok(id);
    }

    @GetMapping("/list-all-fuel-refill")
    public ResponseEntity<Page<FuelRefillDTO>> listAllFuelRefill(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {
        Page<FuelRefillDTO> fuelRefills = fuelRefillService.getFuelRefills(PageRequest.of(page, size));
        return new ResponseEntity<>(fuelRefills, HttpStatus.FOUND);
    }

    @PutMapping("/update-fuel-refill/{id}")
    public ResponseEntity<?> update(@PathVariable UUID id, @RequestBody FuelRefillDTO dto) {
        fuelRefillService.updateFuelRefill(id, dto);
        return ResponseEntity.status(HttpStatus.OK).body("Atualizado com sucesso!\n" + id);
    }

    @DeleteMapping("/delete-refill/{id}")
    public ResponseEntity<?> delete(@PathVariable UUID id) {
        fuelRefillService.deleteFuelRefill(id);
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(id+"\nDeletado com sucesso!");
    }

    @GetMapping("/find-by-id-fuel-refill/{id}")
    public ResponseEntity<?> findFuelRefillById(@PathVariable UUID id) {
        Optional<FuelRefill> fuelRefill = Optional.ofNullable(fuelRefillService.getFuelRefill(id));
        if (fuelRefill.isPresent()) {
            return ResponseEntity.status(HttpStatus.FOUND).body(fuelRefill);
        }else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Abastecimento não encontrado");
        }
    }
}
