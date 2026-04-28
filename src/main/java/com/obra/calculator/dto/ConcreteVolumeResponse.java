package com.obra.calculator.dto;

import java.util.Map;

public class ConcreteVolumeResponse {
    private double volumeTotalM3;
    private Map<String, Double> volumePorAresta;
    private String descricao;

    public ConcreteVolumeResponse(double volumeTotalM3, Map<String, Double> volumePorAresta) {
        this.volumeTotalM3 = volumeTotalM3;
        this.volumePorAresta = volumePorAresta;
        this.descricao = "Volume total de concreto para vigas baldrame (L x A x C)";
    }

    public double getVolumeTotalM3() { return volumeTotalM3; }
    public Map<String, Double> getVolumePorAresta() { return volumePorAresta; }
    public String getDescricao() { return descricao; }
}
