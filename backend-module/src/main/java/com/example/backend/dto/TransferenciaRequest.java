package com.example.backend.dto;

import java.math.BigDecimal;

public class TransferenciaRequest {
    @jakarta.validation.constraints.NotNull(message = "ID do benefício de origem é obrigatório")
    private Long fromId;

    @jakarta.validation.constraints.NotNull(message = "ID do benefício de destino é obrigatório")
    private Long toId;

    @jakarta.validation.constraints.NotNull(message = "Valor é obrigatório")
    @jakarta.validation.constraints.DecimalMin(value = "0.01", message = "Valor deve ser maior que zero")
    private BigDecimal valor;

    public TransferenciaRequest() {}

    public Long getFromId() { return fromId; }
    public void setFromId(Long fromId) { this.fromId = fromId; }
    public Long getToId() { return toId; }
    public void setToId(Long toId) { this.toId = toId; }
    public BigDecimal getValor() { return valor; }
    public void setValor(BigDecimal valor) { this.valor = valor; }
}
