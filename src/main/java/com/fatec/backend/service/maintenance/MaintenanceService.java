package com.fatec.backend.service.maintenance;

import com.fatec.backend.DTO.maintenance.MaintenanceDTO;
import com.fatec.backend.mapper.maintenance.MaintenanceMapper;
import com.fatec.backend.model.maintenance.Maintenance;
import com.fatec.backend.model.vehicle.Vehicle;
import com.fatec.backend.repository.MaintenanceRepository;
import com.fatec.backend.repository.VehicleRespository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MaintenanceService {

    private final MaintenanceRepository maintenanceRepository;
    private final VehicleRespository vehicleRepository;
    private final MaintenanceMapper maintenanceMapper;

    @Transactional
    public MaintenanceDTO createMaintenance(MaintenanceDTO maintenanceDTO, UUID vehicleId) {
        Vehicle vehicle = vehicleRepository.findById(vehicleId)
                .orElseThrow(() -> new IllegalArgumentException("Veículo não encontrado com ID: " + vehicleId));

        // Usando o padrão Builder para criar a entidade
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

        // Atualiza o odômetro do veículo se a manutenção tiver uma leitura maior
        if (maintenance.getOdometer() != null && (vehicle.getKm() == null || maintenance.getOdometer() > vehicle.getKm())) {
            vehicle.setKm(maintenance.getOdometer());
            vehicleRepository.save(vehicle); // Salva a atualização do veículo
        }

        Maintenance savedMaintenance = maintenanceRepository.save(maintenance);
        return maintenanceMapper.ToMaintenanceDTO(savedMaintenance);
    }

    @Transactional(readOnly = true)
    public Page<MaintenanceDTO> getVehicleMaintenances(UUID vehicleId, Pageable pageable) {
        // Valida se o veículo existe antes de buscar as manutenções
        if (!vehicleRepository.existsById(vehicleId)) {
            throw new IllegalArgumentException("Veículo não encontrado com ID: " + vehicleId);
        }
        // A query findByVehicleId garante que só retornará manutenções do veículo correto.
        return maintenanceRepository.findByVehicleUuid(vehicleId, pageable).map(MaintenanceMapper.INSTANCE::ToMaintenanceDTO);


    @Transactional(readOnly = true)
    public MaintenanceDTO getMaintenanceById(UUID maintenanceId, UUID vehicleId) {
        Maintenance maintenance = maintenanceRepository.findById(maintenanceId)
                .orElseThrow(() -> new IllegalArgumentException("Registro de manutenção não encontrado com ID: " + maintenanceId));
        // Garante que a manutenção pertence ao veículo especificado na URL
        if (!maintenance.getVehicle().getUuid().equals(vehicleId)) {
            throw new SecurityException("Registro de manutenção não pertence ao veículo especificado.");
        }
        return maintenanceMapper.ToMaintenanceDTO(maintenance);
    }

    @Transactional
    public MaintenanceDTO updateMaintenance(UUID maintenanceId, MaintenanceDTO maintenanceDTO, UUID vehicleIdFromPath) {
        Maintenance existingMaintenance = maintenanceRepository.findById(maintenanceId)
                .orElseThrow(() -> new IllegalArgumentException("Registro de manutenção não encontrado com ID: " + maintenanceId));

        // Valida se a manutenção pertence ao veículo da URL
        if (!existingMaintenance.getVehicle().getUuid().equals(vehicleIdFromPath)) {
            throw new SecurityException("Registro de manutenção não pertence ao veículo especificado na URL.");
        }
        // Valida se o ID do veículo no DTO (se presente) corresponde ao veículo existente
        if (maintenanceDTO.vehicleId() != null && !maintenanceDTO.vehicleId().equals(existingMaintenance.getVehicle().getUuid())){
            throw new IllegalArgumentException("Não é permitido alterar o veículo associado a um registro de manutenção existente.");
        }

        // Atualiza os campos da entidade com os valores do DTO
        existingMaintenance.setDate(maintenanceDTO.date());
        existingMaintenance.setOdometer(maintenanceDTO.odometer());
        existingMaintenance.setType(maintenanceDTO.type());
        existingMaintenance.setDescription(maintenanceDTO.description());
        existingMaintenance.setCost(maintenanceDTO.cost());
        existingMaintenance.setWorkshopName(maintenanceDTO.workshopName());
        existingMaintenance.setNextDueDate(maintenanceDTO.nextDueDate());
        existingMaintenance.setNextDueOdometer(maintenanceDTO.nextDueOdometer());

        // Atualiza o odômetro do veículo se a manutenção atualizada tiver uma leitura maior
        Vehicle vehicle = existingMaintenance.getVehicle();
        if (existingMaintenance.getOdometer() != null && (vehicle.getKm() == null || existingMaintenance.getOdometer() > vehicle.getKm())) {
            vehicle.setKm(existingMaintenance.getOdometer());
            vehicleRepository.save(vehicle);
        }

        // A entidade Maintenance deve ter @PreUpdate para atualizar updatedAt automaticamente
        Maintenance updatedMaintenance = maintenanceRepository.save(existingMaintenance);
        return maintenanceMapper.ToMaintenanceDTO(updatedMaintenance);
    }

    @Transactional
    public void deleteMaintenance(UUID maintenanceId, UUID vehicleId) {
        Maintenance maintenance = maintenanceRepository.findById(maintenanceId)
                .orElseThrow(() -> new IllegalArgumentException("Registro de manutenção não encontrado com ID: " + maintenanceId));
        // Garante que a manutenção pertence ao veículo da URL antes de deletar
        if (!maintenance.getVehicle().getUuid().equals(vehicleId)) {
            throw new SecurityException("Não autorizado a deletar este registro de manutenção ou ele não pertence ao veículo especificado.");
        }
        maintenanceRepository.deleteById(maintenanceId);
    }
}
