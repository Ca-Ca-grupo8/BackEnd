package com.caeca.estoque.exception;

public class FornecedorNotFoundException extends NotFoundException {
    public FornecedorNotFoundException(Long id) {
        super("Fornecedor com id " + String.valueOf(id) + " não encontrado.");
    }
}
