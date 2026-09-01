package com.caeca.estoque.dto.movimentacao;

import com.caeca.estoque.dto.joia.JoiaEntradaDto;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

public class MovimentacaoRequestDto {
    @Schema(example = "2", description = "Quantidade de jóias envolvidas na movimentação")
    @NotNull
    private Integer quantidade;

    @Schema(example = "1", description = "Id da jóia")
    private Long idJoia;

    private JoiaEntradaDto dadosNovaJoia;

    @Schema(example = "1", description = "Id do tipo de movimentação (Entrada, saída, etc)")
    @NotNull
    private Integer idMotivoMovimentacao;

    private Long idFornecedor;

    private Long idRevendedora;

    private Double precoCompraRevendedor;

    private Double precoBanho;

    private Double multiplicador;

    public MovimentacaoRequestDto() {
    }

    public MovimentacaoRequestDto(Integer quantidade, Long idJoia, JoiaEntradaDto dadosNovaJoia, Integer idTipoMovimentacao, Long idFornecedor, Long idRevendedora, Double precoCompraRevendedor, Double precoBanho, Double multiplicador) {
        this.quantidade = quantidade;
        this.idJoia = idJoia;
        this.dadosNovaJoia = dadosNovaJoia;
        this.idMotivoMovimentacao = idTipoMovimentacao;
        this.idFornecedor = idFornecedor;
        this.idRevendedora = idRevendedora;
        this.precoCompraRevendedor = precoCompraRevendedor;
        this.precoBanho = precoBanho;
        this.multiplicador = multiplicador;
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

    public JoiaEntradaDto getDadosNovaJoia() {
        return dadosNovaJoia;
    }

    public void setDadosNovaJoia(JoiaEntradaDto dadosNovaJoia) {
        this.dadosNovaJoia = dadosNovaJoia;
    }

    public Integer getIdMotivoMovimentacao() {
        return idMotivoMovimentacao;
    }

    public void setIdMotivoMovimentacao(Integer idMotivoMovimentacao) {
        this.idMotivoMovimentacao = idMotivoMovimentacao;
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

    public Double getPrecoCompraRevendedor() {
        return precoCompraRevendedor;
    }

    public void setPrecoCompraRevendedor(Double precoCompraRevendedor) {
        this.precoCompraRevendedor = precoCompraRevendedor;
    }

    public Double getPrecoBanho() {
        return precoBanho;
    }

    public void setPrecoBanho(Double precoBanho) {
        this.precoBanho = precoBanho;
    }

    public Double getMultiplicador() {
        return multiplicador;
    }

    public void setMultiplicador(Double multiplicador) {
        this.multiplicador = multiplicador;
    }
}
