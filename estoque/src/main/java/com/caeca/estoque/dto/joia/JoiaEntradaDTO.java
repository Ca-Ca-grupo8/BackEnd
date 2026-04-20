package com.caeca.estoque.dto.joia;

import com.caeca.estoque.database.model.banho.Banho;
import com.caeca.estoque.database.model.categoria.Categoria;
import com.caeca.estoque.database.model.material.Material;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;

public class JoiaEntradaDTO {

    @NotBlank
    private String nome;

    @NotNull
    @PositiveOrZero
    private Integer quantidade;

    @NotNull
    @Positive
    private Double peso;

    @NotNull
    @Positive
    private Double precoOriginal;

    @NotNull
    @Positive
    private Double multiplicador;

    @NotNull
    @Positive
    private Double precoBanho;

    @NotNull
    @Positive
    private Integer quantidadeMinima;

    @NotNull
    private Long categoria;

    @NotNull
    private Integer banho;

    @NotNull
    private Integer material;

    public JoiaEntradaDTO(String nome, Integer quantidade, Double peso, Double precoOriginal, Double multiplicador, Double precoBanho, Integer quantidadeMinima, Long categoria, Integer banho, Integer material) {
        this.nome = nome;
        this.quantidade = quantidade;
        this.peso = peso;
        this.precoOriginal = precoOriginal;
        this.multiplicador = multiplicador;
        this.precoBanho = precoBanho;
        this.quantidadeMinima = quantidadeMinima;
        this.categoria = categoria;
        this.banho = banho;
        this.material = material;
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

    public Double getPeso() {
        return peso;
    }

    public void setPeso(Double peso) {
        this.peso = peso;
    }

    public Double getPrecoOriginal() {
        return precoOriginal;
    }

    public void setPrecoOriginal(Double precoOriginal) {
        this.precoOriginal = precoOriginal;
    }

    public Double getMultiplicador() {
        return multiplicador;
    }

    public void setMultiplicador(Double multiplicador) {
        this.multiplicador = multiplicador;
    }

    public Double getPrecoBanho() {
        return precoBanho;
    }

    public void setPrecoBanho(Double precoBanho) {
        this.precoBanho = precoBanho;
    }

    public Integer getQuantidadeMinima() {
        return quantidadeMinima;
    }

    public void setQuantidadeMinima(Integer quantidadeMinima) {
        this.quantidadeMinima = quantidadeMinima;
    }

    public Long getCategoria() {
        return categoria;
    }

    public void setCategoria(Long categoria) {
        this.categoria = categoria;
    }

    public Integer getBanho() {
        return banho;
    }

    public void setBanho(Integer banho) {
        this.banho = banho;
    }

    public Integer getMaterial() {
        return material;
    }

    public void setMaterial(Integer material) {
        this.material = material;
    }
}
