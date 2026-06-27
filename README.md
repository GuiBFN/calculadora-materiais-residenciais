# Calculadora de Materiais para Obra Residencial

Atividade avaliativa — Disciplina: Desenvolvimento de Sistemas (UniCEUB) | Aluno: Guilherme

Sistema para cálculo de materiais em obras residenciais, modelando a planta baixa como um grafo G=(V,A). Inclui API REST e frontend Jakarta Faces com persistência em Supabase.

## Tecnologias

- Java 22 (Amazon Corretto)
- Spring Boot 3.3.5
- Jakarta Faces 4.0 (JSF via Joinfaces 5.3.1 — sem servidor externo, Tomcat embutido)
- Spring Data JPA + Hibernate 6
- PostgreSQL — Supabase (produção) / H2 em memória (perfil local)
- Maven (bundled IntelliJ IDEA)

## Pré-requisitos

- Amazon Corretto 22 instalado em `C:\Users\<usuário>\.jdks\corretto-22.0.2`
- Maven (bundled IntelliJ IDEA ou instalação global)
- Para perfil Supabase: variável `DB_PASSWORD` e conectividade IPv6 com o host `db.*.supabase.co`

## Como rodar

### Perfil LOCAL (H2 em memória — não precisa de Supabase)

```powershell
# Windows PowerShell — substitua o caminho do Java conforme sua instalação
$java = "C:\Users\<usuário>\.jdks\corretto-22.0.2\bin\java.exe"

# Compilar
$mvn = "C:\Program Files\JetBrains\IntelliJ IDEA 2026.1\plugins\maven\lib\maven3\bin\mvn.cmd"
& $mvn package -DskipTests

# Rodar (perfil local com H2)
& $java -Djdk.net.unixdomain.tmpdir=C:\Temp -jar target\calculator-1.0.0.jar --spring.profiles.active=local
```

> **Nota:** O flag `-Djdk.net.unixdomain.tmpdir=C:\Temp` é necessário no Java 22 em Windows quando o diretório de usuário contém caracteres especiais (ex: acentos).

### Perfil PRODUÇÃO (Supabase / PostgreSQL)

```powershell
$env:DB_PASSWORD = "sua-senha-aqui"   # nunca commite a senha
& $java -Djdk.net.unixdomain.tmpdir=C:\Temp -jar target\calculator-1.0.0.jar
```

### URLs

- **Frontend (JSF):** `http://localhost:8080/orcamento.xhtml`
- **Console H2** (perfil local): `http://localhost:8080/h2-console`
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
