package com.caeca.estoque.dto.tipoMovimentacao;

import com.caeca.estoque.database.model.tipoMovimentacao.TipoMovimentacaoEnum;

public class TipoMovimentacaoResponseDto {

    private Long id;
    private TipoMovimentacaoEnum nome;

    public TipoMovimentacaoResponseDto() {
    }

    public TipoMovimentacaoResponseDto(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public TipoMovimentacaoEnum getNome() {
        return nome;
    }

    public void setNome(TipoMovimentacaoEnum nome) {
        this.nome = nome;
    }
}
