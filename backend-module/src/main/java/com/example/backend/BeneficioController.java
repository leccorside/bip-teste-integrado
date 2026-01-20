package com.example.backend;

import com.example.backend.dto.BeneficioRequest;
import com.example.backend.dto.BeneficioResponse;
import com.example.backend.dto.TransferenciaRequest;
import com.example.backend.service.BeneficioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Controller REST para operações com Benefícios.
 * Expõe endpoints CRUD e transferência.
 */
@RestController
@RequestMapping("/api/v1/beneficios")
@CrossOrigin(origins = {"http://localhost:4200", "http://127.0.0.1:4200"})
@Tag(name = "Benefícios", description = "API para gerenciamento de benefícios")
public class BeneficioController {

    private final BeneficioService service;

    @Autowired
    public BeneficioController(BeneficioService service) {
        this.service = service;
    }

    /**
     * Lista todos os benefícios.
     */
    @GetMapping
    @Operation(summary = "Listar todos os benefícios", description = "Retorna uma lista com todos os benefícios cadastrados")
    @ApiResponse(responseCode = "200", description = "Lista de benefícios retornada com sucesso")
    public ResponseEntity<List<BeneficioResponse>> listarTodos() {
        List<BeneficioResponse> beneficios = service.findAll();
        return ResponseEntity.ok(beneficios);
    }

    /**
     * Busca um benefício por ID.
     */
    @GetMapping("/{id}")
    @Operation(summary = "Buscar benefício por ID", description = "Retorna os detalhes de um benefício específico")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Benefício encontrado"),
            @ApiResponse(responseCode = "404", description = "Benefício não encontrado")
    })
    public ResponseEntity<BeneficioResponse> buscarPorId(
            @Parameter(description = "ID do benefício") @PathVariable Long id) {
        BeneficioResponse beneficio = service.findById(id);
        return ResponseEntity.ok(beneficio);
    }

    /**
     * Cria um novo benefício.
     */
    @PostMapping
    @Operation(summary = "Criar novo benefício", description = "Cria um novo benefício com os dados fornecidos")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Benefício criado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos")
    })
    public ResponseEntity<BeneficioResponse> criar(@Valid @RequestBody BeneficioRequest request) {
        BeneficioResponse beneficio = service.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(beneficio);
    }

    /**
     * Atualiza um benefício existente.
     */
    @PutMapping("/{id}")
    @Operation(summary = "Atualizar benefício", description = "Atualiza os dados de um benefício existente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Benefício atualizado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Benefício não encontrado"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos"),
            @ApiResponse(responseCode = "409", description = "Conflito de concorrência (optimistic locking)")
    })
    public ResponseEntity<BeneficioResponse> atualizar(
            @Parameter(description = "ID do benefício") @PathVariable Long id,
            @Valid @RequestBody BeneficioRequest request) {
        BeneficioResponse beneficio = service.update(id, request);
        return ResponseEntity.ok(beneficio);
    }

    /**
     * Deleta um benefício.
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "Deletar benefício", description = "Remove um benefício do sistema")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Benefício deletado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Benefício não encontrado")
    })
    public ResponseEntity<Void> deletar(
            @Parameter(description = "ID do benefício") @PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Transfere valor entre dois benefícios.
     */
    @PostMapping("/transferir")
    @Operation(summary = "Transferir valor entre benefícios", 
               description = "Transfere um valor de um benefício para outro. Valida saldo e usa locking para evitar inconsistências.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Transferência realizada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos ou saldo insuficiente"),
            @ApiResponse(responseCode = "404", description = "Benefício não encontrado"),
            @ApiResponse(responseCode = "409", description = "Conflito de concorrência (optimistic locking)")
    })
    public ResponseEntity<Map<String, String>> transferir(@Valid @RequestBody TransferenciaRequest request) {
        service.transferir(request.getFromId(), request.getToId(), request.getValor());
        return ResponseEntity.ok(Map.of(
                "message", "Transferência realizada com sucesso",
                "fromId", request.getFromId().toString(),
                "toId", request.getToId().toString(),
                "valor", request.getValor().toString()
        ));
    }
}
