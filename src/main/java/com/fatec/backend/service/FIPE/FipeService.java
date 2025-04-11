package com.fatec.backend.service.FIPE;

import com.fatec.backend.DTO.FIPE.FipeDetailsDTO;
import com.fatec.backend.DTO.FIPE.MarcaAnoDTO;
import com.fatec.backend.DTO.FIPE.ModeloResponse;
import lombok.AllArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;

@AllArgsConstructor
@Service
public class FipeService {
    private final String BASE_URL = "https://parallelum.com.br/fipe/api/v1/carros";

    private final RestTemplate restTemplate;

@Cacheable("marcas")
    public List<MarcaAnoDTO> getMarcas() {
        MarcaAnoDTO[] marcas = restTemplate.getForObject(BASE_URL + "/marcas", MarcaAnoDTO[].class);
        return Arrays.asList(marcas);
    }
    @Cacheable(value = "modelos", key = "#marcaId")
    public ModeloResponse getModelos(String marcaId) {
        return restTemplate.getForObject(BASE_URL + "/marcas/" + marcaId + "/modelos", ModeloResponse.class);
    }

    @Cacheable(value = "anos", key = "#marcaId + '-' + #modeloId")
    public List<MarcaAnoDTO> getAnos(String marcaId, String modeloId) {
        MarcaAnoDTO[] anos = restTemplate.getForObject(BASE_URL + "/marcas/" + marcaId + "/modelos/" + modeloId + "/anos", MarcaAnoDTO[].class);
        assert anos != null;
        return Arrays.asList(anos);
    }
    @Cacheable(value = "detalhes", key = "#marcaId + '-' + #modeloId + '-' + #anoId")
    public FipeDetailsDTO getDetalhes(String marcaId, String modeloId, String anoId) {
        return restTemplate.getForObject(
                BASE_URL + "/marcas/" + marcaId + "/modelos/" + modeloId + "/anos/" + anoId,
                FipeDetailsDTO.class
        );
    }
}
