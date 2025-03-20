package com.fatec.backend;

import java.util.UUID;

public class SuccessResponse {
    private String message;
    private UUID id;

    public SuccessResponse(String message, UUID ingredientId) {
        this.message = message;
        this.id = ingredientId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }
}

