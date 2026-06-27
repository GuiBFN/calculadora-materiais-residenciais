package com.obra.calculator.model;

/**
 * Representa uma parede (aresta) no grafo G=(V,A) da planta baixa.
 * comprimento = C, largura = espessura da parede = L, alturaParede = altura da parede.
 */
public class Aresta {
    private String id;
    private String verticeOrigem;
    private String verticeDestino;
    private double comprimento;
    private double largura;
    private double alturaParede;

    // Abertura de janela (opcional)
    private boolean temJanela;
    private double janelAltura;
    private double janelaComprimento;

    // Abertura de porta (opcional)
    private boolean temPorta;
    private double portaAltura;
    private double portaComprimento;

    public Aresta() {}

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getVerticeOrigem() { return verticeOrigem; }
    public void setVerticeOrigem(String verticeOrigem) { this.verticeOrigem = verticeOrigem; }
    public String getVerticeDestino() { return verticeDestino; }
    public void setVerticeDestino(String verticeDestino) { this.verticeDestino = verticeDestino; }
    public double getComprimento() { return comprimento; }
    public void setComprimento(double comprimento) { this.comprimento = comprimento; }
    public double getLargura() { return largura; }
    public void setLargura(double largura) { this.largura = largura; }
    public double getAlturaParede() { return alturaParede; }
    public void setAlturaParede(double alturaParede) { this.alturaParede = alturaParede; }
    public boolean isTemJanela() { return temJanela; }
    public void setTemJanela(boolean temJanela) { this.temJanela = temJanela; }
    public double getJanelAltura() { return janelAltura; }
    public void setJanelAltura(double janelAltura) { this.janelAltura = janelAltura; }
    public double getJanelaComprimento() { return janelaComprimento; }
    public void setJanelaComprimento(double janelaComprimento) { this.janelaComprimento = janelaComprimento; }
    public boolean isTemPorta() { return temPorta; }
    public void setTemPorta(boolean temPorta) { this.temPorta = temPorta; }
    public double getPortaAltura() { return portaAltura; }
    public void setPortaAltura(double portaAltura) { this.portaAltura = portaAltura; }
    public double getPortaComprimento() { return portaComprimento; }
    public void setPortaComprimento(double portaComprimento) { this.portaComprimento = portaComprimento; }
}
