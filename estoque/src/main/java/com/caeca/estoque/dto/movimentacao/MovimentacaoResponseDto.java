package com.caeca.estoque.dto.movimentacao;

import com.caeca.estoque.dto.fornecedor.FornecedorResponseDTO;
import com.caeca.estoque.dto.joia.JoiaSaidaDTO;
import com.caeca.estoque.dto.tipoMovimentacao.TipoMovimentacaoResponseDto;
import com.caeca.estoque.dto.usuario.UsuarioListarDto;
import jakarta.validation.constraints.NotNull;
import com.caeca.estoque.dto.movimentacaoRevendedora.MovimentacaoRevendedoraResponseDto

import java.math.BigDecimal;

public class MovimentacaoResponseDto {
    private Long id;
    private Integer quantidade;
    private Double precoSaida;
    private JoiaSaidaDTO joia;
    private TipoMovimentacaoResponseDto tipoMovimentacao;
    private UsuarioListarDto usuario;
    private FornecedorResponseDto fornecedor;
    private movimentacaoRevendedoraResponseDto movimentacaoRevendedora;

    public MovimentacaoResponseDto() {
    }

    public MovimentacaoResponseDto(Long id, Integer quantidade, Double precoSaida, JoiaSaidaDTO joia, TipoMovimentacaoResponseDto tipoMovimentacao, UsuarioListarDto usuario, FornecedorResponseDto fornecedor, movimentacaoRevendedoraResponseDto movimentacaoRevendedora) {
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

    public Double getPrecoSaida() {
        return precoSaida;
    }

    public void setPrecoSaida(Double precoSaida) {
        this.precoSaida = precoSaida;
    }

    public JoiaSaidaDTO getJoia() {
        return joia;
    }

    public void setJoia(JoiaSaidaDTO joia) {
        this.joia = joia;
    }

    public TipoMovimentacaoResponseDto getTipoMovimentacao() {
        return tipoMovimentacao;
    }

    public void setTipoMovimentacao(TipoMovimentacaoResponseDto tipoMovimentacao) {
        this.tipoMovimentacao = tipoMovimentacao;
    }

    public UsuarioListarDto getUsuario() {
        return usuario;
    }

    public void setUsuario(UsuarioListarDto usuario) {
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
