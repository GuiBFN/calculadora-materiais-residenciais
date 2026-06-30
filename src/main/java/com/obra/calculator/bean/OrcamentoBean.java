package com.obra.calculator.bean;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.obra.calculator.entity.Orcamento;
import com.obra.calculator.model.Aresta;
import com.obra.calculator.model.Vertice;
import com.obra.calculator.service.OrcamentoService;
import jakarta.annotation.PostConstruct;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Named;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Named
@ViewScoped
public class OrcamentoBean implements Serializable {

    @Autowired
    private transient OrcamentoService orcamentoService;

    @Autowired
    private transient ObjectMapper objectMapper;

    // ── Campos do formulário ─────────────────────────────────────────────────

    private String nomeUsuario;
    private String modoEntrada = "MANUAL";
    private String jsonInput;

    // Parâmetros de concreto e tijolo
    private double alturaViga = 0.30;
    private double tijoloComprimento = 0.19;
    private double tijoloAltura = 0.05;
    private double tijoloLargura = 0.09;

    private List<Vertice> vertices = new ArrayList<>();
    private List<Aresta> arestas = new ArrayList<>();

    // ── Resultado ────────────────────────────────────────────────────────────

    private Orcamento orcamentoResultado;

    // ── Busca ────────────────────────────────────────────────────────────────

    private String termoBusca;
    private List<Orcamento> resultadosBusca = new ArrayList<>();

    // ── Inicialização ────────────────────────────────────────────────────────

    @PostConstruct
    public void init() {
        Vertice v1 = new Vertice("V1", "Pilar 1");
        Vertice v2 = new Vertice("V2", "Pilar 2");
        vertices.add(v1);
        vertices.add(v2);

        Aresta a = new Aresta();
        a.setId("A1");
        a.setVerticeOrigem("V1");
        a.setVerticeDestino("V2");
        a.setComprimento(5.0);
        a.setLargura(0.20);
        a.setAlturaParede(2.80);
        arestas.add(a);
    }

    // ── Ações do formulário ──────────────────────────────────────────────────

    public void adicionarVertice() {
        Vertice v = new Vertice();
        v.setId("V" + (vertices.size() + 1));
        v.setDescricao("Pilar " + (vertices.size() + 1));
        vertices.add(v);
    }

    public void removerVertice(Vertice v) {
        vertices.remove(v);
    }

    public void adicionarAresta() {
        Aresta a = new Aresta();
        a.setId("A" + (arestas.size() + 1));
        a.setLargura(0.20);
        a.setAlturaParede(2.80);
        arestas.add(a);
    }

    public void removerAresta(Aresta a) {
        arestas.remove(a);
    }

