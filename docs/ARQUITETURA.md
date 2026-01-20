# 🏛️ Documentação de Arquitetura - Sistema de Benefícios

## 📋 Visão Geral

Este documento descreve as decisões arquiteturais tomadas no desenvolvimento do sistema de benefícios.

## 🏗️ Arquitetura em Camadas

O sistema foi desenvolvido seguindo uma arquitetura em camadas bem definida, separando responsabilidades e garantindo manutenibilidade e escalabilidade.

### Camada 1: Database (Banco de Dados)

**Responsabilidade:** Persistência de dados

**Tecnologias:**
- H2 Database (desenvolvimento/testes)
- PostgreSQL (produção)

**Decisões:**
- Uso de `GENERATED ALWAYS AS IDENTITY` para chaves primárias
- Campo `VERSION` para optimistic locking
- Índices em campos frequentemente consultados (ATIVO, NOME)
- Constraint CHECK para garantir VALOR >= 0

### Camada 2: EJB Module (Lógica de Negócio)

**Responsabilidade:** Lógica de negócio e controle transacional

**Tecnologias:**
- Jakarta EE (EJB)
- Jakarta Persistence API (JPA)

**Decisões Arquiteturais:**

1. **Pessimistic Locking:**
   - Uso de `LockModeType.PESSIMISTIC_WRITE` no método `transfer()`
   - Garante que nenhuma outra transação possa modificar os registros simultaneamente
   - Evita lost updates em operações críticas de transferência

2. **Optimistic Locking:**
   - Campo `@Version` na entidade `Beneficio`
   - Detecta conflitos de concorrência no momento do merge
   - Mais eficiente que pessimistic locking para leituras

3. **Validações de Negócio:**
   - Validações de entrada no EJB (não apenas na camada de apresentação)
   - Garantia de integridade mesmo se o backend não validar

4. **Exceções Customizadas:**
   - `BeneficioNaoEncontradoException`
   - `SaldoInsuficienteException`
   - `OptimisticLockException`
   - Permitem tratamento específico em cada camada

5. **Transações:**
   - `@TransactionAttribute(REQUIRED)` garante atomicidade
   - Rollback automático em caso de exceção

### Camada 3: Backend (Spring Boot)

**Responsabilidade:** API REST, validações de entrada, tratamento de erros

**Tecnologias:**
- Spring Boot 3.2.5
- Spring Data JPA
- Spring Validation
- Swagger/OpenAPI

**Decisões Arquiteturais:**

1. **DTOs (Data Transfer Objects):**
   - Separação entre entidades JPA e objetos de transferência
   - `BeneficioRequest` para criação/atualização
   - `BeneficioResponse` para respostas
   - `TransferenciaRequest` para transferências
   - Evita exposição de detalhes internos da entidade

2. **Service Layer:**
   - Encapsula lógica de negócio adicional
   - Integra com EJB para operações complexas (transferências)
   - Conversão entre entidades e DTOs

3. **GlobalExceptionHandler:**
   - Tratamento centralizado de exceções
   - Mapeamento de exceções do EJB para HTTP
   - Respostas padronizadas de erro

4. **Validações Bean Validation:**
   - Validações de entrada com anotações
   - Mensagens de erro claras e consistentes

5. **Swagger/OpenAPI:**
   - Documentação interativa da API
   - Facilita teste e integração

### Camada 4: Frontend (Angular)

**Responsabilidade:** Interface do usuário

**Tecnologias:**
- Angular 17 (standalone components)
- TypeScript
- RxJS
- SCSS

**Decisões Arquiteturais:**

1. **Standalone Components:**
   - Arquitetura moderna do Angular 17
   - Sem necessidade de módulos
   - Código mais limpo e simples

2. **Services:**
   - `BeneficioService` centraliza comunicação HTTP
   - Facilita reutilização e manutenção

3. **Reactive Forms:**
   - Validações no cliente
   - Melhor experiência do usuário

4. **Tratamento de Erros:**
   - Exibição clara de mensagens de erro
   - Estados de loading e sucesso

## 🔒 Controle de Concorrência

### Estratégia Híbrida

O sistema utiliza uma estratégia híbrida de locking:

1. **Pessimistic Locking:**
   - Usado no método `transfer()` do EJB
   - Bloqueia registros durante a operação
   - Garante consistência em operações críticas

2. **Optimistic Locking:**
   - Campo `VERSION` na entidade
   - Detecta conflitos no momento do merge
   - Mais eficiente para operações menos críticas

### Fluxo de Transferência

```
1. Buscar benefícios com PESSIMISTIC_WRITE (bloqueia registros)
2. Validar saldo suficiente
3. Validar status dos benefícios
4. Realizar cálculo da transferência
5. Merge com optimistic locking (campo VERSION verificado)
6. Rollback automático se houver conflito
```

## 🧪 Testes

### Estratégia de Testes

1. **Testes Unitários (EJB):**
   - Testam lógica de negócio isoladamente
   - Usam Mockito para mockar EntityManager
   - Cobertura de casos de sucesso e erro

2. **Testes Unitários (Service):**
   - Testam camada de serviço
   - Mock do repository e EJB
   - Validação de conversões e integrações

3. **Testes de Integração (Controller):**
   - Testam endpoints REST completos
   - Usam MockMvc do Spring Boot
   - Validação de HTTP status e JSON responses

## 📊 Decisões de Design

### Por que EJB?

- Controle transacional declarativo
- Integração nativa com JPA
- Suporte a locking avançado
- Padrão Enterprise Java

### Por que Spring Boot?

- Facilidade de desenvolvimento
- Integração com Spring Data JPA
- Suporte a testes
- Ecossistema robusto

### Por que Angular?

- Framework moderno e completo
- TypeScript para type safety
- Reactive programming com RxJS
- Standalone components (sem módulos)

## 🚀 Escalabilidade

### Considerações Futuras

1. **Cache:**
   - Implementar cache para consultas frequentes
   - Redis ou EhCache

2. **Mensageria:**
   - Operações assíncronas para transferências grandes
   - RabbitMQ ou Kafka

3. **Microserviços:**
   - Separar serviços por domínio
   - API Gateway

4. **Monitoramento:**
   - Logs estruturados
   - Métricas com Prometheus
   - Tracing distribuído

## 📝 Padrões Utilizados

- **Repository Pattern**: Separação de acesso a dados
- **DTO Pattern**: Transferência de dados entre camadas
- **Service Layer Pattern**: Lógica de negócio encapsulada
- **Exception Handler Pattern**: Tratamento centralizado de erros
- **Dependency Injection**: Inversão de controle

---

**Última atualização:** FASE 6 - Documentação e CI/CD
