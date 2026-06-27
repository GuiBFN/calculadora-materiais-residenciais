package com.obra.calculator.dto;

import com.obra.calculator.model.Aresta;
import java.util.List;

public class ConcreteVolumeRequest {
    private List<Aresta> arestas;
    private double altura; // altura da viga baldrame (user input)

    public List<Aresta> getArestas() { return arestas; }
    public void setArestas(List<Aresta> arestas) { this.arestas = arestas; }
    public double getAltura() { return altura; }
    public void setAltura(double altura) { this.altura = altura; }
}
