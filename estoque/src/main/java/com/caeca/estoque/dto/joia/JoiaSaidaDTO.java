package com.caeca.estoque.dto.joia;

import java.time.LocalDateTime;

public class JoiaSaidaDTO {

    private Long id;

    private String nome;

    private Double preco;

    private Integer quantidadeMinima;

    private Boolean isAtivo;

    private LocalDateTime dataCadastro;

    public JoiaSaidaDTO(Long id, String nome, Integer quantidadeMinima, Double preco, Boolean isAtivo, LocalDateTime dataCadastro) {
        this.id = id;
        this.nome = nome;
        this.quantidadeMinima = quantidadeMinima;
        this.preco = preco;
        this.isAtivo = isAtivo;
        this.dataCadastro = dataCadastro;
    }

    public JoiaSaidaDTO() {}

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

    public Double getPreco() {
        return preco;
    }

    public void setPreco(Double preco) {
        this.preco = preco;
    }

    public Boolean getAtivo() {
        return isAtivo;
    }

    public void setAtivo(Boolean ativo) {
        isAtivo = ativo;
    }

    public Integer getQuantidadeMinima() {
        return quantidadeMinima;
    }

    public void setQuantidadeMinima(Integer quantidadeMinima) {
        this.quantidadeMinima = quantidadeMinima;
    }

    public LocalDateTime getDataCadastro() {
        return dataCadastro;
    }

    public void setDataCadastro(LocalDateTime dataCadastro) {
        this.dataCadastro = dataCadastro;
    }
}
