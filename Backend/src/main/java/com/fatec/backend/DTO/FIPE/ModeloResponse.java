package com.fatec.backend.DTO.FIPE;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record ModeloResponse(
        List<MarcaAnoDTO> modelos
) {
}
