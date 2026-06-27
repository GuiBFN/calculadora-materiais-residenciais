# Calculadora de Materiais para Obra Residencial

Atividade avaliativa — Disciplina: Desenvolvimento de Sistemas (UniCEUB) | Aluno: Guilherme

Sistema para cálculo de materiais em obras residenciais, modelando a planta baixa como um grafo G=(V,A). Inclui API REST e frontend Jakarta Faces com persistência em Supabase.

## Tecnologias

- Java 21
- Spring Boot 3.3.5
- Jakarta Faces (JSF via Joinfaces 5.3.0 — sem servidor externo)
- Spring Data JPA + Hibernate
- PostgreSQL — Supabase (conexão JDBC direta)
- Maven

## Pré-requisitos

1. Java 21 e Maven instalados.
2. Conta Supabase com o projeto `efvjiosjxenrgeygvqms` configurado.

## Configuração

Exporte a senha do banco antes de rodar (**nunca commite a senha**):

```bash
# Windows PowerShell
$env:DB_PASSWORD = "sua-senha-aqui"

# Linux / macOS
export DB_PASSWORD="sua-senha-aqui"
```

## Como rodar

```bash
mvn spring-boot:run
```

- **Frontend (JSF):** `http://localhost:8080/orcamento.xhtml`
- **API REST:** `http://localhost:8080/api/...`

---

## Endpoints

### POST `/api/concrete-volume`
Calcula o volume de concreto das vigas baldrame.

**Fórmula:** `Volume = Largura × Altura × Comprimento` por aresta

```json
{
  "altura": 0.40,
  "arestas": [
    { "id": "a12", "verticeOrigem": "V1", "verticeDestino": "V2", "comprimento": 5.0, "largura": 0.20 },
    { "id": "a23", "verticeOrigem": "V2", "verticeDestino": "V3", "comprimento": 3.0, "largura": 0.20 }
  ]
}
```

**Resposta:**
```json
{
  "volumeTotalM3": 0.64,
  "volumePorAresta": { "a12": 0.4, "a23": 0.24 },
  "descricao": "Volume total de concreto para vigas baldrame (L x A x C)"
}
```

---

### POST `/api/brick-quantity`
Calcula a quantidade de tijolos por parede (com 10% de perda).

**Fórmula:** `(Área líquida / Área da face do tijolo) × 1.10`

```json
{
  "tijoloAltura": 0.057,
  "tijoloLargura": 0.09,
  "tijoloComprimento": 0.19,
  "arestas": [
    { "id": "a12", "comprimento": 5.0, "alturaParede": 2.80, "temJanela": true, "janelAltura": 1.20, "janelaComprimento": 1.50, "temPorta": false },
    { "id": "a23", "comprimento": 3.0, "alturaParede": 2.80, "temPorta": true, "portaAltura": 2.10, "portaComprimento": 0.90, "temJanela": false }
  ]
}
```

**Resposta:**
```json
{
  "quantidadeTotalTijolos": 996,
  "tijolosPorAresta": { "a12": 621, "a23": 375 },
  "descricao": "Quantidade de tijolos com 10% de perda incluída"
}
```

---

## Plano de Teste

Ver [PLANO_DE_TESTE.md](PLANO_DE_TESTE.md) para os 8 casos de teste documentados (CT-01 a CT-08).

## Estrutura do Projeto

```
src/main/java/com/obra/calculator/
├── ObraCalculatorApplication.java
├── model/
│   ├── Vertice.java            # Pilar (vértice do grafo)
│   └── Aresta.java             # Parede (aresta do grafo)
├── entity/
│   └── Orcamento.java          # Entidade JPA — tabela orcamentos
├── repository/
│   └── OrcamentoRepository.java
├── dto/
│   ├── ConcreteVolumeRequest/Response.java
│   └── BrickRequest/Response.java
├── service/
│   ├── CalculadoraService.java  # Cálculos (Etapa 1 — não modificado)
│   └── OrcamentoService.java    # Persistência e orquestração
├── bean/
│   └── OrcamentoBean.java       # Managed bean Jakarta Faces
└── controller/
    └── CalculadoraController.java

src/main/resources/
├── application.properties
└── META-INF/
    ├── faces-config.xml
    └── resources/
        └── orcamento.xhtml      # Tela principal JSF
```
