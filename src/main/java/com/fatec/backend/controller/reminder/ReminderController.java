package com.fatec.backend.controller.reminder;

import com.fatec.backend.DTO.reminder.ReminderDTO;
import com.fatec.backend.response.SuccessResponse; // Importando SuccessResponse
import com.fatec.backend.service.reminder.ReminderService;
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
@RequestMapping("/api/reminders")
@RequiredArgsConstructor
public class ReminderController {

    private final ReminderService reminderService;

    // TODO: Substituir mockUserId pela obtenção real do ID do usuário autenticado
    private final UUID mockUserId = UUID.fromString("d0f6e2d9-4f8c-4f8b-9b8b-0e2f0c6f0a1b");

    /**
     * Cria um novo lembrete para o usuário autenticado.
     * Retorna o UUID do lembrete criado.
     */
    @PostMapping
    public ResponseEntity<UUID> createReminder(@Valid @RequestBody ReminderDTO reminderDTO) {
        ReminderDTO createdReminder = reminderService.createReminder(reminderDTO, mockUserId);
        return new ResponseEntity<>(createdReminder.id(), HttpStatus.CREATED);
    }

    /**
     * Lista os lembretes do usuário autenticado com paginação.
     */
    @GetMapping("/user")
    public ResponseEntity<Page<ReminderDTO>> getUserReminders(@PageableDefault(size = 10) Pageable pageable) {
        Page<ReminderDTO> reminders = reminderService.getUserReminders(mockUserId, pageable);
        // HttpStatus.FOUND não é o ideal para GET. Usar OK.
        return ResponseEntity.ok(reminders);
    }

    /**
     * Busca um lembrete específico pelo ID, verificando se pertence ao usuário autenticado.
     */
    @GetMapping("/{reminderId}")
    public ResponseEntity<ReminderDTO> getReminderById(@PathVariable UUID reminderId) {
        ReminderDTO reminder = reminderService.getReminderById(reminderId, mockUserId);
        return ResponseEntity.ok(reminder);
    }

    /**
     * Atualiza um lembrete existente.
     * Retorna SuccessResponse em caso de sucesso.
     */
    @PutMapping("/{reminderId}")
    public ResponseEntity<SuccessResponse> updateReminder(@PathVariable UUID reminderId, @Valid @RequestBody ReminderDTO reminderDTO) {
        reminderService.updateReminder(reminderId, reminderDTO, mockUserId);
        SuccessResponse response = new SuccessResponse("Lembrete atualizado com sucesso", reminderId);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * Deleta um lembrete existente.
     * Retorna SuccessResponse em caso de sucesso.
     */
    @DeleteMapping("/{reminderId}")
    public ResponseEntity<SuccessResponse> deleteReminder(@PathVariable UUID reminderId) {
        reminderService.deleteReminder(reminderId, mockUserId);
        SuccessResponse response = new SuccessResponse("Lembrete deletado com sucesso", reminderId);
        // Usar OK ou NO_CONTENT é comum para DELETE. SuccessResponse sugere OK.
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * Endpoint conceitual para disparar a verificação de lembretes pendentes.
     * A funcionalidade de agendamento real está desabilitada neste ambiente.
     */
    @PostMapping("/check-pending")
    public ResponseEntity<String> checkPendingReminders() {
        // Simplificando a resposta
        try {
            reminderService.checkAndProcessPendingReminders();
            return ResponseEntity.ok("Verificação de lembretes pendentes iniciada (conceitual).");
        } catch (Exception e) {
            // Adicionar log de erro aqui seria bom
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro ao verificar lembretes: " + e.getMessage());
        }
    }
}
