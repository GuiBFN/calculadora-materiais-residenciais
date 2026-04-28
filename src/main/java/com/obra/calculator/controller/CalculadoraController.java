package com.obra.calculator.controller;

import com.obra.calculator.dto.*;
import com.obra.calculator.service.CalculadoraService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class CalculadoraController {

    private final CalculadoraService service;

    public CalculadoraController(CalculadoraService service) {
        this.service = service;
    }

    /**
     * Etapa 2 – Volume de Concreto na Fundação (Viga Baldrame)
     * POST /api/concrete-volume
     */
    @PostMapping("/concrete-volume")
    public ResponseEntity<ConcreteVolumeResponse> concreteVolume(@RequestBody ConcreteVolumeRequest request) {
        return ResponseEntity.ok(service.calcularVolumeConcreto(request));
    }

    /**
     * Etapa 3 – Quantidade de Tijolos de Paredes
     * POST /api/brick-quantity
     */
    @PostMapping("/brick-quantity")
    public ResponseEntity<BrickResponse> brickQuantity(@RequestBody BrickRequest request) {
        return ResponseEntity.ok(service.calcularTijolos(request));
    }
}
