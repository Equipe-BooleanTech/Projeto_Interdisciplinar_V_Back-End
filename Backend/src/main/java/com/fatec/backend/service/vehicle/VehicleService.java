package com.fatec.backend.service.vehicle;

import com.fatec.backend.DTO.vehicle.VehicleDTO;
import com.fatec.backend.mapper.vehicle.VehicleMapper;
import com.fatec.backend.model.Vehicle;
import com.fatec.backend.repository.VehicleRespository;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.UUID;
@Service
@AllArgsConstructor
public class VehicleService {
    private final VehicleRespository vehicleRespository;

    public UUID createVehicle(VehicleDTO vehicleDTO) {
        if(vehicleRespository.findByPlate(vehicleDTO.plate()).isPresent()) {
            throw new IllegalArgumentException("Plate already exists");
        }
        Vehicle vehicle = Vehicle.builder()
                .plate(vehicleDTO.plate())
                .model(vehicleDTO.model())
                .color(vehicleDTO.color())
                .manufacturer(vehicleDTO.manufacturer())
                .type(vehicleDTO.type())
                .description(vehicleDTO.description())
                .year(vehicleDTO.year())
                .Km(vehicleDTO.Km())
                .fuelType(vehicleDTO.fuelType())
                .fuelCapacity(vehicleDTO.fuelCapacity())
                .fuelConsumption(vehicleDTO.fuelConsumption())
                .build();

        vehicleRespository.save(vehicle);

        return vehicle.getUuid();
    }

    public void updateVehicle(UUID id,VehicleDTO vehicleDTO) {
        Vehicle vehicle = vehicleRespository.findById(id).orElseThrow(() -> new IllegalArgumentException("Vehicle not found"));
        vehicle.setPlate(vehicleDTO.plate());
        vehicle.setModel(vehicleDTO.model());
        vehicle.setColor(vehicleDTO.color());
        vehicle.setManufacturer(vehicleDTO.manufacturer());
        vehicle.setType(vehicleDTO.type());
        vehicle.setDescription(vehicleDTO.description());
        vehicle.setYear(vehicleDTO.year());
        vehicle.setKm(vehicleDTO.Km());
        vehicle.setFuelType(vehicleDTO.fuelType());
        vehicle.setFuelCapacity(vehicleDTO.fuelCapacity());
        vehicle.setFuelConsumption(vehicleDTO.fuelConsumption());

        vehicleRespository.save(vehicle);
    }


    public void deleteVehicle(UUID id) {
        if(!vehicleRespository.existsById(id)){
            throw new IllegalArgumentException("Vehicle not found");
        }
        vehicleRespository.deleteById(id);
    }

    public Page<VehicleDTO> listVehicles(PageRequest pageRequest) {
        return vehicleRespository.findAll(pageRequest)
                .map(VehicleMapper.INSTANCE::ToVehicleDTO);
    }

    public Vehicle findById(UUID id) {
        return vehicleRespository.findById(id).orElseThrow(() -> new IllegalArgumentException("Vehicle not found"));
    }
}
