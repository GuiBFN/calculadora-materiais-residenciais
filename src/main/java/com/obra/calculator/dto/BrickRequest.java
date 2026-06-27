package com.obra.calculator.dto;

import com.obra.calculator.model.Aresta;
import java.util.List;

public class BrickRequest {
    private List<Aresta> arestas;
    private double tijoloAltura;       // altura do tijolo (m)
    private double tijoloLargura;      // largura/espessura do tijolo (m)
    private double tijoloComprimento;  // comprimento do tijolo (m)

    public List<Aresta> getArestas() { return arestas; }
    public void setArestas(List<Aresta> arestas) { this.arestas = arestas; }
    public double getTijoloAltura() { return tijoloAltura; }
    public void setTijoloAltura(double tijoloAltura) { this.tijoloAltura = tijoloAltura; }
    public double getTijoloLargura() { return tijoloLargura; }
    public void setTijoloLargura(double tijoloLargura) { this.tijoloLargura = tijoloLargura; }
    public double getTijoloComprimento() { return tijoloComprimento; }
    public void setTijoloComprimento(double tijoloComprimento) { this.tijoloComprimento = tijoloComprimento; }
}
