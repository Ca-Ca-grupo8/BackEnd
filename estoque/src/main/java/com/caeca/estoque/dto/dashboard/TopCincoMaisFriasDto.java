package com.caeca.estoque.dto.dashboard;

import java.time.LocalDateTime;

public class TopCincoMaisFriasDto {

    private String nome;
    private Integer quantidade;
    private LocalDateTime ultimaMovimentacao;

    public TopCincoMaisFriasDto(String nome, Integer quantidade, LocalDateTime ultimaMovimentacao) {
        this.nome = nome;
        this.quantidade = quantidade;
        this.ultimaMovimentacao = ultimaMovimentacao;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public Integer getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(Integer quantidade) {
        this.quantidade = quantidade;
    }

    public LocalDateTime getUltimaMovimentacao() {
        return ultimaMovimentacao;
    }

    public void setUltimaMovimentacao(LocalDateTime ultimaMovimentacao) {
        this.ultimaMovimentacao = ultimaMovimentacao;
    }
}
