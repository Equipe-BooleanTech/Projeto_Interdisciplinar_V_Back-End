package com.fatec.backend.service.vehicle;

import com.fatec.backend.DTO.vehicle.VehicleDTO;
import com.fatec.backend.mapper.vehicle.VehicleMapper;
import com.fatec.backend.model.User;
import com.fatec.backend.model.vehicle.Vehicle;
import com.fatec.backend.repository.UserRepository;
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
    private final UserRepository userRepository;

    public UUID createVehicle(VehicleDTO vehicleDTO, UUID userId) {
        System.out.println("Recebido: " + vehicleDTO);

        if (vehicleRespository.findByPlate(vehicleDTO.plate()).isPresent()) {
            throw new IllegalArgumentException("Placa já cadastrada!");
        }

        if (vehicleDTO.plate() == null || vehicleDTO.plate().isBlank()) {
            throw new IllegalArgumentException("A placa não pode ser nula ou vazia!");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado!"));

        Vehicle vehicle = Vehicle.builder()
                .plate(vehicleDTO.plate())
                .model(vehicleDTO.model())
                .color(vehicleDTO.color())
                .manufacturer(vehicleDTO.manufacturer())
                .type(vehicleDTO.type())
                .description(vehicleDTO.description())
                .year(vehicleDTO.year())
                .odometer(Double.valueOf(vehicleDTO.km()))
                .fuelType(vehicleDTO.fuelType())
                .fuelCapacity(vehicleDTO.fuelCapacity())
                .fuelConsumption(vehicleDTO.fuelConsumption())
                .user(user)
                .build();

        return vehicleRespository.save(vehicle).getUuid();
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
        vehicle.setOdometer(Double.valueOf(vehicleDTO.km()));
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

    public Vehicle findbyPlate( String plate) {
        return vehicleRespository.findByPlate(plate).orElse(null);
    }
    public Vehicle findById(UUID id) {
        return vehicleRespository.findById(id).orElseThrow(() -> new IllegalArgumentException("Vehicle not found"));
    }
}
