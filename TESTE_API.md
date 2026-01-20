# 🧪 Como Testar a API

## ✅ Aplicação está rodando!

A aplicação iniciou com sucesso na porta **8080**.

## 📋 Endpoints Disponíveis

### 1. Informações da API
```
GET http://localhost:8080/
```

### 2. Documentação Swagger
```
http://localhost:8080/swagger-ui.html
```

### 3. API de Benefícios

#### Listar todos os benefícios
```bash
GET http://localhost:8080/api/v1/beneficios
```

#### Buscar por ID
```bash
GET http://localhost:8080/api/v1/beneficios/1
```

#### Criar benefício
```bash
POST http://localhost:8080/api/v1/beneficios
Content-Type: application/json

{
  "nome": "Benefício Teste",
  "descricao": "Descrição do teste",
  "valor": 1000.00,
  "ativo": true
}
```

#### Atualizar benefício
```bash
PUT http://localhost:8080/api/v1/beneficios/1
Content-Type: application/json

{
  "nome": "Benefício Atualizado",
  "descricao": "Nova descrição",
  "valor": 1500.00,
  "ativo": true
}
```

#### Deletar benefício
```bash
DELETE http://localhost:8080/api/v1/beneficios/1
```

#### Transferir valor entre benefícios
```bash
POST http://localhost:8080/api/v1/beneficios/transferir
Content-Type: application/json

{
  "fromId": 1,
  "toId": 2,
  "valor": 100.00
}
```

## 🧪 Testando com PowerShell

```powershell
# Listar benefícios
Invoke-RestMethod -Uri "http://localhost:8080/api/v1/beneficios" -Method Get

# Criar benefício
$body = @{
    nome = "Benefício Teste"
    descricao = "Descrição do teste"
    valor = 1000.00
    ativo = $true
} | ConvertTo-Json

Invoke-RestMethod -Uri "http://localhost:8080/api/v1/beneficios" -Method Post -Body $body -ContentType "application/json"

# Transferir
$transfer = @{
    fromId = 1
    toId = 2
    valor = 100.00
} | ConvertTo-Json

Invoke-RestMethod -Uri "http://localhost:8080/api/v1/beneficios/transferir" -Method Post -Body $transfer -ContentType "application/json"
```

## 🌐 Testando no Navegador

1. Acesse: http://localhost:8080/swagger-ui.html
2. Use a interface Swagger para testar todos os endpoints

## ✅ Status

- ✅ Aplicação rodando
- ✅ Banco de dados H2 inicializado
- ✅ Endpoints disponíveis
- ✅ Swagger configurado

Os avisos sobre recursos estáticos são normais e não afetam o funcionamento da API!
