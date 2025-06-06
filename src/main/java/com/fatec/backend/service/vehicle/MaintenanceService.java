package com.fatec.backend.service.vehicle;

import com.fatec.backend.DTO.vehicle.DateRangeDTO;
import com.fatec.backend.DTO.vehicle.FuelRefillDTO;
import com.fatec.backend.DTO.vehicle.MaintenanceDTO;
import com.fatec.backend.DTO.vehicle.TimeSummaryDTO;
import com.fatec.backend.mapper.maintenance.MaintenanceMapper;
import com.fatec.backend.mapper.vehicle.FuelRefillMapper;
import com.fatec.backend.model.vehicle.FuelRefill;
import com.fatec.backend.model.vehicle.Maintenance;
import com.fatec.backend.model.vehicle.Vehicle;
import com.fatec.backend.repository.MaintenanceRepository;
import com.fatec.backend.repository.VehicleRespository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MaintenanceService {

    private final MaintenanceRepository maintenanceRepository;
    private final VehicleRespository vehicleRepository;
    private final MaintenanceMapper maintenanceMapper;


    public MaintenanceDTO createMaintenance(MaintenanceDTO maintenanceDTO, UUID vehicleId) {
        Vehicle vehicle = vehicleRepository.findById(vehicleId)
                .orElseThrow(() -> new IllegalArgumentException("Veículo não encontrado com ID: " + vehicleId));

        Maintenance maintenance = Maintenance.builder()
                .date(maintenanceDTO.date())
                .odometer(maintenanceDTO.odometer())
                .type(maintenanceDTO.type())
                .description(maintenanceDTO.description())
                .cost(maintenanceDTO.cost())
                .workshopName(maintenanceDTO.workshopName())
                .nextDueDate(maintenanceDTO.nextDueDate())
                .nextDueOdometer(maintenanceDTO.nextDueOdometer())
                .vehicle(vehicle)
                .build();


        if (maintenance.getOdometer() != null && (vehicle.getOdometer() == null || maintenance.getOdometer() > vehicle.getOdometer())) {
            vehicle.setOdometer(maintenance.getOdometer());
            vehicleRepository.save(vehicle);
        }

        Maintenance savedMaintenance = maintenanceRepository.save(maintenance);
        return maintenanceMapper.ToMaintenanceDTO(savedMaintenance);
    }

    public void updateMaintenance(UUID maintenanceId, MaintenanceDTO maintenanceDTO, UUID vehicleIdFromPath) {
        Maintenance existingMaintenance = maintenanceRepository.findById(maintenanceId)
                .orElseThrow(() -> new IllegalArgumentException("Registro de manutenção não encontrado com ID: " + maintenanceId));

        if (!existingMaintenance.getVehicle().getId().equals(vehicleIdFromPath)) {
            throw new SecurityException("Registro de manutenção não pertence ao veículo especificado na URL.");
        }
        if (maintenanceDTO.vehicleId() != null && !maintenanceDTO.vehicleId().equals(existingMaintenance.getVehicle().getId())) {
            throw new IllegalArgumentException("Não é permitido alterar o veículo associado a um registro de manutenção existente.");
        }

        existingMaintenance.setDate(maintenanceDTO.date());
        existingMaintenance.setOdometer(maintenanceDTO.odometer());
        existingMaintenance.setType(maintenanceDTO.type());
        existingMaintenance.setDescription(maintenanceDTO.description());
        existingMaintenance.setCost(maintenanceDTO.cost());
        existingMaintenance.setWorkshopName(maintenanceDTO.workshopName());
        existingMaintenance.setNextDueDate(maintenanceDTO.nextDueDate());
        existingMaintenance.setNextDueOdometer(maintenanceDTO.nextDueOdometer());

        Vehicle vehicle = existingMaintenance.getVehicle();
        if (existingMaintenance.getOdometer() != null && (vehicle.getOdometer() == null || existingMaintenance.getOdometer() > vehicle.getOdometer())) {
            vehicle.setOdometer(existingMaintenance.getOdometer());
            vehicleRepository.save(vehicle);
        }

        // A entidade Maintenance deve ter @PreUpdate para atualizar updatedAt automaticamente
        Maintenance updatedMaintenance = maintenanceRepository.save(existingMaintenance);
        maintenanceMapper.ToMaintenanceDTO(updatedMaintenance);
    }

    public void deleteMaintenance(UUID maintenanceId, UUID vehicleId) {
        Maintenance maintenance = maintenanceRepository.findById(maintenanceId)
                .orElseThrow(() -> new IllegalArgumentException("Registro de manutenção não encontrado com ID: " + maintenanceId));
        if (!maintenance.getVehicle().getId().equals(vehicleId)) {
            throw new SecurityException("Não autorizado a deletar este registro de manutenção ou ele não pertence ao veículo especificado.");
        }
        maintenanceRepository.deleteById(maintenanceId);
    }

    public Maintenance findById(UUID id) {
        return maintenanceRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Manutenção não encontrada com ID: " + id));
    }

    public Page<MaintenanceDTO> listMaintenances(UUID vehicleId,PageRequest pageRequest) {
        return maintenanceRepository.findAllByVehicleId(vehicleId,pageRequest)
                .map(MaintenanceMapper.INSTANCE::ToMaintenanceDTO);
    }

    public TimeSummaryDTO ListMaintenanceByDateRange(UUID vehicleId,DateRangeDTO dateRangeDTO) {
        List<Maintenance> data = maintenanceRepository.findAllByVehicleIdAndDateBetween(vehicleId,dateRangeDTO.startDate(), dateRangeDTO.endDate());
        List<MaintenanceDTO> maitenances = data.stream()
                .map(MaintenanceMapper.INSTANCE::ToMaintenanceDTO)
                .toList();
        return new TimeSummaryDTO(Collections.singletonList(maitenances), maitenances.size());
    }
}
