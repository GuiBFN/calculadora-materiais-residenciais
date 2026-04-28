package com.obra.calculator.model;

public class Vertice {
    private String id;
    private String descricao;

    public Vertice() {}

    public Vertice(String id, String descricao) {
        this.id = id;
        this.descricao = descricao;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }
}
