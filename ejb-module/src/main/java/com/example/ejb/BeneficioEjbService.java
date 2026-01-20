package com.example.ejb;

import com.example.ejb.exception.BeneficioNaoEncontradoException;
import com.example.ejb.exception.OptimisticLockException;
import com.example.ejb.exception.SaldoInsuficienteException;
import com.example.ejb.model.Beneficio;
import jakarta.ejb.Stateless;
import jakarta.ejb.TransactionAttribute;
import jakarta.ejb.TransactionAttributeType;
import jakarta.persistence.EntityManager;
import jakarta.persistence.LockModeType;
import jakarta.persistence.PersistenceContext;
import java.math.BigDecimal;

/**
 * Serviço EJB para operações com Benefícios.
 * Implementa transferências com validações, locking e controle transacional.
 */
@Stateless
@TransactionAttribute(TransactionAttributeType.REQUIRED)
public class BeneficioEjbService {

    @PersistenceContext
    private EntityManager em;

    /**
     * Transfere valor entre dois benefícios.
     * 
     * Implementa:
     * - Validação de saldo suficiente
     * - Pessimistic locking para evitar lost updates
     * - Tratamento de exceções com rollback automático
     * - Validações de entrada
     * 
     * @param fromId ID do benefício de origem
     * @param toId ID do benefício de destino
     * @param amount Valor a ser transferido
     * @throws BeneficioNaoEncontradoException se algum benefício não for encontrado
     * @throws SaldoInsuficienteException se o saldo for insuficiente
     * @throws IllegalArgumentException se os parâmetros forem inválidos
     */
    public void transfer(Long fromId, Long toId, BigDecimal amount) {
        // Validações de entrada
        if (fromId == null || toId == null) {
            throw new IllegalArgumentException("IDs de origem e destino não podem ser nulos");
        }
        
        if (fromId.equals(toId)) {
            throw new IllegalArgumentException("Benefício de origem e destino não podem ser o mesmo");
        }
        
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Valor da transferência deve ser maior que zero");
        }

        // Buscar benefícios com PESSIMISTIC WRITE LOCK para evitar lost updates
        // Isso garante que nenhuma outra transação possa modificar os registros simultaneamente
        Beneficio from = em.find(Beneficio.class, fromId, LockModeType.PESSIMISTIC_WRITE);
        if (from == null) {
            throw new BeneficioNaoEncontradoException("Benefício de origem não encontrado: " + fromId);
        }

        Beneficio to = em.find(Beneficio.class, toId, LockModeType.PESSIMISTIC_WRITE);
        if (to == null) {
            throw new BeneficioNaoEncontradoException("Benefício de destino não encontrado: " + toId);
        }

        // Validar se benefícios estão ativos
        if (!Boolean.TRUE.equals(from.getAtivo())) {
            throw new IllegalArgumentException("Benefício de origem está inativo: " + fromId);
        }
        
        if (!Boolean.TRUE.equals(to.getAtivo())) {
            throw new IllegalArgumentException("Benefício de destino está inativo: " + toId);
        }

        // Validar saldo suficiente
        BigDecimal saldoAtual = from.getValor();
        if (saldoAtual == null || saldoAtual.compareTo(amount) < 0) {
            throw new SaldoInsuficienteException(
                String.format("Saldo insuficiente. Saldo atual: %s, Valor solicitado: %s", 
                    saldoAtual, amount)
            );
        }

        try {
            // Realizar transferência
            BigDecimal novoSaldoOrigem = saldoAtual.subtract(amount);
            BigDecimal novoSaldoDestino = to.getValor().add(amount);

            // Validar que o novo saldo não ficou negativo (double-check)
            if (novoSaldoOrigem.compareTo(BigDecimal.ZERO) < 0) {
                throw new SaldoInsuficienteException("Operação resultaria em saldo negativo");
            }

            from.setValor(novoSaldoOrigem);
            to.setValor(novoSaldoDestino);

            // Merge com optimistic locking (campo VERSION será verificado)
            em.merge(from);
            em.merge(to);
            
            // Se houver conflito de versão, o JPA lançará OptimisticLockException
            // que será capturada e relançada como nossa exceção customizada
            
        } catch (jakarta.persistence.OptimisticLockException e) {
            // Rollback automático pela anotação @TransactionAttribute(REQUIRED)
            throw new com.example.ejb.exception.OptimisticLockException(
                "Conflito de concorrência detectado. A operação foi cancelada. Tente novamente.", 
                e
            );
        }
    }

    /**
     * Busca um benefício por ID.
     * 
     * @param id ID do benefício
     * @return Benefício encontrado
     * @throws BeneficioNaoEncontradoException se não for encontrado
     */
    public Beneficio findById(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("ID não pode ser nulo");
        }
        
        Beneficio beneficio = em.find(Beneficio.class, id);
        if (beneficio == null) {
            throw new BeneficioNaoEncontradoException("Benefício não encontrado: " + id);
        }
        
        return beneficio;
    }
}
