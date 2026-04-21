package com.caeca.estoque.dto.joia;

import io.swagger.v3.oas.annotations.media.Schema;

public class JoiaAtualizacaoDTO {

    @Schema(description = "Nome da joia", example = "Anel Coração")
    private String nome;

    @Schema(description = "Multiplicador para gerar o lucro", example = "2")
    private Double multiplicador;

    @Schema(description = "Peso de uma unidade da joia", example = "0.3")
    private Double peso;

    @Schema(description = "Preço original pago na joia", example = "10.50")
    private Double precoOriginal;

    @Schema(description = "Preço pago pelo banho da joia", example = "4.5")
    private Double precoBanho;

    @Schema(description = "Quantidade miníma recomendada para se ter no estoque", example = "2")
    private Integer quantidadeMinima;

    @Schema(description = "Quantidade de joias disponível", example = "2")
    private Integer quantidade;

    @Schema(description = "Id da categoria (Ex: anel)", example = "1")
    private Long categoriaId;

    @Schema(description = "Id do banho (Ex: dourado)", example = "1")
    private Integer banhoId;

    @Schema(description = "Id do material (Ex: aço)", example = "1")
    private Integer materialId;

    public JoiaAtualizacaoDTO(String nome, Double multiplicador, Double peso, Double precoOriginal, Double precoBanho, Integer quantidadeMinima, Integer quantidade, Long categoriaId, Integer banhoId, Integer materialId) {
        this.nome = nome;
        this.multiplicador = multiplicador;
        this.peso = peso;
        this.precoOriginal = precoOriginal;
        this.precoBanho = precoBanho;
        this.quantidadeMinima = quantidadeMinima;
        this.quantidade = quantidade;
        this.categoriaId = categoriaId;
        this.banhoId = banhoId;
        this.materialId = materialId;
    }

    public JoiaAtualizacaoDTO() {}

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public Double getMultiplicador() {
        return multiplicador;
    }

    public void setMultiplicador(Double multiplicador) {
        this.multiplicador = multiplicador;
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

    public Integer getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(Integer quantidade) {
        this.quantidade = quantidade;
    }

    public Long getCategoriaId() {
        return categoriaId;
    }

    public void setCategoriaId(Long categoriaId) {
        this.categoriaId = categoriaId;
    }

    public Integer getBanhoId() {
        return banhoId;
    }

    public void setBanhoId(Integer banhoId) {
        this.banhoId = banhoId;
    }

    public Integer getMaterialId() {
        return materialId;
    }

    public void setMaterialId(Integer materialId) {
        this.materialId = materialId;
    }
}