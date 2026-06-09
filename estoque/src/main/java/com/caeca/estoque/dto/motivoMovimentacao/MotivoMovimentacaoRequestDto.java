package com.caeca.estoque.dto.motivoMovimentacao;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class MotivoMovimentacaoRequestDto {

    @NotBlank(message = "A descrição é obrigatória")
    @Size(max = 45, message = "A descrição deve ter no máximo 45 caracteres")
    private String descricao;

    private Long idTipoMovimentacao;

    public MotivoMovimentacaoRequestDto() {}

    public MotivoMovimentacaoRequestDto(String descricao, Long idTipoMovimentacao) {
        this.descricao = descricao;
        this.idTipoMovimentacao = idTipoMovimentacao;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public Long getIdTipoMovimentacao() {
        return idTipoMovimentacao;
    }

    public void setIdTipoMovimentacao(Long idTipoMovimentacao) {
        this.idTipoMovimentacao = idTipoMovimentacao;
    }
}
