package com.obra.calculator.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "orcamentos")
public class Orcamento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "numero_orcamento", unique = true, nullable = false)
    private String numeroOrcamento;

    @Column(name = "nome_usuario", nullable = false)
    private String nomeUsuario;

    @Column(name = "data_criacao")
    private LocalDateTime dataCriacao;

    // Parâmetros de entrada serializados como JSON
    @Column(name = "vertices_json", columnDefinition = "TEXT")
    private String verticesJson;

    @Column(name = "arestas_json", columnDefinition = "TEXT")
    private String arestasJson;

    // Parâmetros complementares de cálculo
    @Column(name = "altura_viga")
    private Double alturaViga;

    @Column(name = "tijolo_comprimento")
    private Double tijoloComprimento;

    @Column(name = "tijolo_altura")
    private Double tijoloAltura;

    @Column(name = "tijolo_largura")
    private Double tijoloLargura;

    // Resultados
    @Column(name = "volume_total_concreto_m3")
    private Double volumeTotalConcretoM3;

    @Column(name = "quantidade_total_tijolos")
    private Integer quantidadeTotalTijolos;

    @PrePersist
    private void prePersist() {
        dataCriacao = LocalDateTime.now();
        if (numeroOrcamento == null) {
            numeroOrcamento = "ORC-" + System.currentTimeMillis();
        }
    }

    // ── Getters e Setters ────────────────────────────────────────────────────

    public Long getId() { return id; }

    public String getNumeroOrcamento() { return numeroOrcamento; }
    public void setNumeroOrcamento(String numeroOrcamento) { this.numeroOrcamento = numeroOrcamento; }

    public String getNomeUsuario() { return nomeUsuario; }
    public void setNomeUsuario(String nomeUsuario) { this.nomeUsuario = nomeUsuario; }

    public LocalDateTime getDataCriacao() { return dataCriacao; }
    public void setDataCriacao(LocalDateTime dataCriacao) { this.dataCriacao = dataCriacao; }

    public String getVerticesJson() { return verticesJson; }
    public void setVerticesJson(String verticesJson) { this.verticesJson = verticesJson; }

    public String getArestasJson() { return arestasJson; }
    public void setArestasJson(String arestasJson) { this.arestasJson = arestasJson; }

    public Double getAlturaViga() { return alturaViga; }
    public void setAlturaViga(Double alturaViga) { this.alturaViga = alturaViga; }

    public Double getTijoloComprimento() { return tijoloComprimento; }
    public void setTijoloComprimento(Double tijoloComprimento) { this.tijoloComprimento = tijoloComprimento; }

    public Double getTijoloAltura() { return tijoloAltura; }
    public void setTijoloAltura(Double tijoloAltura) { this.tijoloAltura = tijoloAltura; }

    public Double getTijoloLargura() { return tijoloLargura; }
    public void setTijoloLargura(Double tijoloLargura) { this.tijoloLargura = tijoloLargura; }

    public Double getVolumeTotalConcretoM3() { return volumeTotalConcretoM3; }
    public void setVolumeTotalConcretoM3(Double volumeTotalConcretoM3) { this.volumeTotalConcretoM3 = volumeTotalConcretoM3; }

    public Integer getQuantidadeTotalTijolos() { return quantidadeTotalTijolos; }
    public void setQuantidadeTotalTijolos(Integer quantidadeTotalTijolos) { this.quantidadeTotalTijolos = quantidadeTotalTijolos; }
}
