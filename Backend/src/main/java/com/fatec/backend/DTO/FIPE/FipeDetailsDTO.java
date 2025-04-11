package com.fatec.backend.DTO.FIPE;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;


@JsonIgnoreProperties(ignoreUnknown = true)
public record FipeDetailsDTO(
        Integer TipoVeiculo,
        String Valor,
        String Marca,
        String Modelo,
        Integer AnoModelo,
        String Combustivel,
        String CodigoFipe,
        String MesReferencia,
        String SiglaCombustivel
) {
}
