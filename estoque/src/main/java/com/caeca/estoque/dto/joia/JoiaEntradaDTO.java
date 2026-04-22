package com.caeca.estoque.dto.joia;

import com.caeca.estoque.database.model.banho.Banho;
import com.caeca.estoque.database.model.categoria.Categoria;
import com.caeca.estoque.database.model.material.Material;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;

public class JoiaEntradaDTO {

    @NotBlank
    @Schema(description = "Nome da joia", example = "Anel Coração")
    private String nome;

    @NotNull
    @PositiveOrZero
    @Schema(description = "Quantidade de joias disponível", example = "2")
    private Integer quantidade;

    @NotNull
    @Positive
    @Schema(description = "Peso de uma unidade da joia", example = "0.3")
    private Double peso;

    @NotNull
    @Positive
    @Schema(description = "Preço original pago na joia", example = "10.50")
    private Double precoOriginal;

    @NotNull
    @Positive
    @Schema(description = "Multiplicador para gerar o lucro", example = "2")
    private Double multiplicador;

    @NotNull
    @Positive
    @Schema(description = "Preço pago pelo banho da joia", example = "4.5")
    private Double precoBanho;

    @NotNull
    @Positive
    @Schema(description = "Quantidade miníma recomendada para se ter no estoque", example = "2")
    private Integer quantidadeMinima;

    @NotNull
    @Schema(description = "Id da categoria (Ex: anel)", example = "1")
    private Long categoria;

    @NotNull
    @Schema(description = "Id do banho (Ex: dourado)", example = "1")
    private Integer banho;

    @NotNull
    @Schema(description = "Id do material (Ex: aço)", example = "1")
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
