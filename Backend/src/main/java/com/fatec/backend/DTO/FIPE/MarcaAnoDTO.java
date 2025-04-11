package com.fatec.backend.DTO.FIPE;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;


@JsonIgnoreProperties(ignoreUnknown = true)
public record MarcaAnoDTO (
    String codigo,
    String nome
){}
