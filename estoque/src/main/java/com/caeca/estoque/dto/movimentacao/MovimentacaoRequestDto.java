package com.caeca.estoque.dto.movimentacao;

import com.caeca.estoque.dto.joia.JoiaEntradaDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

public class MovimentacaoRequestDto {
    @Schema(example = "2", description = "Quantidade de jóias envolvidas na movimentação")
    @NotNull
    private Integer quantidade;

    @Schema(example = "1", description = "Id da jóia")
    private Long idJoia;

    private JoiaEntradaDTO dadosNovaJoia;

    @Schema(example = "1", description = "Id do tipo de movimentação (Entrada, saída, etc)")
    @NotNull
    private Integer idTipoMovimentacao;

    private Long idFornecedor;

    private Long idRevendedora;

    public MovimentacaoRequestDto() {
    }

    public MovimentacaoRequestDto(Integer quantidade, Long idJoia, JoiaEntradaDTO dadosNovaJoia, Integer idTipoMovimentacao, Long idFornecedor, Long idRevendedora) {
        this.quantidade = quantidade;
        this.idJoia = idJoia;
        this.dadosNovaJoia = dadosNovaJoia;
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

    public JoiaEntradaDTO getDadosNovaJoia() {
        return dadosNovaJoia;
    }

    public void setDadosNovaJoia(JoiaEntradaDTO dadosNovaJoia) {
        this.dadosNovaJoia = dadosNovaJoia;
    }
}
