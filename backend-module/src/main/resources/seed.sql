INSERT INTO BENEFICIO (NOME, DESCRICAO, VALOR, ATIVO, VERSION) VALUES
('Beneficio A', 'Descrição do Benefício A', 1000.00, TRUE, 0),
('Beneficio B', 'Descrição do Benefício B', 500.00, TRUE, 0)
ON CONFLICT DO NOTHING;
