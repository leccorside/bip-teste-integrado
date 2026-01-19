-- ============================================
-- Schema do Banco de Dados - Sistema de Benefícios
-- ============================================
-- Este script cria a tabela BENEFICIO com suporte a optimistic locking
-- Campo VERSION é usado para controle de concorrência

CREATE TABLE BENEFICIO (
  ID BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
  NOME VARCHAR(100) NOT NULL,
  DESCRICAO VARCHAR(255),
  VALOR DECIMAL(15,2) NOT NULL CHECK (VALOR >= 0),
  ATIVO BOOLEAN DEFAULT TRUE,
  VERSION BIGINT DEFAULT 0 NOT NULL
);

-- Índices para melhorar performance
CREATE INDEX IDX_BENEFICIO_ATIVO ON BENEFICIO(ATIVO);
CREATE INDEX IDX_BENEFICIO_NOME ON BENEFICIO(NOME);
