package com.caeca.estoque.exception;

public class FornecedorNotFoundException extends RuntimeException {
    public FornecedorNotFoundException(Long id) {
        super("Fornecedor com id " + String.valueOf(id) + " não encontrado");
    }
}
