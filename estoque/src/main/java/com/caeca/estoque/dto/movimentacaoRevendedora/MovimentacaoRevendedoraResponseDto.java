package com.caeca.estoque.dto.movimentacaoRevendedora;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class MovimentacaoRevendedoraResponseDto {
    private Long idMovimentacaoRevendedora;
    private boolean isFinalizada;
    private RevendedoraMovimentacaoDto revendedora;
    private List<MovimentacaoItemDto> movimentacoes;
    private BigDecimal totalVenda;
    private LocalDateTime data;

    public static class RevendedoraMovimentacaoDto{
        private Long idRevendedora;
        private String nome;

        public RevendedoraMovimentacaoDto() {
        }

        public RevendedoraMovimentacaoDto(Long idRevendedora, String nome) {
            this.idRevendedora = idRevendedora;
            this.nome = nome;
        }

        public Long getIdRevendedora() {
            return idRevendedora;
        }

        public void setIdRevendedora(Long idRevendedora) {
            this.idRevendedora = idRevendedora;
        }

        public String getNome() {
            return nome;
        }

        public void setNome(String nome) {
            this.nome = nome;
        }
    };

    public static class MovimentacaoItemDto {
        private Long idMovimentacao;
        private JoiaResumoDto joia;
        private Integer quantidade;
        private BigDecimal precoSaida;

        public MovimentacaoItemDto() {
        }

        public MovimentacaoItemDto(Long idMovimentacao, JoiaResumoDto joia, Integer decimal, BigDecimal precoSaida) {
            this.idMovimentacao = idMovimentacao;
            this.joia = joia;
            this.quantidade = decimal;
            this.precoSaida = precoSaida;
        }

        public Long getIdMovimentacao() {
            return idMovimentacao;
        }

        public void setIdMovimentacao(Long idMovimentacao) {
            this.idMovimentacao = idMovimentacao;
        }

        public JoiaResumoDto getJoia() {
            return joia;
        }

        public void setJoia(JoiaResumoDto joia) {
            this.joia = joia;
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
    }

    public MovimentacaoRevendedoraResponseDto() {
    }

    public MovimentacaoRevendedoraResponseDto(Long idMovimentacaoRevendedora, boolean isFinalizada, RevendedoraMovimentacaoDto revendedora, List<MovimentacaoItemDto> movimentacoes, BigDecimal totalVenda, LocalDateTime data) {
        this.idMovimentacaoRevendedora = idMovimentacaoRevendedora;
        this.isFinalizada = isFinalizada;
        this.revendedora = revendedora;
        this.movimentacoes = movimentacoes;
        this.totalVenda = totalVenda;
        this.data = data;
    }

    public Long getIdMovimentacaoRevendedora() {
        return idMovimentacaoRevendedora;
    }

    public void setIdMovimentacaoRevendedora(Long idMovimentacaoRevendedora) {
        this.idMovimentacaoRevendedora = idMovimentacaoRevendedora;
    }

    public RevendedoraMovimentacaoDto getRevendedora() {
        return revendedora;
    }

    public void setRevendedora(RevendedoraMovimentacaoDto revendedora) {
        this.revendedora = revendedora;
    }

    public List<MovimentacaoItemDto> getMovimentacoes() {
        return movimentacoes;
    }

    public void setMovimentacoes(List<MovimentacaoItemDto> movimentacoes) {
        this.movimentacoes = movimentacoes;
    }

    public BigDecimal getTotalVenda() {
        return totalVenda;
    }

    public void setTotalVenda(BigDecimal totalVenda) {
        this.totalVenda = totalVenda;
    }

    public LocalDateTime getData() {
        return data;
    }

    public void setData(LocalDateTime data) {
        this.data = data;
    }

    public boolean getIsFinalizada() {
        return isFinalizada;
    }

    public void setFinalizada(boolean finalizada) {
        isFinalizada = finalizada;
    }
}
