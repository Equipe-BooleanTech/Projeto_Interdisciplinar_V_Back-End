package com.fatec.backend.controller.vehicle;

import com.fatec.backend.DTO.vehicle.GasStationDTO;
import com.fatec.backend.model.vehicle.GasStation;
import com.fatec.backend.response.SuccessResponse;
import com.fatec.backend.service.vehicle.GasStationService;
import org.springframework.web.bind.annotation.RequestBody;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/gasstation")
public class GasStationController {
    private final GasStationService gasStationService;

    @PostMapping("/create-gas-station")
    public ResponseEntity<UUID> createGasStation(@RequestBody GasStationDTO gasStation) {
        UUID gasStationId = gasStationService.createGasStation(gasStation);
        System.out.println(gasStation);
        return ResponseEntity.ok().body(gasStationId);
    }

    @PutMapping("/update-gas-station/{id}")
    public ResponseEntity<SuccessResponse> updateGasStation(@PathVariable UUID id, @RequestBody GasStationDTO gasStation) {
        gasStationService.updateGasStation(id, gasStation);
        SuccessResponse response = new SuccessResponse("Posto Atualizado com sucesso",id);
        return ResponseEntity.ok().body(response);
    }

    @DeleteMapping("/delete-gas-station/{id}")
    public ResponseEntity<SuccessResponse> deleteGasStation(@PathVariable UUID id) {
        gasStationService.deleteGasStation(id);
        SuccessResponse response = new SuccessResponse("Posto Deletado com sucesso",id);
        return new ResponseEntity<>(response,HttpStatus.OK);
    }

    @GetMapping("/listall-gas-station")
    public ResponseEntity<Page<GasStationDTO>> listAllGasStation(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {
        Page<GasStationDTO> gasStations = gasStationService.listGasStations(PageRequest.of(page, size));
        return new ResponseEntity<>(gasStations, HttpStatus.FOUND);
    }

    @GetMapping("findbyid-gas-station/{id}")
    public ResponseEntity<?> findGasStationById(@PathVariable UUID id) {
        Optional<GasStation> gasStation = Optional.ofNullable(gasStationService.findById(id));
        if (gasStation.isPresent()) {
            return ResponseEntity.status(HttpStatus.FOUND).body(gasStation);
        }else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Posto não encontrado");
        }
    }

}
