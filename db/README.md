# 📊 Banco de Dados - Sistema de Benefícios

## 📋 Descrição

Scripts SQL para criação e inicialização do banco de dados do sistema de benefícios.

## 🗂️ Arquivos

- **`schema.sql`**: Script de criação da tabela `BENEFICIO`
- **`seed.sql`**: Script de inserção de dados iniciais

## 🚀 Como Executar

### Opção 1: PostgreSQL

```bash
# Conectar ao PostgreSQL
psql -U seu_usuario -d seu_banco

# Executar schema
\i schema.sql

# Executar seed
\i seed.sql
```

### Opção 2: H2 (Para desenvolvimento/testes)

O H2 é configurado automaticamente pelo Spring Boot para testes. Os scripts podem ser executados via `schema.sql` e `data.sql` no classpath.

### Opção 3: Via Spring Boot (automático)

Se configurado no `application.properties`:
```properties
spring.sql.init.mode=always
spring.sql.init.schema-locations=classpath:schema.sql
spring.sql.init.data-locations=classpath:seed.sql
```

## 📐 Estrutura da Tabela

| Campo | Tipo | Descrição |
|-------|------|-----------|
| ID | BIGINT | Chave primária (auto-incremento) |
| NOME | VARCHAR(100) | Nome do benefício (obrigatório) |
| DESCRICAO | VARCHAR(255) | Descrição do benefício |
| VALOR | DECIMAL(15,2) | Valor do benefício (>= 0) |
| ATIVO | BOOLEAN | Status ativo/inativo (default: TRUE) |
| VERSION | BIGINT | Controle de versão para optimistic locking |

## 🔒 Controle de Concorrência

O campo `VERSION` é utilizado para implementar **optimistic locking**, garantindo que atualizações concorrentes sejam detectadas e tratadas adequadamente.

## ✅ Validações

- Valor do benefício deve ser >= 0 (constraint CHECK)
- Nome é obrigatório (NOT NULL)

## 📝 Notas

- Os scripts são compatíveis com PostgreSQL e H2
- O campo VERSION é essencial para o controle de concorrência no EJB
- Os índices melhoram a performance de consultas por status e nome
