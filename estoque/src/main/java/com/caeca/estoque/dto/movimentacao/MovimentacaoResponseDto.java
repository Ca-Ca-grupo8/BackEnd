package com.caeca.estoque.dto.movimentacao;

import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public class MovimentacaoResponseDto {
    private Long id;
    private Integer quantidade;
    private BigDecimal precoSaida;
    private JoiaResponseDto joia;
    private TipoMovimentacaoResponseDto tipoMovimentacao;
    private UsuarioResponseDto usuario;
    private FornecedorResponseDto fornecedor;
    private movimentacaoRevendedoraResponseDto movimentacaoRevendedora;

    public MovimentacaoResponseDto() {
    }

    public MovimentacaoResponseDto(Long id, Integer quantidade, BigDecimal precoSaida, JoiaResponseDto joia, TipoMovimentacaoResponseDto tipoMovimentacao, UsuarioResponseDto usuario, FornecedorResponseDto fornecedor, movimentacaoRevendedoraResponseDto movimentacaoRevendedora) {
        this.id = id;
        this.quantidade = quantidade;
        this.precoSaida = precoSaida;
        this.joia = joia;
        this.tipoMovimentacao = tipoMovimentacao;
        this.usuario = usuario;
        this.fornecedor = fornecedor;
        this.movimentacaoRevendedora = movimentacaoRevendedora;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(Integer quantidade) {
        this.quantidade = quantidade;
    }

    public BigDecimal getPrecoSaida() {
        return precoSaida;
    }

    public void setPrecoSaida(BigDecimal precoSaida) {
        this.precoSaida = precoSaida;
    }

    public JoiaResponseDto getJoia() {
        return joia;
    }

    public void setJoia(JoiaResponseDto joia) {
        this.joia = joia;
    }

    public TipoMovimentacaoResponseDto getTipoMovimentacao() {
        return tipoMovimentacao;
    }

    public void setTipoMovimentacao(TipoMovimentacaoResponseDto tipoMovimentacao) {
        this.tipoMovimentacao = tipoMovimentacao;
    }

    public UsuarioResponseDto getUsuario() {
        return usuario;
    }

    public void setUsuario(UsuarioResponseDto usuario) {
        this.usuario = usuario;
    }

    public FornecedorResponseDto getFornecedor() {
        return fornecedor;
    }

    public void setFornecedor(FornecedorResponseDto fornecedor) {
        this.fornecedor = fornecedor;
    }

    public movimentacaoRevendedoraResponseDto getMovimentacaoRevendedora() {
        return movimentacaoRevendedora;
    }

    public void setMovimentacaoRevendedora(movimentacaoRevendedoraResponseDto movimentacaoRevendedora) {
        this.movimentacaoRevendedora = movimentacaoRevendedora;
    }
}
