package com.caeca.estoque.dto.dashboard;

import java.time.LocalDateTime;

public class TopCincoMaisFriasDto {

    private Long id;
    private String nome;
    private Integer quantidade;
    private LocalDateTime ultimaMovimentacao;

    public TopCincoMaisFriasDto(Long id, String nome, Integer quantidade, LocalDateTime ultimaMovimentacao) {
        this.id = id;
        this.nome = nome;
        this.quantidade = quantidade;
        this.ultimaMovimentacao = ultimaMovimentacao;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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
