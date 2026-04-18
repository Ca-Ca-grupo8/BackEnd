package com.caeca.estoque.dto.fornecedor;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class FornecedorStatusDTO {

    @NotNull
    private boolean isAtivo;

    public FornecedorStatusDTO() {
    }

    public FornecedorStatusDTO(boolean isAtivo) {
        this.isAtivo = isAtivo;
    }

    public boolean isAtivo() {
        return isAtivo;
    }

    public void setAtivo(boolean ativo) {
        isAtivo = ativo;
    }

}
