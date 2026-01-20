package com.example.backend;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * Controller para a raiz da aplicação.
 * Redireciona para a documentação Swagger.
 */
@RestController
@CrossOrigin(origins = {"http://localhost:4200", "http://127.0.0.1:4200"})
public class HomeController {

    @GetMapping("/")
    public ResponseEntity<Map<String, Object>> home() {
        return ResponseEntity.ok(Map.of(
            "message", "API de Benefícios - Backend",
            "version", "1.0.0",
            "swagger", "/swagger-ui.html",
            "api-docs", "/api-docs",
            "endpoints", Map.of(
                "GET /api/v1/beneficios", "Listar todos os benefícios",
                "GET /api/v1/beneficios/{id}", "Buscar benefício por ID",
                "POST /api/v1/beneficios", "Criar novo benefício",
                "PUT /api/v1/beneficios/{id}", "Atualizar benefício",
                "DELETE /api/v1/beneficios/{id}", "Deletar benefício",
                "POST /api/v1/beneficios/transferir", "Transferir valor entre benefícios"
            )
        ));
    }
}
