package com.caeca.estoque.dto.tipoMovimentacao;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class TipoMovimentacaoRequestDto {

    @NotBlank(message = "A descrição é obrigatória")
    @Size(max = 45, message = "A descrição deve ter no máximo 45 caracteres")
    private String descricao;

    public TipoMovimentacaoRequestDto() {}

    public TipoMovimentacaoRequestDto(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }
}
