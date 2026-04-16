package com.caeca.estoque.dto.joia;

import com.caeca.estoque.database.model.banho.Banho;
import com.caeca.estoque.database.model.categoria.Categoria;
import com.caeca.estoque.database.model.material.Material;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public class JoiaEntradaDTO {

    @NotBlank
    private String nome;

    @NotNull
    @Positive
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
    private Integer quantidadeMinima;

    @NotNull
    private Categoria categoria;

    @NotNull
    private Banho banho;

    @NotNull
    private Material material;

    public JoiaEntradaDTO(String nome, Integer quantidade, Double peso, Double precoOriginal, Double multiplicador, Integer quantidadeMinima, Categoria categoria, Banho banho, Material material) {
        this.nome = nome;
        this.quantidade = quantidade;
        this.peso = peso;
        this.precoOriginal = precoOriginal;
        this.multiplicador = multiplicador;
        this.quantidadeMinima = quantidadeMinima;
        this.categoria = categoria;
        this.banho = banho;
        this.material = material;
    }

    public JoiaEntradaDTO() {}

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

    public Integer getQuantidadeMinima() {
        return quantidadeMinima;
    }

    public void setQuantidadeMinima(Integer quantidadeMinima) {
        this.quantidadeMinima = quantidadeMinima;
    }

    public Categoria getCategoria() {
        return categoria;
    }

    public void setCategoria(Categoria categoria) {
        this.categoria = categoria;
    }

    public Banho getBanho() {
        return banho;
    }

    public void setBanho(Banho banho) {
        this.banho = banho;
    }

    public Material getMaterial() {
        return material;
    }

    public void setMaterial(Material material) {
        this.material = material;
    }
}