    public void calcularOrcamento() {
        FacesContext ctx = FacesContext.getCurrentInstance();

        if (nomeUsuario == null || nomeUsuario.isBlank()) {
            ctx.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Informe o nome do usuário.", null));
            return;
        }

        if ("JSON".equals(modoEntrada)) {
            if (!importarDeJson()) return;
        }

        if (arestas.isEmpty()) {
            ctx.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN,
                    "Adicione pelo menos uma parede (aresta) antes de calcular.", null));
            return;
        }

        // BUG-3: Rejeita valores negativos ou zero nos parâmetros de cálculo
        if (alturaViga <= 0 || tijoloComprimento <= 0 || tijoloAltura <= 0 || tijoloLargura <= 0) {
            ctx.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Parâmetros inválidos.",
                    "Altura da viga e dimensões do tijolo devem ser maiores que zero."));
            return;
        }
        for (Aresta a : arestas) {
            if (a.getComprimento() <= 0 || a.getLargura() <= 0 || a.getAlturaParede() <= 0) {
                ctx.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                        "Valor inválido na aresta " + a.getId() + ".",
                        "Comprimento, largura e altura da parede devem ser maiores que zero."));
                return;
            }
            if (a.isTemJanela() && (a.getJanelaComprimento() <= 0 || a.getJanelAltura() <= 0)) {
                ctx.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                        "Janela inválida na aresta " + a.getId() + ".",
                        "Dimensões da janela devem ser maiores que zero."));
                return;
            }
            if (a.isTemPorta() && (a.getPortaComprimento() <= 0 || a.getPortaAltura() <= 0)) {
                ctx.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                        "Porta inválida na aresta " + a.getId() + ".",
                        "Dimensões da porta devem ser maiores que zero."));
                return;
            }
        }

        try {
            orcamentoResultado = orcamentoService.calcularEPersistir(
                    nomeUsuario, alturaViga,
                    tijoloComprimento, tijoloAltura, tijoloLargura,
                    vertices, arestas);

            ctx.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO,
                    "Orçamento " + orcamentoResultado.getNumeroOrcamento() + " gerado com sucesso!", null));
        } catch (Exception e) {
            ctx.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Erro ao calcular orçamento: " + e.getMessage(), null));
        }
    }

    private boolean importarDeJson() {
        FacesContext ctx = FacesContext.getCurrentInstance();
        if (jsonInput == null || jsonInput.isBlank()) {
            ctx.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Cole o JSON da planta antes de calcular.", null));
            return false;
        }
        try {
            JsonNode root = objectMapper.readTree(jsonInput);

            // Suporta formato ConcreteVolumeRequest / BrickRequest (campo "arestas")
            if (root.has("arestas")) {
                arestas = orcamentoService.parseArestasDoJson(
                        objectMapper.writeValueAsString(root.get("arestas")));
            }
            if (root.has("vertices")) {
                vertices = orcamentoService.parseVerticesDoJson(
                        objectMapper.writeValueAsString(root.get("vertices")));
            }
            if (root.has("altura")) {
                alturaViga = root.get("altura").asDouble(alturaViga);
            }
            if (root.has("tijoloComprimento")) {
                tijoloComprimento = root.get("tijoloComprimento").asDouble(tijoloComprimento);
            }
            if (root.has("tijoloAltura")) {
                tijoloAltura = root.get("tijoloAltura").asDouble(tijoloAltura);
            }
            if (root.has("tijoloLargura")) {
                tijoloLargura = root.get("tijoloLargura").asDouble(tijoloLargura);
            }
            // BUG-2: Rejeita arestas sem alturaParede definida (resultaria em 0 tijolos silenciosamente)
            for (Aresta a : arestas) {
                if (a.getAlturaParede() <= 0) {
                    ctx.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                            "JSON inválido.",
                            "Campo 'alturaParede' ausente ou zero na aresta '"
                                    + (a.getId() != null ? a.getId() : "?")
                                    + "'. O cálculo de tijolos não pode ser realizado."));
                    return false;
                }
            }
            return true;
        } catch (JsonProcessingException e) {
            // UX-1: Exibe mensagem amigável em vez da exceção bruta do Jackson
            ctx.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "JSON inválido.", "Verifique a formatação e tente novamente."));
            return false;
        } catch (Exception e) {
            ctx.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Erro ao processar JSON.", e.getMessage()));
            return false;
        }
    }

    public void buscarPorNumero() {
        FacesContext ctx = FacesContext.getCurrentInstance();
        if (termoBusca == null || termoBusca.isBlank()) {
            ctx.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO,
                    "Informe um número de orçamento para buscar.", null));
            return;
        }
        resultadosBusca = new ArrayList<>();
        orcamentoService.buscarPorNumero(termoBusca).ifPresent(resultadosBusca::add);
    }

    public void buscarPorNome() {
        FacesContext ctx = FacesContext.getCurrentInstance();
        if (termoBusca == null || termoBusca.isBlank()) {
            ctx.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO,
                    "Informe um nome de usuário para buscar.", null));
            return;
        }
        resultadosBusca = orcamentoService.buscarPorNome(termoBusca);
    }

    public void limpar() {
        vertices.clear();
        arestas.clear();
        orcamentoResultado = null;
        resultadosBusca.clear();
        nomeUsuario = null;
        jsonInput = null;
        modoEntrada = "MANUAL";
        init();
    }

    // ── Getters e Setters ────────────────────────────────────────────────────

    public String getNomeUsuario() { return nomeUsuario; }
    public void setNomeUsuario(String nomeUsuario) { this.nomeUsuario = nomeUsuario; }

    public String getModoEntrada() { return modoEntrada; }
    public void setModoEntrada(String modoEntrada) { this.modoEntrada = modoEntrada; }

    public String getJsonInput() { return jsonInput; }
    public void setJsonInput(String jsonInput) { this.jsonInput = jsonInput; }

    public double getAlturaViga() { return alturaViga; }
    public void setAlturaViga(double alturaViga) { this.alturaViga = alturaViga; }

    public double getTijoloComprimento() { return tijoloComprimento; }
    public void setTijoloComprimento(double tijoloComprimento) { this.tijoloComprimento = tijoloComprimento; }

    public double getTijoloAltura() { return tijoloAltura; }
    public void setTijoloAltura(double tijoloAltura) { this.tijoloAltura = tijoloAltura; }

    public double getTijoloLargura() { return tijoloLargura; }
    public void setTijoloLargura(double tijoloLargura) { this.tijoloLargura = tijoloLargura; }

    public List<Vertice> getVertices() { return vertices; }
    public void setVertices(List<Vertice> vertices) { this.vertices = vertices; }

    public List<Aresta> getArestas() { return arestas; }
    public void setArestas(List<Aresta> arestas) { this.arestas = arestas; }

    public Orcamento getOrcamentoResultado() { return orcamentoResultado; }

    public String getTermoBusca() { return termoBusca; }
    public void setTermoBusca(String termoBusca) { this.termoBusca = termoBusca; }

    public List<Orcamento> getResultadosBusca() { return resultadosBusca; }

    public boolean isModoManual() { return "MANUAL".equals(modoEntrada); }
    public boolean isModoJson() { return "JSON".equals(modoEntrada); }
}
