package com.fatec.backend.enums;

public enum ReminderStatus {
    PENDING,    // Lembrete está ativo e aguardando ação
    COMPLETED,  // Lembrete foi concluído
    SNOOZED,    // Lembrete foi adiado
    CANCELLED   // Lembrete foi cancelado (opcional, se necessário)
}

