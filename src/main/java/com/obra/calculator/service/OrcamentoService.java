package com.obra.calculator.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.obra.calculator.dto.BrickRequest;
import com.obra.calculator.dto.BrickResponse;
import com.obra.calculator.dto.ConcreteVolumeRequest;
import com.obra.calculator.dto.ConcreteVolumeResponse;
import com.obra.calculator.entity.Orcamento;
import com.obra.calculator.model.Aresta;
import com.obra.calculator.model.Vertice;
import com.obra.calculator.repository.OrcamentoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class OrcamentoService {

    private final CalculadoraService calculadoraService;
    private final OrcamentoRepository repository;
    private final ObjectMapper objectMapper;

    public OrcamentoService(CalculadoraService calculadoraService,
                            OrcamentoRepository repository,
                            ObjectMapper objectMapper) {
        this.calculadoraService = calculadoraService;
        this.repository = repository;
        this.objectMapper = objectMapper;
    }

    @Transactional
    public Orcamento calcularEPersistir(String nomeUsuario,
                                        double alturaViga,
                                        double tijoloComprimento,
                                        double tijoloAltura,
                                        double tijoloLargura,
                                        List<Vertice> vertices,
                                        List<Aresta> arestas) {
        // Concreto
        ConcreteVolumeRequest concreteReq = new ConcreteVolumeRequest();
        concreteReq.setArestas(arestas);
        concreteReq.setAltura(alturaViga);
        ConcreteVolumeResponse concreteResp = calculadoraService.calcularVolumeConcreto(concreteReq);

        // Tijolos
        BrickRequest brickReq = new BrickRequest();
        brickReq.setArestas(arestas);
        brickReq.setTijoloComprimento(tijoloComprimento);
        brickReq.setTijoloAltura(tijoloAltura);
        brickReq.setTijoloLargura(tijoloLargura);
        BrickResponse brickResp = calculadoraService.calcularTijolos(brickReq);

        // Persistência
        Orcamento orc = new Orcamento();
        orc.setNomeUsuario(nomeUsuario);
        orc.setAlturaViga(alturaViga);
        orc.setTijoloComprimento(tijoloComprimento);
        orc.setTijoloAltura(tijoloAltura);
        orc.setTijoloLargura(tijoloLargura);
        orc.setVolumeTotalConcretoM3(concreteResp.getVolumeTotalM3());
        orc.setQuantidadeTotalTijolos(brickResp.getQuantidadeTotalTijolos());

        try {
            orc.setVerticesJson(objectMapper.writeValueAsString(vertices));
            orc.setArestasJson(objectMapper.writeValueAsString(arestas));
        } catch (Exception e) {
            throw new RuntimeException("Erro ao serializar planta em JSON", e);
        }

        return repository.save(orc);
    }

    public Optional<Orcamento> buscarPorNumero(String numero) {
        return repository.findByNumeroOrcamento(numero.trim());
    }

    public List<Orcamento> buscarPorNome(String nome) {
        return repository.findByNomeUsuarioContainingIgnoreCase(nome.trim());
    }

    public List<Aresta> parseArestasDoJson(String json) {
        try {
            return objectMapper.readValue(json, new TypeReference<List<Aresta>>() {});
        } catch (Exception e) {
            throw new IllegalArgumentException("JSON de arestas inválido: " + e.getMessage(), e);
        }
    }

    public List<Vertice> parseVerticesDoJson(String json) {
        try {
            return objectMapper.readValue(json, new TypeReference<List<Vertice>>() {});
        } catch (Exception e) {
            throw new IllegalArgumentException("JSON de vértices inválido: " + e.getMessage(), e);
        }
    }
}
