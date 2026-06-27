package com.obra.calculator.dto;

import java.util.Map;

public class BrickResponse {
    private int quantidadeTotalTijolos;
    private Map<String, Integer> tijolosPorAresta;
    private String descricao;

    public BrickResponse(int quantidadeTotalTijolos, Map<String, Integer> tijolosPorAresta) {
        this.quantidadeTotalTijolos = quantidadeTotalTijolos;
        this.tijolosPorAresta = tijolosPorAresta;
        this.descricao = "Quantidade de tijolos com 10% de perda incluída";
    }

    public int getQuantidadeTotalTijolos() { return quantidadeTotalTijolos; }
    public Map<String, Integer> getTijolosPorAresta() { return tijolosPorAresta; }
    public String getDescricao() { return descricao; }
}
