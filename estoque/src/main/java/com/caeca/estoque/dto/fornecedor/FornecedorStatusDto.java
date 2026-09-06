package com.caeca.estoque.dto.fornecedor;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class FornecedorStatusDto {

    @NotNull
    private boolean isAtivo;

    public FornecedorStatusDto() {
    }

    public FornecedorStatusDto(boolean isAtivo) {
        this.isAtivo = isAtivo;
    }

    public boolean isAtivo() {
        return isAtivo;
    }

    public void setAtivo(boolean ativo) {
        isAtivo = ativo;
    }

}
