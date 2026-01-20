# 🔧 Troubleshooting - Erros Comuns

## ❌ Erro: "Process terminated with exit code: 1"

Este erro geralmente indica um problema durante a inicialização da aplicação Spring Boot.

### 🔍 Passos para Diagnosticar

1. **Execute com mais detalhes:**
   ```powershell
   cd backend-module
   mvn spring-boot:run -e -X
   ```
   Isso mostrará o stack trace completo do erro.

2. **Verifique se o ejb-module foi compilado:**
   ```powershell
   cd ejb-module
   mvn clean install
   cd ..
   ```

3. **Limpe e recompile tudo:**
   ```powershell
   # Do diretório raiz
   mvn clean install
   ```

4. **Verifique os logs do Spring Boot:**
   Procure por erros como:
   - "Could not find artifact"
   - "Bean creation failed"
   - "EntityManager not found"
   - "No persistence unit"

### 🛠️ Soluções Comuns

#### Problema: Entidades Duplicadas

**Sintoma:** Erro sobre múltiplas entidades mapeando a mesma tabela

**Solução:** O projeto tem duas entidades `Beneficio`:
- `com.example.backend.model.Beneficio` (usada pelo Spring Boot)
- `com.example.ejb.model.Beneficio` (usada pelo EJB)

A configuração atual escaneia apenas a entidade do backend. Se houver conflito, verifique o `BackendApplication.java`.

#### Problema: EntityManager não injetado

**Sintoma:** Erro "No EntityManager available"

**Solução:** Verifique se o `EjbConfig` está sendo carregado corretamente e se o `EntityManager` está disponível.

#### Problema: Dependência não encontrada

**Sintoma:** "Could not find artifact com.example:ejb-module"

**Solução:**
```powershell
cd ejb-module
mvn clean install
cd ..
cd backend-module
mvn clean install
```

### 📋 Checklist de Verificação

- [ ] `ejb-module` compilado e instalado (`mvn install`)
- [ ] `backend-module` compilado (`mvn compile`)
- [ ] Sem erros de compilação
- [ ] Banco de dados H2 configurado corretamente
- [ ] Entidades escaneadas corretamente

### 🆘 Se Nada Funcionar

Execute e compartilhe o output completo:

```powershell
cd backend-module
mvn spring-boot:run -e 2>&1 | Tee-Object -FilePath error.log
```

Envie o conteúdo do arquivo `error.log` para análise.
