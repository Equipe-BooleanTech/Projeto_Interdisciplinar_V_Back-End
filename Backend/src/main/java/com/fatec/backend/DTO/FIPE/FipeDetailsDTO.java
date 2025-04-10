package com.fatec.backend.DTO.FIPE;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;


@JsonIgnoreProperties(ignoreUnknown = true)
public record FipeDetailsDTO(
        String modelo,
        String marca,
        Integer anoModelo,
        String combustivel,
        String codigoFipe,
        Integer tipoVeiculo
) {
}
