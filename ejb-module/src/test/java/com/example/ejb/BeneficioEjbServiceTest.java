package com.example.ejb;

import com.example.ejb.exception.BeneficioNaoEncontradoException;
import com.example.ejb.exception.SaldoInsuficienteException;
import com.example.ejb.model.Beneficio;
import jakarta.persistence.EntityManager;
import jakarta.persistence.LockModeType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Testes unitários para BeneficioEjbService.
 */
@ExtendWith(MockitoExtension.class)
class BeneficioEjbServiceTest {

    @Mock
    private EntityManager em;

    @InjectMocks
    private BeneficioEjbService service;

    private Beneficio beneficioOrigem;
    private Beneficio beneficioDestino;

    @BeforeEach
    void setUp() {
        beneficioOrigem = new Beneficio();
        beneficioOrigem.setId(1L);
        beneficioOrigem.setNome("Benefício A");
        beneficioOrigem.setValor(new BigDecimal("1000.00"));
        beneficioOrigem.setAtivo(true);
        beneficioOrigem.setVersion(0L);

        beneficioDestino = new Beneficio();
        beneficioDestino.setId(2L);
        beneficioDestino.setNome("Benefício B");
        beneficioDestino.setValor(new BigDecimal("500.00"));
        beneficioDestino.setAtivo(true);
        beneficioDestino.setVersion(0L);
    }

    @Test
    void testTransfer_Sucesso() {
        // Arrange
        BigDecimal valorTransferencia = new BigDecimal("200.00");
        when(em.find(Beneficio.class, 1L, LockModeType.PESSIMISTIC_WRITE)).thenReturn(beneficioOrigem);
        when(em.find(Beneficio.class, 2L, LockModeType.PESSIMISTIC_WRITE)).thenReturn(beneficioDestino);

        // Act
        assertDoesNotThrow(() -> service.transfer(1L, 2L, valorTransferencia));

        // Assert
        assertEquals(new BigDecimal("800.00"), beneficioOrigem.getValor());
        assertEquals(new BigDecimal("700.00"), beneficioDestino.getValor());
        verify(em, times(1)).merge(beneficioOrigem);
        verify(em, times(1)).merge(beneficioDestino);
    }

    @Test
    void testTransfer_BeneficioOrigemNaoEncontrado() {
        // Arrange
        when(em.find(Beneficio.class, 1L, LockModeType.PESSIMISTIC_WRITE)).thenReturn(null);

        // Act & Assert
        assertThrows(BeneficioNaoEncontradoException.class, 
            () -> service.transfer(1L, 2L, new BigDecimal("100.00")));
    }

    @Test
    void testTransfer_BeneficioDestinoNaoEncontrado() {
        // Arrange
        when(em.find(Beneficio.class, 1L, LockModeType.PESSIMISTIC_WRITE)).thenReturn(beneficioOrigem);
        when(em.find(Beneficio.class, 2L, LockModeType.PESSIMISTIC_WRITE)).thenReturn(null);

        // Act & Assert
        assertThrows(BeneficioNaoEncontradoException.class, 
            () -> service.transfer(1L, 2L, new BigDecimal("100.00")));
    }

    @Test
    void testTransfer_SaldoInsuficiente() {
        // Arrange
        BigDecimal valorTransferencia = new BigDecimal("1500.00"); // Maior que o saldo
        when(em.find(Beneficio.class, 1L, LockModeType.PESSIMISTIC_WRITE)).thenReturn(beneficioOrigem);
        when(em.find(Beneficio.class, 2L, LockModeType.PESSIMISTIC_WRITE)).thenReturn(beneficioDestino);

        // Act & Assert
        assertThrows(SaldoInsuficienteException.class, 
            () -> service.transfer(1L, 2L, valorTransferencia));
    }

    @Test
    void testTransfer_ValorZero() {
        // Act & Assert
        assertThrows(IllegalArgumentException.class, 
            () -> service.transfer(1L, 2L, BigDecimal.ZERO));
    }

    @Test
    void testTransfer_ValorNegativo() {
        // Act & Assert
        assertThrows(IllegalArgumentException.class, 
            () -> service.transfer(1L, 2L, new BigDecimal("-100.00")));
    }

    @Test
    void testTransfer_MesmoBeneficio() {
        // Act & Assert
        assertThrows(IllegalArgumentException.class, 
            () -> service.transfer(1L, 1L, new BigDecimal("100.00")));
    }

    @Test
    void testTransfer_BeneficioOrigemInativo() {
        // Arrange
        beneficioOrigem.setAtivo(false);
        when(em.find(Beneficio.class, 1L, LockModeType.PESSIMISTIC_WRITE)).thenReturn(beneficioOrigem);
        when(em.find(Beneficio.class, 2L, LockModeType.PESSIMISTIC_WRITE)).thenReturn(beneficioDestino);

        // Act & Assert
        assertThrows(IllegalArgumentException.class, 
            () -> service.transfer(1L, 2L, new BigDecimal("100.00")));
    }

    @Test
    void testTransfer_BeneficioDestinoInativo() {
        // Arrange
        beneficioDestino.setAtivo(false);
        when(em.find(Beneficio.class, 1L, LockModeType.PESSIMISTIC_WRITE)).thenReturn(beneficioOrigem);
        when(em.find(Beneficio.class, 2L, LockModeType.PESSIMISTIC_WRITE)).thenReturn(beneficioDestino);

        // Act & Assert
        assertThrows(IllegalArgumentException.class, 
            () -> service.transfer(1L, 2L, new BigDecimal("100.00")));
    }

    @Test
    void testFindById_Sucesso() {
        // Arrange
        when(em.find(Beneficio.class, 1L)).thenReturn(beneficioOrigem);

        // Act
        Beneficio resultado = service.findById(1L);

        // Assert
        assertNotNull(resultado);
        assertEquals(1L, resultado.getId());
        verify(em, times(1)).find(Beneficio.class, 1L);
    }

    @Test
    void testFindById_NaoEncontrado() {
        // Arrange
        when(em.find(Beneficio.class, 1L)).thenReturn(null);

        // Act & Assert
        assertThrows(BeneficioNaoEncontradoException.class, () -> service.findById(1L));
    }

    @Test
    void testFindById_IdNulo() {
        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> service.findById(null));
    }
}
