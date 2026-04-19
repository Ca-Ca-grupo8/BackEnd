package com.caeca.estoque.dto.movimentacao;

import jakarta.validation.constraints.NotNull;

public class MovimentacaoRequestDto {
    @NotNull
    private Integer quantidade;

    @NotNull
    private Long idJoia;

    @NotNull
    private Integer idTipoMovimentacao;

    private Long idFornecedor;

    private Long idRevendedora;

    public MovimentacaoRequestDto() {
    }

    public MovimentacaoRequestDto(Integer quantidade, Long idJoia, Integer idTipoMovimentacao, Long idFornecedor, Long idRevendedora) {
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

    public Long getIdJoia() {
        return idJoia;
    }

    public void setIdJoia(Long idJoia) {
        this.idJoia = idJoia;
    }

    public Integer getIdTipoMovimentacao() {
        return idTipoMovimentacao;
    }

    public void setIdTipoMovimentacao(Integer idTipoMovimentacao) {
        this.idTipoMovimentacao = idTipoMovimentacao;
    }

    public Long getIdFornecedor() {
        return idFornecedor;
    }

    public void setIdFornecedor(Long idFornecedor) {
        this.idFornecedor = idFornecedor;
    }

    public Long getIdRevendedora() {
        return idRevendedora;
    }

    public void setIdRevendedora(Long idRevendedora) {
        this.idRevendedora = idRevendedora;
    }
}
