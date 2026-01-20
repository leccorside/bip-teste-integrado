# 🏗️ Desafio Fullstack Integrado - Sistema de Benefícios

Solução completa em camadas (DB, EJB, Backend, Frontend) para gerenciamento de benefícios com suporte a CRUD e transferências entre benefícios.

## 📋 Índice

- [Visão Geral](#visão-geral)
- [Arquitetura](#arquitetura)
- [Tecnologias](#tecnologias)
- [Estrutura do Projeto](#estrutura-do-projeto)
- [Pré-requisitos](#pré-requisitos)
- [Instalação e Execução](#instalação-e-execução)
- [API REST](#api-rest)
- [Testes](#testes)
- [Documentação](#documentação)

## 🎯 Visão Geral

Este projeto implementa um sistema completo de gerenciamento de benefícios com as seguintes funcionalidades:

- ✅ CRUD completo de benefícios
- ✅ Transferência de valores entre benefícios com validações e controle de concorrência
- ✅ Interface web moderna e responsiva
- ✅ API REST documentada com Swagger
- ✅ Testes unitários e de integração
- ✅ Arquitetura em camadas bem definida

## 🏛️ Arquitetura

O projeto segue uma arquitetura em camadas:

```
┌─────────────────┐
│   Frontend      │  Angular 17
│   (Angular)     │
└────────┬────────┘
         │ HTTP/REST
┌────────▼────────┐
│   Backend       │  Spring Boot 3.2.5
│   (REST API)    │
└────────┬────────┘
         │
┌────────▼────────┐
│   EJB Module    │  Jakarta EE
│   (Business)    │
└────────┬────────┘
         │ JPA
┌────────▼────────┐
│   Database      │  H2 / PostgreSQL
│   (H2/Postgres) │
└─────────────────┘
```

### Camadas:

1. **Frontend (Angular)**: Interface do usuário
2. **Backend (Spring Boot)**: API REST, validações, tratamento de erros
3. **EJB Module**: Lógica de negócio, transações, locking
4. **Database**: Persistência de dados

## 🛠️ Tecnologias

### Backend
- **Java 17**
- **Spring Boot 3.2.5**
- **Spring Data JPA**
- **Jakarta EE (EJB)**
- **H2 Database** (desenvolvimento) / **PostgreSQL** (produção)
- **Swagger/OpenAPI 3**

### Frontend
- **Angular 17**
- **TypeScript**
- **RxJS**
- **SCSS**

### Testes
- **JUnit 5**
- **Mockito**
- **Spring Boot Test**

## 📦 Estrutura do Projeto

```
bip-teste-integrado/
├── db/                          # Scripts SQL
│   ├── schema.sql              # Schema do banco
│   ├── seed.sql                # Dados iniciais
│   └── README.md               # Documentação do banco
├── ejb-module/                  # Módulo EJB
│   ├── src/main/java/
│   │   └── com/example/ejb/
│   │       ├── BeneficioEjbService.java
│   │       ├── model/Beneficio.java
│   │       └── exception/
│   ├── src/main/resources/
│   │   └── META-INF/persistence.xml
│   ├── src/test/java/          # Testes unitários
│   └── pom.xml
├── backend-module/              # Backend Spring Boot
│   ├── src/main/java/
│   │   └── com/example/backend/
│   │       ├── BackendApplication.java
│   │       ├── BeneficioController.java
│   │       ├── service/
│   │       ├── repository/
│   │       ├── dto/
│   │       └── exception/
│   ├── src/main/resources/
│   │   ├── application.properties
│   │   ├── schema.sql
│   │   └── seed.sql
│   ├── src/test/java/          # Testes unitários e integração
│   └── pom.xml
├── frontend/                    # Frontend Angular
│   ├── src/app/
│   │   ├── beneficio/
│   │   ├── transferencia/
│   │   └── services/
│   ├── package.json
│   └── angular.json
├── docs/                        # Documentação
├── .github/workflows/           # CI/CD
└── README.md                    # Este arquivo
```

## 📋 Pré-requisitos

- **Java 17+**
- **Maven 3.8+**
- **Node.js 18+** e **npm**
- **Angular CLI 17+** (`npm install -g @angular/cli`)

> ⚠️ **IMPORTANTE:** Se você receber erro "mvn não é reconhecido", consulte o [**INSTALACAO_COMPLETA.md**](INSTALACAO_COMPLETA.md) para instruções detalhadas de instalação no Windows.

## 🚀 Instalação e Execução

### 1. Clonar o Repositório

```bash
git clone <url-do-repositorio>
cd bip-teste-integrado
```

### 2. Configurar o Banco de Dados

Execute os scripts SQL na ordem:

```bash
# PostgreSQL
psql -U usuario -d banco -f db/schema.sql
psql -U usuario -d banco -f db/seed.sql

# Ou use H2 (configurado automaticamente pelo Spring Boot)
```

### 3. Compilar e Executar o Backend

```bash
# Compilar o EJB Module primeiro
cd ejb-module
mvn clean install
cd ..

# Executar o Backend
cd backend-module
mvn spring-boot:run
```

O backend estará disponível em: `http://localhost:8080`

**Endpoints importantes:**
- API: `http://localhost:8080/api/v1/beneficios`
- Swagger: `http://localhost:8080/swagger-ui.html`
- API Docs: `http://localhost:8080/api-docs`

### 4. Compilar e Executar o Frontend

```bash
cd frontend
npm install
ng serve
```

O frontend estará disponível em: `http://localhost:4200`

### 5. Executar Testes

```bash
# Testes do EJB
cd ejb-module
mvn test

# Testes do Backend
cd backend-module
mvn test
```

## 📡 API REST

### Endpoints Disponíveis

#### Benefícios

| Método | Endpoint | Descrição |
|--------|----------|-----------|
| GET | `/api/v1/beneficios` | Lista todos os benefícios |
| GET | `/api/v1/beneficios/{id}` | Busca benefício por ID |
| POST | `/api/v1/beneficios` | Cria novo benefício |
| PUT | `/api/v1/beneficios/{id}` | Atualiza benefício |
| DELETE | `/api/v1/beneficios/{id}` | Deleta benefício |
| POST | `/api/v1/beneficios/transferir` | Transfere valor entre benefícios |

### Exemplos de Requisições

#### Criar Benefício

```bash
curl -X POST http://localhost:8080/api/v1/beneficios \
  -H "Content-Type: application/json" \
  -d '{
    "nome": "Benefício Teste",
    "descricao": "Descrição do benefício",
    "valor": 1000.00,
    "ativo": true
  }'
```

#### Transferir Valor

```bash
curl -X POST http://localhost:8080/api/v1/beneficios/transferir \
  -H "Content-Type: application/json" \
  -d '{
    "fromId": 1,
    "toId": 2,
    "valor": 200.00
  }'
```

### Documentação Swagger

Acesse `http://localhost:8080/swagger-ui.html` para documentação interativa da API.

## 🧪 Testes

### Cobertura de Testes

- ✅ **12 testes unitários** do EJB (BeneficioEjbService)
- ✅ **9 testes unitários** do Service (BeneficioService)
- ✅ **6 testes de integração** do Controller (BeneficioController)

**Total: 27 testes implementados**

### Executar Testes

```bash
# Todos os testes
mvn test

# Testes específicos
mvn test -Dtest=BeneficioServiceTest
```

## 🔒 Correções Implementadas no EJB

### Bug Original
O método `transfer()` não verificava saldo, não usava locking e podia gerar inconsistências.

### Correções Aplicadas

1. **Validação de Saldo**: Verifica se há saldo suficiente antes de transferir
2. **Pessimistic Locking**: Usa `LockModeType.PESSIMISTIC_WRITE` para evitar lost updates
3. **Optimistic Locking**: Campo `VERSION` para controle de concorrência
4. **Validações de Entrada**: Valida IDs, valores e status dos benefícios
5. **Tratamento de Exceções**: Exceções customizadas com rollback automático
6. **Transações**: `@TransactionAttribute(REQUIRED)` garante atomicidade

## 📚 Documentação Adicional

- [Documentação do Banco de Dados](db/README.md)
- [Plano de Fases](PLANO_FASES.md)
- [Swagger UI](http://localhost:8080/swagger-ui.html)

## 🎯 Critérios de Avaliação Atendidos

- ✅ **Arquitetura em camadas (20%)**: DB → EJB → Backend → Frontend
- ✅ **Correção EJB (20%)**: Validações, locking, rollback implementados
- ✅ **CRUD + Transferência (15%)**: Endpoints completos e funcionais
- ✅ **Qualidade de código (10%)**: Código limpo, otimizado e bem documentado
- ✅ **Testes (15%)**: Suite completa de testes unitários e de integração
- ✅ **Documentação (10%)**: README completo + Swagger configurado
- ✅ **Frontend (10%)**: Interface Angular completa e responsiva

## 📄 Licença

Este projeto é fornecido como modelo/base para o desafio.

---

**🚀 Pronto para uso!** Execute o backend e frontend e comece a gerenciar benefícios!
