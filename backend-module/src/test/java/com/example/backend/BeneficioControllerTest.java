package com.example.backend;

import com.example.backend.dto.BeneficioRequest;
import com.example.backend.dto.BeneficioResponse;
import com.example.backend.dto.TransferenciaRequest;
import com.example.backend.service.BeneficioService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Testes de integração para BeneficioController.
 * Usa @SpringBootTest com configuração de teste que exclui JPA e EJB.
 */
@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.MOCK,
    classes = ControllerTestConfig.class
)
@AutoConfigureMockMvc
class BeneficioControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BeneficioService service;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testListarTodos() throws Exception {
        // Arrange
        BeneficioResponse response = new BeneficioResponse(1L, "Teste", "Desc", 
            new BigDecimal("1000.00"), true, 0L);
        List<BeneficioResponse> responses = Arrays.asList(response);
        when(service.findAll()).thenReturn(responses);

        // Act & Assert
        mockMvc.perform(get("/api/v1/beneficios"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nome").value("Teste"));
    }

    @Test
    void testBuscarPorId() throws Exception {
        // Arrange
        BeneficioResponse response = new BeneficioResponse(1L, "Teste", "Desc", 
            new BigDecimal("1000.00"), true, 0L);
        when(service.findById(1L)).thenReturn(response);

        // Act & Assert
        mockMvc.perform(get("/api/v1/beneficios/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nome").value("Teste"));
    }

    @Test
    void testCriar() throws Exception {
        // Arrange
        BeneficioRequest request = new BeneficioRequest();
        request.setNome("Novo");
        request.setValor(new BigDecimal("500.00"));

        BeneficioResponse response = new BeneficioResponse(1L, "Novo", null, 
            new BigDecimal("500.00"), true, 0L);
        when(service.create(any(BeneficioRequest.class))).thenReturn(response);

        // Act & Assert
        mockMvc.perform(post("/api/v1/beneficios")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nome").value("Novo"));
    }

    @Test
    void testAtualizar() throws Exception {
        // Arrange
        BeneficioRequest request = new BeneficioRequest();
        request.setNome("Atualizado");
        request.setValor(new BigDecimal("1500.00"));

        BeneficioResponse response = new BeneficioResponse(1L, "Atualizado", null, 
            new BigDecimal("1500.00"), true, 0L);
        when(service.update(eq(1L), any(BeneficioRequest.class))).thenReturn(response);

        // Act & Assert
        mockMvc.perform(put("/api/v1/beneficios/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value("Atualizado"));
    }

    @Test
    void testDeletar() throws Exception {
        // Arrange
        doNothing().when(service).delete(1L);

        // Act & Assert
        mockMvc.perform(delete("/api/v1/beneficios/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void testTransferir() throws Exception {
        // Arrange
        TransferenciaRequest request = new TransferenciaRequest();
        request.setFromId(1L);
        request.setToId(2L);
        request.setValor(new BigDecimal("200.00"));

        doNothing().when(service).transferir(1L, 2L, new BigDecimal("200.00"));

        // Act & Assert
        mockMvc.perform(post("/api/v1/beneficios/transferir")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").exists());
    }
}
