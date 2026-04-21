package com.caeca.estoque.dto.joia;

public class JoiaAtualizacaoDTO {

    private String nome;
    private Double multiplicador;
    private Double peso;
    private Double precoOriginal;
    private Double precoBanho;
    private Integer quantidadeMinima;
    private Integer quantidade;

    private Long categoriaId;
    private Integer banhoId;
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