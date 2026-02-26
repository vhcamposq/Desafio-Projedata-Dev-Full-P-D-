# Sistema de Gerenciamento de Produção Industrial

Aplicação Full Stack para gerenciamento de insumos e otimização de produção industrial.

## Tecnologias

- **Back-end:** Java 21 + Spring Boot 4
- **Banco de Dados:** H2 (em memória)
- **Front-end:** Vue.js 3 + Vue Router + Axios

## Estrutura do Projeto

```
/
├── production-manager/   # Back-end Spring Boot
└── frontend/             # Front-end Vue.js
```

## Pré-requisitos

- Java 21+
- Node.js 20+
- Git

---

## Como Rodar

### 1. Back-end

```bash
cd production-manager
./mvnw spring-boot:run
```

O servidor sobe em: `http://localhost:8080`

Console do banco H2 disponível em: `http://localhost:8080/h2-console`
- JDBC URL: `jdbc:h2:mem:productiondb`
- Usuário: `sa`
- Senha: *(vazio)*

### 2. Front-end

```bash
cd frontend
npm install
npm run dev
```

A aplicação abre em: `http://localhost:5173`

> **Atenção:** o back-end deve estar rodando antes de usar o front-end.

---

## Funcionalidades

### Matérias-Primas
- Cadastrar, editar, listar e excluir matérias-primas
- Cada matéria-prima possui: código, nome e quantidade em estoque

### Produtos
- Cadastrar, editar, listar e excluir produtos
- Cada produto possui: código, nome, valor e composição (lista de matérias-primas e quantidades necessárias)

### Cálculo de Produção
- Analisa o estoque atual e sugere quais produtos fabricar para obter o **maior valor total de venda**
- Algoritmo **Greedy**: prioriza os produtos de maior valor, resolvendo conflitos quando dois produtos disputam a mesma matéria-prima

---

## Endpoints da API

| Método | Endpoint | Descrição |
|--------|----------|-----------|
| GET | `/api/raw-materials` | Lista matérias-primas |
| POST | `/api/raw-materials` | Cria matéria-prima |
| PUT | `/api/raw-materials/{id}` | Atualiza matéria-prima |
| DELETE | `/api/raw-materials/{id}` | Remove matéria-prima |
| GET | `/api/products` | Lista produtos |
| POST | `/api/products` | Cria produto |
| PUT | `/api/products/{id}` | Atualiza produto |
| DELETE | `/api/products/{id}` | Remove produto |
| GET | `/api/production/suggestion` | Retorna sugestão de produção |

---

## Testes

### Executar testes do back-end

```bash
cd production-manager
./mvnw test
```

Os testes cobrem a lógica do algoritmo de cálculo de produção com 7 cenários, incluindo:
- Estoque suficiente para todos os produtos
- Estoque insuficiente
- Conflito entre produtos que disputam a mesma matéria-prima
- Produto sem ingredientes
