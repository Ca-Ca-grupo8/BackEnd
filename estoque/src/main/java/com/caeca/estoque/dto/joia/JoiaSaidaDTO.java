package com.caeca.estoque.dto.joia;

import com.caeca.estoque.database.model.arquivo.Arquivo;
import com.caeca.estoque.dto.arquivo.ArquivoResponseDto;

import java.time.LocalDateTime;

public class JoiaSaidaDTO {

    private Long id;

    private String nome;

    private Double preco;

    private Integer quantidade;

    private Integer quantidadeMinima;

    private Boolean isAtivo;

    private LocalDateTime dataCadastro;

    private ArquivoResponseDto arquivo;

    public JoiaSaidaDTO(Long id, String nome, Double preco, Integer quantidade, Integer quantidadeMinima, Boolean isAtivo, LocalDateTime dataCadastro, ArquivoResponseDto arquivo) {
        this.id = id;
        this.nome = nome;
        this.preco = preco;
        this.quantidade = quantidade;
        this.quantidadeMinima = quantidadeMinima;
        this.isAtivo = isAtivo;
        this.dataCadastro = dataCadastro;
        this.arquivo = arquivo;
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

    public Integer getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(Integer quantidade) {
        this.quantidade = quantidade;
    }

    public ArquivoResponseDto getArquivo() {
        return arquivo;
    }

    public void setArquivo(ArquivoResponseDto arquivo) {
        this.arquivo = arquivo;
    }
}
