package com.fatec.backend.service.maintenance;

import com.fatec.backend.model.vehicle.Vehicle;
import com.fatec.backend.repository.VehicleRespository;
import lombok.RequiredArgsConstructor;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MaintenanceService {

    private final MaintenanceRepository maintenanceRepository;
    private final VehicleRespository vehicleRepository;
    private final MaintenanceMapper maintenanceMapper;
    // private final ReminderService reminderService; // Inject if creating reminders

    @Transactional
    public MaintenanceDTO createMaintenance(MaintenanceDTO maintenanceDTO, UUID vehicleId) {
        Vehicle vehicle = vehicleRepository.findById(vehicleId)
                .orElseThrow(() -> new IllegalArgumentException("Vehicle not found with ID: " + vehicleId));

        Maintenance maintenance = maintenanceMapper.toEntity(maintenanceDTO);
        maintenance.setVehicle(vehicle);

        // Logic to update vehicle odometer if this maintenance has a higher reading
        if (maintenance.getOdometer() != null && (vehicle.getKm() == null || maintenance.getOdometer() > vehicle.getKm())) {
            vehicle.setKm(maintenance.getOdometer());
            vehicleRepository.save(vehicle);
        }

        Maintenance savedMaintenance = maintenanceRepository.save(maintenance);

        // Optional: Create a reminder for the next due maintenance if details are provided
        // if (savedMaintenance.getNextDueDate() != null) {
        //     ReminderDTO reminderForNextMaintenance = ReminderDTO.builder()
        //             .vehicleId(vehicle.getUuid())
        //             .title("Próxima Manutenção: " + savedMaintenance.getType() + " para " + vehicle.getModel())
        //             .description("Lembrete para a próxima manutenção agendada: " + savedMaintenance.getDescription())
        //             .type(ReminderType.MAINTENANCE)
        //             .dueDate(savedMaintenance.getNextDueDate())
        //             // antecedenceDays can be default or configurable
        //             .build();
        //     reminderService.createReminder(reminderForNextMaintenance, vehicle.getUser().getId());
        // }

        return maintenanceMapper.toDTO(savedMaintenance);
    }

    @Transactional(readOnly = true)
    public Page<MaintenanceDTO> getVehicleMaintenances(UUID vehicleId, Pageable pageable) {
        Vehicle vehicle = vehicleRepository.findById(vehicleId)
                .orElseThrow(() -> new IllegalArgumentException("Vehicle not found with ID: " + vehicleId));
        return maintenanceRepository.findByVehicle(vehicle, pageable).map(maintenanceMapper::toDTO);
    }

    @Transactional(readOnly = true)
    public MaintenanceDTO getMaintenanceById(UUID maintenanceId, UUID vehicleId) {
        Maintenance maintenance = maintenanceRepository.findById(maintenanceId)
                .orElseThrow(() -> new IllegalArgumentException("Maintenance record not found with ID: " + maintenanceId));
        // Ensure the maintenance belongs to the specified vehicle
        if (!maintenance.getVehicle().getUuid().equals(vehicleId)) {
            throw new SecurityException("Maintenance record does not belong to the specified vehicle");
        }
        return maintenanceMapper.toDTO(maintenance);
    }

    @Transactional
    public MaintenanceDTO updateMaintenance(UUID maintenanceId, MaintenanceDTO maintenanceDTO, UUID vehicleIdFromPath) {
        Maintenance existingMaintenance = maintenanceRepository.findById(maintenanceId)
                .orElseThrow(() -> new IllegalArgumentException("Maintenance record not found with ID: " + maintenanceId));

        // Validate that the maintenance belongs to the vehicle from the path (vehicleIdFromPath)
        // and also that the vehicleId in DTO matches, or handle it as an attempt to reassign (which might be disallowed)
        if (!existingMaintenance.getVehicle().getUuid().equals(vehicleIdFromPath)) {
            throw new SecurityException("Maintenance record does not belong to the vehicle specified in path.");
        }
        if (!existingMaintenance.getVehicle().getUuid().equals(maintenanceDTO.vehicleId())){
            // This could be an attempt to change the vehicle associated with the maintenance record.
            // Decide if this is allowed. If so, fetch the new vehicle.
            // For now, let's assume the vehicle cannot be changed this way for simplicity.
            // Or, ensure DTO's vehicleId matches existingMaintenance.getVehicle().getUuid()
            throw new IllegalArgumentException("Changing the vehicle of a maintenance record is not supported or vehicleId in DTO mismatch.");
        }

        // Update fields from DTO
        existingMaintenance.setDate(maintenanceDTO.date());
        existingMaintenance.setOdometer(maintenanceDTO.odometer());
        existingMaintenance.setType(maintenanceDTO.type());
        existingMaintenance.setDescription(maintenanceDTO.description());
        existingMaintenance.setCost(maintenanceDTO.cost());
        existingMaintenance.setWorkshopName(maintenanceDTO.workshopName());
        existingMaintenance.setNextDueDate(maintenanceDTO.nextDueDate());
        existingMaintenance.setNextDueOdometer(maintenanceDTO.nextDueOdometer());

        // Logic to update vehicle odometer if this maintenance has a higher reading
        Vehicle vehicle = existingMaintenance.getVehicle();
        if (existingMaintenance.getOdometer() != null && (vehicle.getKm() == null || existingMaintenance.getOdometer() > vehicle.getKm())) {
            vehicle.setKm(existingMaintenance.getOdometer());
            vehicleRepository.save(vehicle);
        }
        // PreUpdate in Maintenance entity will handle updatedAt
        Maintenance updatedMaintenance = maintenanceRepository.save(existingMaintenance);
        return maintenanceMapper.toDTO(updatedMaintenance);
    }

    @Transactional
    public void deleteMaintenance(UUID maintenanceId, UUID vehicleId) {
        Maintenance maintenance = maintenanceRepository.findById(maintenanceId)
                .orElseThrow(() -> new IllegalArgumentException("Maintenance record not found with ID: " + maintenanceId));
        if (!maintenance.getVehicle().getUuid().equals(vehicleId)) {
            throw new SecurityException("User not authorized to delete this maintenance record or record does not belong to vehicle");
        }
        maintenanceRepository.deleteById(maintenanceId);
    }
}

