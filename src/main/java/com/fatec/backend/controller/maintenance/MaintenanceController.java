package com.fatec.backend.controller.maintenance;

import com.fatec.backend.DTO.maintenance.MaintenanceDTO;
import com.fatec.backend.response.SuccessResponse; // Importando SuccessResponse
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
@RequestMapping("/api/vehicles/{vehicleId}/maintenances") // Base path por veículo
@RequiredArgsConstructor
public class MaintenanceController {

    private final MaintenanceService maintenanceService;

    /**
     * Cria um novo registro de manutenção para um veículo específico.
     * Retorna o UUID da manutenção criada.
     */
    @PostMapping
    public ResponseEntity<UUID> createMaintenance(@PathVariable UUID vehicleId, @Valid @RequestBody MaintenanceDTO maintenanceDTO) {
        // Validação básica para garantir consistência entre path e body
        if (maintenanceDTO.vehicleId() != null && !maintenanceDTO.vehicleId().equals(vehicleId)) {
            throw new IllegalArgumentException("O ID do veículo no corpo da requisição não corresponde ao ID na URL.");
        }
        MaintenanceDTO createdMaintenance = maintenanceService.createMaintenance(maintenanceDTO, vehicleId);
        return new ResponseEntity<>(createdMaintenance.id(), HttpStatus.CREATED);
    }

    /**
     * Lista os registros de manutenção de um veículo específico com paginação.
     */
    @GetMapping
    public ResponseEntity<Page<MaintenanceDTO>> getVehicleMaintenances(@PathVariable UUID vehicleId, @PageableDefault(size = 10) Pageable pageable) {
        Page<MaintenanceDTO> maintenances = maintenanceService.getVehicleMaintenances(vehicleId, pageable);
        return ResponseEntity.ok(maintenances);
    }

    /**
     * Busca um registro de manutenção específico pelo ID, garantindo que pertence ao veículo da URL.
     */
    @GetMapping("/{maintenanceId}")
    public ResponseEntity<MaintenanceDTO> getMaintenanceById(@PathVariable UUID vehicleId, @PathVariable UUID maintenanceId) {
        MaintenanceDTO maintenance = maintenanceService.getMaintenanceById(maintenanceId, vehicleId);
        return ResponseEntity.ok(maintenance);
    }

    /**
     * Atualiza um registro de manutenção existente.
     * Retorna SuccessResponse em caso de sucesso.
     */
    @PutMapping("/{maintenanceId}")
    public ResponseEntity<SuccessResponse> updateMaintenance(@PathVariable UUID vehicleId,
                                                             @PathVariable UUID maintenanceId,
                                                             @Valid @RequestBody MaintenanceDTO maintenanceDTO) {
        // Validação básica
        if (maintenanceDTO.vehicleId() != null && !maintenanceDTO.vehicleId().equals(vehicleId)) {
            throw new IllegalArgumentException("O ID do veículo no corpo da requisição não corresponde ao ID na URL.");
        }
        maintenanceService.updateMaintenance(maintenanceId, maintenanceDTO, vehicleId);
        SuccessResponse response = new SuccessResponse("Manutenção atualizada com sucesso", maintenanceId);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * Deleta um registro de manutenção existente.
     * Retorna SuccessResponse em caso de sucesso.
     */
    @DeleteMapping("/{maintenanceId}")
    public ResponseEntity<SuccessResponse> deleteMaintenance(@PathVariable UUID vehicleId, @PathVariable UUID maintenanceId) {
        maintenanceService.deleteMaintenance(maintenanceId, vehicleId);
        SuccessResponse response = new SuccessResponse("Manutenção deletada com sucesso", maintenanceId);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
