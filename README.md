# Calculadora de Materiais para Obra Residencial

Atividade avaliativa — Disciplina: Desenvolvimento de Sistemas (UniCEUB)

Sistema de orçamento para obras residenciais que calcula consumo de materiais (concreto e tijolos) a partir da planta baixa, modelada como um grafo G=(V,A) — onde vértices são pilares e arestas são paredes. Inclui API REST, frontend em Jakarta Faces (JSF) e persistência em banco de dados (Supabase/PostgreSQL).

## Tecnologias

- Java 21 / 22
- Spring Boot 3.3.5
- Jakarta Faces (JSF) via JoinFaces 5.3.1 — sem necessidade de servidor de aplicação externo
- Spring Data JPA + PostgreSQL (Supabase) em produção / H2 em memória para testes locais
- Maven

## Funcionalidades

- **Cálculo de concreto**: volume de vigas baldrame por parede
- **Cálculo de tijolos**: quantidade por parede, descontando vãos de portas/janelas, com 10% de perda
- **Tela de orçamento** (`/orcamento.xhtml`) com visual dark industrial:
  - Modo **Manual**: formulário para informar vértices (pilares) e arestas (paredes) individualmente, com suporte a portas e janelas por parede
  - Modo **JSON**: importação da planta completa colando um JSON no schema dos DTOs da API
  - Geração de orçamento único (concreto + tijolos calculados juntos), salvo com número único `ORC-{timestamp}`
  - Busca de orçamentos anteriores por número ou por nome de usuário
  - Redirecionamento automático de `/` para a tela de orçamento
- **Validações de entrada**: valores negativos ou zero em qualquer campo numérico são rejeitados com mensagem de erro específica
- **Persistência real**: cada orçamento é salvo no banco de dados, sobrevivendo a reinicializações da aplicação

## Interface

A tela principal usa um design dark industrial com:

- Paleta escura (`#0f1117` / `#1a1d27`) com destaque laranja (`#f5a623`) e teal (`#4ecdc4`)
- Fontes **Share Tech Mono** (cabeçalhos) e **Barlow** (corpo), via Google Fonts
- Cards com borda superior colorida e animação de entrada
- Toggle de modo como botões-aba estilizados
- Tabelas de pilares/paredes com cabeçalho escuro e rótulos em laranja
- Card de resultado com borda verde e valores em teal

## Como rodar

### Opção 1 — Com Supabase (produção, persistência real)

Pré-requisito: variável de ambiente `DB_PASSWORD` configurada (nunca commitada no código).

```powershell
$env:DB_PASSWORD = "sua-senha-do-supabase"
mvn spring-boot:run
```

A aplicação conecta ao Supabase via **Session Mode Pooler** (IPv4), configurado em `application.properties`:

| Parâmetro | Valor |
|---|---|
| Host | `aws-1-us-east-1.pooler.supabase.com` |
| Porta | `5432` |
| Usuário | `postgres.efvjiosjxenrgeygvqms` |
| Senha | lida de `${DB_PASSWORD}` |

> O host de conexão direta (`db.*.supabase.co`) resolve apenas em IPv6 (registro AAAA) e não funciona em redes/máquinas sem IPv6 global. Por isso, a conexão de produção usa o Session Pooler, que é IPv4 e não tem essa limitação.

### Opção 2 — Local com H2 (sem depender de internet ou credenciais)

Para testar a aplicação sem configurar o Supabase, use o perfil `local`, que roda com banco H2 em memória:

```powershell
mvn package -DskipTests
& $java -Djdk.net.unixdomain.tmpdir=C:\Temp -jar target\calculator-1.0.0.jar --spring.profiles.active=local
```

- Schema criado automaticamente pelo Hibernate
- Console H2 disponível em `http://localhost:8080/h2-console`
- **Atenção**: dados não persistem entre reinicializações neste modo — útil apenas para teste rápido da interface, não substitui a evidência de persistência real exigida pela atividade

Em ambos os casos, a aplicação fica disponível em `http://localhost:8080` (redireciona automaticamente para `/orcamento.xhtml`).

#### Nota técnica — Java 22 no Windows

Se estiver usando Java 22 no Windows e o diretório de usuário tiver caracteres acentuados (ex: `C:\Users\Usuário`), a aplicação pode falhar ao tentar criar Unix Domain Sockets no diretório temporário padrão. A flag `-Djdk.net.unixdomain.tmpdir=C:\Temp` contorna isso, apontando para um caminho sem acentos. Antes de rodar, crie o diretório caso não exista:

```powershell
New-Item -ItemType Directory -Force C:\Temp
```

## Segurança

A senha do banco de dados **nunca é commitada** no repositório. Ela é sempre lida da variável de ambiente `DB_PASSWORD`, configurada localmente pelo desenvolvedor antes de iniciar a aplicação. Caso um arquivo `.env` seja criado para conveniência local, ele deve estar listado no `.gitignore`.

## Endpoints REST

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
    ├── CalculadoraController.java
    └── HomeController.java      # Redireciona / → /orcamento.xhtml

src/main/resources/
├── application.properties           # Produção (Supabase)
├── application-local.properties     # Perfil local (H2)
└── META-INF/
    ├── faces-config.xml
    └── resources/
        └── orcamento.xhtml          # Tela principal JSF
```

## Plano de Teste

Ver [PLANO_DE_TESTE.md](PLANO_DE_TESTE.md) para os 8 casos de teste documentados e executados.

**Resultado da execução em 27/06/2026 — todos os casos passaram:**

| CT | Descrição | Status |
|----|-----------|--------|
| CT-01 | Orçamento via formulário manual | ✅ PASSOU |
| CT-02 | Orçamento via importação JSON | ✅ PASSOU |
| CT-03 | Busca por número existente | ✅ PASSOU |
| CT-04 | Busca por nome de usuário | ✅ PASSOU |
| CT-05 | Busca por número inexistente | ✅ PASSOU |
| CT-06 | Planta sem nenhuma aresta (validação) | ✅ PASSOU |
| CT-07 | Parede com porta e janela simultaneamente | ✅ PASSOU |
| CT-08 | Persistência sobrevive a restart | ✅ PASSOU |

**Correções e melhorias aplicadas em 29/06/2026:**

| ID | Descrição |
|----|-----------|
| BUG-1 | Botão Remover atualiza tabela imediatamente via AJAX |
| BUG-2 | JSON com `alturaParede` ausente ou zero é rejeitado antes do cálculo |
| BUG-3 | Valores negativos ou zero em campos numéricos bloqueiam o cálculo com mensagem de erro |
| UX-1 | Erros de parsing JSON exibem mensagem amigável em vez do stack trace do Jackson |
| UX-2 | `h:messages` com `globalOnly="false"` captura erros de conversão de campos numéricos |
| UX-4 | Clicar nos botões de busca com campo vazio exibe orientação ao usuário |
| ❌-1 | Raiz `/` redireciona automaticamente para `/orcamento.xhtml` (era 404) |
