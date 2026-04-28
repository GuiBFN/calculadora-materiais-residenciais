package com.obra.calculator.service;

import com.obra.calculator.dto.*;
import com.obra.calculator.model.Aresta;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.Map;

@Service
public class CalculadoraService {

    /**
     * Etapa 2: Volume de concreto das vigas baldrame.
     * Fórmula por viga: L (largura da parede) x A (altura do usuário) x C (comprimento da parede)
     */
    public ConcreteVolumeResponse calcularVolumeConcreto(ConcreteVolumeRequest request) {
        Map<String, Double> volumePorAresta = new LinkedHashMap<>();
        double total = 0;

        for (Aresta a : request.getArestas()) {
            double volume = a.getLargura() * request.getAltura() * a.getComprimento();
            String chave = a.getId() != null ? a.getId() : "aresta";
            volumePorAresta.put(chave, Math.round(volume * 1000.0) / 1000.0);
            total += volume;
        }

        return new ConcreteVolumeResponse(Math.round(total * 1000.0) / 1000.0, volumePorAresta);
    }

    /**
     * Etapa 3: Quantidade de tijolos por parede.
     * Área líquida = (comprimento × alturaParede) - áreas de janelas e portas.
     * Área da face do tijolo = tijoloComprimento × tijoloAltura.
     * Aplica 10% de perda.
     */
    public BrickResponse calcularTijolos(BrickRequest request) {
        Map<String, Integer> tijolosPorAresta = new LinkedHashMap<>();
        int total = 0;

        double areaFaceTijolo = request.getTijoloComprimento() * request.getTijoloAltura();

        for (Aresta a : request.getArestas()) {
            double areaBruta = a.getComprimento() * a.getAlturaParede();

            double areaAberturas = 0;
            if (a.isTemJanela()) {
                areaAberturas += a.getJanelaComprimento() * a.getJanelAltura();
            }
            if (a.isTemPorta()) {
                areaAberturas += a.getPortaComprimento() * a.getPortaAltura();
            }

            double areaLiquida = Math.max(0, areaBruta - areaAberturas);
            int qtd = (int) Math.ceil((areaLiquida / areaFaceTijolo) * 1.10); // +10% perda

            String chave = a.getId() != null ? a.getId() : "aresta";
            tijolosPorAresta.put(chave, qtd);
            total += qtd;
        }

        return new BrickResponse(total, tijolosPorAresta);
    }
}
