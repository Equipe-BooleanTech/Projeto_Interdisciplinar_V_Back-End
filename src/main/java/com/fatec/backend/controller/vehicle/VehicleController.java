package com.fatec.backend.controller.vehicle;

import com.fatec.backend.DTO.vehicle.VehicleDTO;
import com.fatec.backend.model.vehicle.Vehicle;
import com.fatec.backend.response.SuccessResponse;
import com.fatec.backend.service.vehicle.VehicleService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.util.UUID;

@AllArgsConstructor
@RestController
@RequestMapping("/api/vehicle")
public class VehicleController {

    private final VehicleService vehicleService;

    @PostMapping("/create-vehicle/{id}")
    public ResponseEntity<UUID> createVehicle(@PathVariable UUID id, @RequestBody VehicleDTO vehicleDTO) {
        System.out.println("Recebido JSON: " + vehicleDTO);
        UUID vehicleId = vehicleService.createVehicle(vehicleDTO, id);
        return ResponseEntity.ok(vehicleId);
    }

    @PutMapping("/update-vehicle/{id}")
    public ResponseEntity<SuccessResponse> updateVehicle(@PathVariable UUID id, @RequestBody VehicleDTO vehicleDTO) {
        vehicleService.updateVehicle(id, vehicleDTO);
        SuccessResponse response = new SuccessResponse("Veículo Atualizado com sucesso",id);
        return new ResponseEntity<>(response,HttpStatus.ACCEPTED);
    }

    @DeleteMapping("/delete-vehicle/{id}")
    public ResponseEntity<SuccessResponse> deleteVehicle(@PathVariable UUID id) {
        vehicleService.deleteVehicle(id);
        SuccessResponse response = new SuccessResponse("Veículo deletado com sucesso",id);
        return new ResponseEntity<>(response,HttpStatus.OK);
    }

    @GetMapping("/listall-vehicle")
    public ResponseEntity<Page<VehicleDTO>> listAllVehicle(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<VehicleDTO> vehicles = vehicleService.listVehicles(PageRequest.of(page, size));
        return new ResponseEntity<>(vehicles,HttpStatus.FOUND);
    }

    @GetMapping("/findbyid-vehicle/{id}")
    public ResponseEntity<?> findVehicleById(@PathVariable UUID id) {
        Optional<Vehicle> vehicle = Optional.ofNullable(vehicleService.findById(id));
        if(vehicle.isPresent()){
            return ResponseEntity.ok(vehicle);
        }else{
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Veículo não encontrado");
        }
    }
}
