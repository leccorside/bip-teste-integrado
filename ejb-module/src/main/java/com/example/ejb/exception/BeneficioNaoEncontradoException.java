package com.example.ejb.exception;

public class BeneficioNaoEncontradoException extends RuntimeException {
    public BeneficioNaoEncontradoException(String message) {
        super(message);
    }
    
    public BeneficioNaoEncontradoException(String message, Throwable cause) {
        super(message, cause);
    }
}