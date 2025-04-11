package com.fatec.backend.controller.FIPE;

import com.fatec.backend.DTO.FIPE.FipeDetailsDTO;
import com.fatec.backend.DTO.FIPE.MarcaAnoDTO;
import com.fatec.backend.DTO.FIPE.ModeloResponse;
import com.fatec.backend.service.FIPE.FipeService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/fipe")
@RequiredArgsConstructor
public class FipeController {

    private final FipeService fipeService;

    @GetMapping("/marcas")
    public List<MarcaAnoDTO> getManufacturer() {
        return fipeService.getMarcas();
    }

    @GetMapping("/marcas/{marcaId}/modelos")
    public ModeloResponse getModels(@PathVariable String marcaId) {
        return fipeService.getModelos(marcaId);
    }

    @GetMapping("/marcas/{marcaId}/modelos/{modeloId}/anos")
    public List<MarcaAnoDTO> getYears(@PathVariable String marcaId, @PathVariable String modeloId) {
        return fipeService.getAnos(marcaId, modeloId);
    }

    @GetMapping("/marcas/{marcaId}/modelos/{modeloId}/anos/{anoId}")
    public FipeDetailsDTO getDetails(@PathVariable String marcaId,
                                      @PathVariable String modeloId,
                                      @PathVariable String anoId) {
        return fipeService.getDetalhes(marcaId, modeloId, anoId);
    }
}
