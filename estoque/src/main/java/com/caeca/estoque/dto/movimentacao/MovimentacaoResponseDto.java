package com.caeca.estoque.dto.movimentacao;

import com.caeca.estoque.dto.fornecedor.FornecedorResponseDTO;
import com.caeca.estoque.dto.joia.JoiaSaidaDTO;
import com.caeca.estoque.dto.motivoMovimentacao.MotivoMovimentacaoResponseDto;
import com.caeca.estoque.dto.usuario.UsuarioListarDto;
import com.caeca.estoque.dto.movimentacaoRevendedora.MovimentacaoRevendedoraResponseDto;

public class MovimentacaoResponseDto {
    private Long id;
    private Integer quantidade;
    private Double precoSaida;
    private JoiaSaidaDTO joia;
    private MotivoMovimentacaoResponseDto motivoMovimentacao;
    private UsuarioListarDto usuario;
    private FornecedorResponseDTO fornecedor;
    private MovimentacaoRevendedoraResponseDto movimentacaoRevendedora;

    public MovimentacaoResponseDto() {
    }

    public MovimentacaoResponseDto(Long id, Integer quantidade, Double precoSaida, JoiaSaidaDTO joia, MotivoMovimentacaoResponseDto motivoMovimentacao, UsuarioListarDto usuario, FornecedorResponseDTO fornecedor, MovimentacaoRevendedoraResponseDto movimentacaoRevendedora) {
        this.id = id;
        this.quantidade = quantidade;
        this.precoSaida = precoSaida;
        this.joia = joia;
        this.motivoMovimentacao = motivoMovimentacao;
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

    public MotivoMovimentacaoResponseDto getMotivoMovimentacao() {
        return motivoMovimentacao;
    }

    public void setMotivoMovimentacao(MotivoMovimentacaoResponseDto motivoMovimentacao) {
        this.motivoMovimentacao = motivoMovimentacao;
    }

    public UsuarioListarDto getUsuario() {
        return usuario;
    }

    public void setUsuario(UsuarioListarDto usuario) {
        this.usuario = usuario;
    }

    public FornecedorResponseDTO getFornecedor() {
        return fornecedor;
    }

    public void setFornecedor(FornecedorResponseDTO fornecedor) {
        this.fornecedor = fornecedor;
    }

    public MovimentacaoRevendedoraResponseDto getMovimentacaoRevendedora() {
        return movimentacaoRevendedora;
    }

    public void setMovimentacaoRevendedora(MovimentacaoRevendedoraResponseDto movimentacaoRevendedora) {
        this.movimentacaoRevendedora = movimentacaoRevendedora;
    }
}
