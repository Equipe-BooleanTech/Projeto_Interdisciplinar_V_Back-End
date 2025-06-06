package com.fatec.backend.controller.vehicle;

import com.fatec.backend.DTO.vehicle.DateRangeDTO;
import com.fatec.backend.DTO.vehicle.MaintenanceDTO;
import com.fatec.backend.DTO.vehicle.TimeSummaryDTO;
import com.fatec.backend.model.vehicle.Maintenance;
import com.fatec.backend.response.SuccessResponse; // Importando SuccessResponse
import com.fatec.backend.service.vehicle.MaintenanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/vehicles/{vehicleId}/maintenances")
@RequiredArgsConstructor
public class MaintenanceController {

    private final MaintenanceService maintenanceService;

    @PostMapping("/create-maitenance")
    public ResponseEntity<UUID> createMaintenance(@PathVariable UUID vehicleId, @Valid @RequestBody MaintenanceDTO maintenanceDTO) {
        if (maintenanceDTO.vehicleId() != null && !maintenanceDTO.vehicleId().equals(vehicleId)) {
            throw new IllegalArgumentException("O ID do veículo no corpo da requisição não corresponde ao ID na URL.");
        }
        MaintenanceDTO createdMaintenance = maintenanceService.createMaintenance(maintenanceDTO, vehicleId);
        return new ResponseEntity<>(createdMaintenance.id(), HttpStatus.OK);
    }


    @GetMapping("/list-all-maintenances")
    public ResponseEntity<Page<MaintenanceDTO>> listAllMaintenances(@PathVariable UUID vehicleId,@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {
        Page<MaintenanceDTO> maintenances = maintenanceService.listMaintenances(vehicleId,PageRequest.of(page, size));
        return new ResponseEntity<>(maintenances, HttpStatus.OK);
    }

    @GetMapping("/find-by-id-maintenance/{id}")
    public ResponseEntity<?> findMaintenanceById(@PathVariable UUID id) {
        Optional<Maintenance> maintenance = Optional.ofNullable(maintenanceService.findById(id));
        if (maintenance.isPresent()) {
            return ResponseEntity.status(HttpStatus.OK).body(maintenance);
        }else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Abastecimento não encontrado");
        }
    }



    @PutMapping("/update-maintenance/{maintenanceId}")
    public ResponseEntity<SuccessResponse> updateMaintenance(@PathVariable UUID vehicleId,
                                                             @PathVariable UUID maintenanceId,
                                                             @Valid @RequestBody MaintenanceDTO maintenanceDTO) {
        if (maintenanceDTO.vehicleId() != null && !maintenanceDTO.vehicleId().equals(vehicleId)) {
            throw new IllegalArgumentException("O ID do veículo no corpo da requisição não corresponde ao ID na URL.");
        }
        maintenanceService.updateMaintenance(maintenanceId, maintenanceDTO, vehicleId);
        SuccessResponse response = new SuccessResponse("Manutenção atualizada com sucesso", maintenanceId);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }


    @DeleteMapping("/delete-maintenance/{maintenanceId}")
    public ResponseEntity<SuccessResponse> deleteMaintenance(@PathVariable UUID vehicleId, @PathVariable UUID maintenanceId) {
        maintenanceService.deleteMaintenance(maintenanceId, vehicleId);
        SuccessResponse response = new SuccessResponse("Manutenção deletada com sucesso", maintenanceId);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/list-maintenance-by-period")
    public ResponseEntity<?> getByPeriod(
            @RequestBody DateRangeDTO dateRangeDTO,
            @RequestParam(defaultValue = "monthly") String groupingType,
            @PathVariable UUID vehicleId
    ){
        TimeSummaryDTO maintenances = maintenanceService.ListMaintenanceByDateRange(vehicleId,dateRangeDTO);
        if(maintenances.data().isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("NENHUMA MANUTENÇÃO ENCONTRADA NESTE PERIODO");
        }else {
            return ResponseEntity.status(HttpStatus.OK).body(maintenances);
        }
    }


}
