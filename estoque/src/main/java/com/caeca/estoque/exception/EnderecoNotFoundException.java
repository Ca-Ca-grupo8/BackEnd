package com.caeca.estoque.exception;

public class EnderecoNotFoundException extends RuntimeException {
    public EnderecoNotFoundException(Long id) {
        super("Endereço com id " + String.valueOf(id) + " não encontrado.");
    }

    public EnderecoNotFoundException(String message) {
        super(message);
    }
}
