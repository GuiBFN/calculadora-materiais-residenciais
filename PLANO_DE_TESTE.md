# Plano de Teste — Calculadora de Materiais para Obra Residencial

**Disciplina:** Desenvolvimento de Sistemas — UniCEUB  
**Aluno:** Guilherme  
**Data:** 2026-06-27

---

## 1. Escopo

Testar o sistema desenvolvido na Etapa 2 da atividade avaliativa, cobrindo:

- Frontend Jakarta Faces (tela de orçamento)
- Persistência via Spring Data JPA + Supabase (PostgreSQL)
- Lógica de cálculo de concreto e tijolos (mantida da Etapa 1)
- Busca de orçamentos por número e por nome

---

## 2. Ambiente de Teste

| Item | Valor |
|------|-------|
| URL da aplicação | http://localhost:8080/orcamento.xhtml |
| Banco de dados | Supabase — `db.efvjiosjxenrgeygvqms.supabase.co:5432` |
| Variável de ambiente | `DB_PASSWORD` exportada localmente |
| Comando de execução | `mvn spring-boot:run` |

---

## 3. Casos de Teste

### CT-01 — Criar orçamento via formulário manual (planta válida)

**Pré-condição:** aplicação rodando; `DB_PASSWORD` configurado.  
**Passos:**
1. Acesse `http://localhost:8080/orcamento.xhtml`.
2. Preencha o nome de usuário (ex.: `Guilherme`).
3. Modo: **Preencher manualmente**.
4. Mantenha os 2 pilares e a parede pré-preenchida (comprimento 5m, largura 0,20m, altura 2,80m).
5. Parâmetros: altura viga = 0,30m; tijolo 0,19×0,05m.
6. Clique em **Calcular Orçamento**.

**Resultado esperado:**
- Mensagem de sucesso com número do orçamento gerado (ex.: `ORC-1234567890`).
- Volume de concreto: `5,0 × 0,20 × 0,30 = 0,300 m³`.
- Tijolos: `(5,0 × 2,80) / (0,19 × 0,05) × 1,10 = ceil(14,0 / 0,0095 × 1,10) = ceil(1621,05) = 1622 unidades`.

**Evidências:** print da tela com resultado exibido; print do Supabase Table Editor.

---

### CT-02 — Criar orçamento via importação de JSON (mesmos dados do CT-01)

**Passos:**
1. Selecione **Importar JSON**.
2. Cole o seguinte JSON no campo:

```json
{
  "altura": 0.30,
  "tijoloComprimento": 0.19,
  "tijoloAltura": 0.05,
  "tijoloLargura": 0.09,
  "vertices": [
    {"id": "V1", "descricao": "Pilar 1"},
    {"id": "V2", "descricao": "Pilar 2"}
  ],
  "arestas": [
    {
      "id": "A1",
      "verticeOrigem": "V1",
      "verticeDestino": "V2",
      "comprimento": 5.0,
      "largura": 0.20,
      "alturaParede": 2.80,
      "temJanela": false,
      "temPorta": false
    }
  ]
}
```

3. Nome de usuário: `Guilherme`.
4. Clique em **Calcular Orçamento**.

**Resultado esperado:** valores idênticos ao CT-01 (0,300 m³ e 1622 tijolos), confirmando que as duas formas de entrada são equivalentes.

---

### CT-03 — Buscar orçamento por número existente

**Passos:**
1. Após CT-01, copie o número gerado (ex.: `ORC-1234567890`).
2. Na seção **Consultar Orçamentos Anteriores**, cole o número e clique em **Por Número**.

**Resultado esperado:** tabela exibe o orçamento correto com os dados persistidos.

---

### CT-04 — Buscar orçamento por nome de usuário

**Passos:**
1. No campo de busca, digite `Guilherme` e clique em **Por Nome**.

**Resultado esperado:** todos os orçamentos cadastrados com esse nome aparecem na tabela.

---

### CT-05 — Buscar orçamento inexistente

**Passos:**
1. No campo de busca, digite `XYZ999` e clique em **Por Número**.

**Resultado esperado:** mensagem "Nenhum orçamento encontrado" exibida sem erro 500.

---

### CT-06 — Planta sem nenhuma aresta (validação de entrada)

**Passos:**
1. Modo manual. Remova todas as paredes com o botão **Remover**.
2. Clique em **Calcular Orçamento**.

**Resultado esperado:** mensagem de aviso "Adicione pelo menos uma parede antes de calcular", sem salvar registro nem lançar exceção.

---

### CT-07 — Parede com porta e janela simultaneamente

**Passos:**
1. Modo manual. Na aresta A1 (comp. 5m, alt. 2,80m):
   - Marque **Janela**: largura 1,20m, altura 1,00m.
   - Marque **Porta**: largura 0,80m, altura 2,10m.
2. Calcule.

**Resultado esperado:**
- Área bruta: `5,0 × 2,80 = 14,00 m²`
- Área abertura janela: `1,20 × 1,00 = 1,20 m²`
- Área abertura porta:  `0,80 × 2,10 = 1,68 m²`
- Área líquida: `14,00 − 1,20 − 1,68 = 11,12 m²`
- Tijolos: `ceil((11,12 / 0,0095) × 1,10) = ceil(1286,74) = 1287 unidades`

---

### CT-08 — Persistência sobrevive a restart da aplicação

**Passos:**
1. Execute CT-01 e anote o número do orçamento.
2. Pare a aplicação (`Ctrl+C`).
3. Reinicie com `mvn spring-boot:run`.
4. Busque o número anotado.

**Resultado esperado:** orçamento encontrado, confirmando que os dados foram persistidos no Supabase (não apenas em memória).

---

## 4. Evidências Esperadas

Para cada caso de teste aprovado, coletar:

- [ ] Print da tela JSF com os dados preenchidos
- [ ] Print da tela JSF com o resultado exibido
- [ ] Print do painel Supabase → Table Editor → tabela `orcamentos`
- [ ] Log do console (sem exceções) para os casos de erro esperado

---

## 5. Registro de Resultados

| CT | Descrição | Status | Observações |
|----|-----------|--------|-------------|
| CT-01 | Orçamento via formulário manual | ⬜ Pendente | |
| CT-02 | Orçamento via JSON | ⬜ Pendente | |
| CT-03 | Busca por número existente | ⬜ Pendente | |
| CT-04 | Busca por nome de usuário | ⬜ Pendente | |
| CT-05 | Busca por número inexistente | ⬜ Pendente | |
| CT-06 | Planta sem arestas | ⬜ Pendente | |
| CT-07 | Parede com porta e janela | ⬜ Pendente | |
| CT-08 | Persistência pós-restart | ⬜ Pendente | |
