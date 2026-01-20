package com.example.backend;

import com.example.backend.dto.BeneficioRequest;
import com.example.backend.dto.BeneficioResponse;
import com.example.backend.model.Beneficio;
import com.example.backend.repository.BeneficioRepository;
import com.example.backend.service.BeneficioService;
import com.example.ejb.BeneficioEjbService;
import com.example.ejb.exception.BeneficioNaoEncontradoException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Testes unitários para BeneficioService.
 */
@ExtendWith(MockitoExtension.class)
class BeneficioServiceTest {

    @Mock
    private BeneficioRepository repository;

    @Mock
    private BeneficioEjbService ejbService;

    @InjectMocks
    private BeneficioService service;

    private Beneficio beneficio;

    @BeforeEach
    void setUp() {
        beneficio = new Beneficio();
        beneficio.setId(1L);
        beneficio.setNome("Benefício Teste");
        beneficio.setDescricao("Descrição teste");
        beneficio.setValor(new BigDecimal("1000.00"));
        beneficio.setAtivo(true);
        beneficio.setVersion(0L);
    }

    @Test
    void testFindAll() {
        // Arrange
        List<Beneficio> beneficios = Arrays.asList(beneficio);
        when(repository.findAll()).thenReturn(beneficios);

        // Act
        List<BeneficioResponse> resultado = service.findAll();

        // Assert
        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertEquals("Benefício Teste", resultado.get(0).getNome());
    }

    @Test
    void testFindById_Sucesso() {
        // Arrange
        when(repository.findById(1L)).thenReturn(Optional.of(beneficio));

        // Act
        BeneficioResponse resultado = service.findById(1L);

        // Assert
        assertNotNull(resultado);
        assertEquals(1L, resultado.getId());
        assertEquals("Benefício Teste", resultado.getNome());
    }

    @Test
    void testFindById_NaoEncontrado() {
        // Arrange
        when(repository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(BeneficioNaoEncontradoException.class, () -> service.findById(1L));
    }

    @Test
    void testCreate() {
        // Arrange
        BeneficioRequest request = new BeneficioRequest();
        request.setNome("Novo Benefício");
        request.setDescricao("Nova descrição");
        request.setValor(new BigDecimal("500.00"));
        request.setAtivo(true);

        when(repository.save(any(Beneficio.class))).thenAnswer(invocation -> {
            Beneficio b = invocation.getArgument(0);
            b.setId(2L);
            return b;
        });

        // Act
        BeneficioResponse resultado = service.create(request);

        // Assert
        assertNotNull(resultado);
        assertEquals("Novo Benefício", resultado.getNome());
        verify(repository, times(1)).save(any(Beneficio.class));
    }

    @Test
    void testUpdate() {
        // Arrange
        BeneficioRequest request = new BeneficioRequest();
        request.setNome("Benefício Atualizado");
        request.setDescricao("Descrição atualizada");
        request.setValor(new BigDecimal("1500.00"));

        when(repository.findById(1L)).thenReturn(Optional.of(beneficio));
        when(repository.save(any(Beneficio.class))).thenReturn(beneficio);

        // Act
        BeneficioResponse resultado = service.update(1L, request);

        // Assert
        assertNotNull(resultado);
        verify(repository, times(1)).save(any(Beneficio.class));
    }

    @Test
    void testUpdate_NaoEncontrado() {
        // Arrange
        BeneficioRequest request = new BeneficioRequest();
        when(repository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(BeneficioNaoEncontradoException.class, () -> service.update(1L, request));
    }

    @Test
    void testDelete() {
        // Arrange
        when(repository.existsById(1L)).thenReturn(true);

        // Act
        service.delete(1L);

        // Assert
        verify(repository, times(1)).deleteById(1L);
    }

    @Test
    void testDelete_NaoEncontrado() {
        // Arrange
        when(repository.existsById(1L)).thenReturn(false);

        // Act & Assert
        assertThrows(BeneficioNaoEncontradoException.class, () -> service.delete(1L));
    }

    @Test
    void testTransferir() {
        // Arrange
        BigDecimal valor = new BigDecimal("200.00");

        // Act
        assertDoesNotThrow(() -> service.transferir(1L, 2L, valor));

        // Assert
        verify(ejbService, times(1)).transfer(1L, 2L, valor);
    }
}
