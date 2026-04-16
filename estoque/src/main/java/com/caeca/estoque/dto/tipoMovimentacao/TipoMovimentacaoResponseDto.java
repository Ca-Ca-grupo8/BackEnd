package com.caeca.estoque.dto.tipoMovimentacao;

public class TipoMovimentacaoResponseDto {

    private Integer idTipoMovimentacao;
    private String descricao;

    public TipoMovimentacaoResponseDto() {}

    public TipoMovimentacaoResponseDto(Integer idTipoMovimentacao, String descricao) {
        this.idTipoMovimentacao = idTipoMovimentacao;
        this.descricao = descricao;
    }

    public Integer getIdTipoMovimentacao() { return idTipoMovimentacao; }
    public void setIdTipoMovimentacao(Integer idTipoMovimentacao) { this.idTipoMovimentacao = idTipoMovimentacao; }

    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }
}