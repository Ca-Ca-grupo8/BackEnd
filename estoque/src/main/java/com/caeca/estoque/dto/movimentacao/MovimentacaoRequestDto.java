package com.caeca.estoque.dto.movimentacao;

import jakarta.validation.constraints.NotNull;

public class MovimentacaoRequestDto {
    @NotNull
    private Integer quantidade;

    @NotNull
    private Integer idJoia;

    @NotNull
    private Integer idTipoMovimentacao;

    private Integer idFornecedor;

    private Integer idRevendedora;

    public MovimentacaoRequestDto() {
    }

    public MovimentacaoRequestDto(Integer quantidade, Integer idJoia, Integer idTipoMovimentacao, Integer idFornecedor, Integer idRevendedora) {
        this.quantidade = quantidade;
        this.idJoia = idJoia;
        this.idTipoMovimentacao = idTipoMovimentacao;
        this.idFornecedor = idFornecedor;
        this.idRevendedora = idRevendedora;
    }

    public Integer getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(Integer quantidade) {
        this.quantidade = quantidade;
    }

    public Integer getIdJoia() {
        return idJoia;
    }

    public void setIdJoia(Integer idJoia) {
        this.idJoia = idJoia;
    }

    public Integer getIdTipoMovimentacao() {
        return idTipoMovimentacao;
    }

    public void setIdTipoMovimentacao(Integer idTipoMovimentacao) {
        this.idTipoMovimentacao = idTipoMovimentacao;
    }

    public Integer getIdFornecedor() {
        return idFornecedor;
    }

    public void setIdFornecedor(Integer idFornecedor) {
        this.idFornecedor = idFornecedor;
    }

    public Integer getIdRevendedora() {
        return idRevendedora;
    }

    public void setIdRevendedora(Integer idRevendedora) {
        this.idRevendedora = idRevendedora;
    }
}
