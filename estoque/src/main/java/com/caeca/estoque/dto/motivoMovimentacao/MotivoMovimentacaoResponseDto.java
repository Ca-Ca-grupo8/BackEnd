package com.caeca.estoque.dto.motivoMovimentacao;

import com.caeca.estoque.database.model.tipoMovimentacao.TipoMovimentacao;

public class MotivoMovimentacaoResponseDto {

    private Integer idTipoMovimentacao;
    private String descricao;
    private TipoMovimentacao tipoMovimentacao;

    public MotivoMovimentacaoResponseDto() {}

    public MotivoMovimentacaoResponseDto(Integer idTipoMovimentacao, String descricao, TipoMovimentacao tipoMovimentacao) {
        this.idTipoMovimentacao = idTipoMovimentacao;
        this.descricao = descricao;
        this.tipoMovimentacao = tipoMovimentacao;
    }

    public Integer getIdTipoMovimentacao() {
        return idTipoMovimentacao;
    }

    public void setIdTipoMovimentacao(Integer idTipoMovimentacao) {
        this.idTipoMovimentacao = idTipoMovimentacao;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public TipoMovimentacao getTipoMovimentacao() {
        return tipoMovimentacao;
    }

    public void setTipoMovimentacao(TipoMovimentacao tipoMovimentacao) {
        this.tipoMovimentacao = tipoMovimentacao;
    }
}