package com.example.backend.service;

import com.example.backend.dto.BeneficioRequest;
import com.example.backend.dto.BeneficioResponse;
import com.example.backend.model.Beneficio;
import com.example.backend.repository.BeneficioRepository;
import com.example.ejb.BeneficioEjbService;
import com.example.ejb.exception.BeneficioNaoEncontradoException;
import com.example.ejb.exception.OptimisticLockException;
import com.example.ejb.exception.SaldoInsuficienteException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Serviço de negócio para operações com Benefícios.
 * Integra com o EJB para operações de transferência.
 */
@Service
@Transactional
public class BeneficioService {

    private final BeneficioRepository repository;
    private final BeneficioEjbService ejbService;

    @Autowired
    public BeneficioService(BeneficioRepository repository, BeneficioEjbService ejbService) {
        this.repository = repository;
        this.ejbService = ejbService;
    }

    /**
     * Lista todos os benefícios.
     * 
     * @return Lista de benefícios
     */
    public List<BeneficioResponse> findAll() {
        return repository.findAll().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    /**
     * Busca um benefício por ID.
     * 
     * @param id ID do benefício
     * @return Benefício encontrado
     * @throws BeneficioNaoEncontradoException se não for encontrado
     */
    public BeneficioResponse findById(Long id) {
        Beneficio beneficio = repository.findById(id)
                .orElseThrow(() -> new BeneficioNaoEncontradoException("Benefício não encontrado: " + id));
        return toResponse(beneficio);
    }

    /**
     * Cria um novo benefício.
     * 
     * @param request Dados do benefício
     * @return Benefício criado
     */
    public BeneficioResponse create(BeneficioRequest request) {
        Beneficio beneficio = new Beneficio();
        beneficio.setNome(request.getNome());
        beneficio.setDescricao(request.getDescricao());
        beneficio.setValor(request.getValor());
        beneficio.setAtivo(request.getAtivo() != null ? request.getAtivo() : true);
        
        Beneficio saved = repository.save(beneficio);
        return toResponse(saved);
    }

    /**
     * Atualiza um benefício existente.
     * 
     * @param id ID do benefício
     * @param request Dados atualizados
     * @return Benefício atualizado
     * @throws BeneficioNaoEncontradoException se não for encontrado
     */
    public BeneficioResponse update(Long id, BeneficioRequest request) {
        Beneficio beneficio = repository.findById(id)
                .orElseThrow(() -> new BeneficioNaoEncontradoException("Benefício não encontrado: " + id));
        
        beneficio.setNome(request.getNome());
        beneficio.setDescricao(request.getDescricao());
        beneficio.setValor(request.getValor());
        if (request.getAtivo() != null) {
            beneficio.setAtivo(request.getAtivo());
        }
        
        Beneficio saved = repository.save(beneficio);
        return toResponse(saved);
    }

    /**
     * Deleta um benefício.
     * 
     * @param id ID do benefício
     * @throws BeneficioNaoEncontradoException se não for encontrado
     */
    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new BeneficioNaoEncontradoException("Benefício não encontrado: " + id);
        }
        repository.deleteById(id);
    }

    /**
     * Transfere valor entre dois benefícios usando o EJB.
     * 
     * @param fromId ID do benefício de origem
     * @param toId ID do benefício de destino
     * @param valor Valor a ser transferido
     * @throws SaldoInsuficienteException se o saldo for insuficiente
     * @throws OptimisticLockException se houver conflito de concorrência
     * @throws BeneficioNaoEncontradoException se algum benefício não for encontrado
     */
    public void transferir(Long fromId, Long toId, BigDecimal valor) {
        ejbService.transfer(fromId, toId, valor);
    }

    /**
     * Converte entidade para DTO de resposta.
     */
    private BeneficioResponse toResponse(Beneficio beneficio) {
        return new BeneficioResponse(
                beneficio.getId(),
                beneficio.getNome(),
                beneficio.getDescricao(),
                beneficio.getValor(),
                beneficio.getAtivo(),
                beneficio.getVersion()
        );
    }
}
