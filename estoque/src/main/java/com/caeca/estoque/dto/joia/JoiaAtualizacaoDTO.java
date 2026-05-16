package com.caeca.estoque.dto.joia;

import io.swagger.v3.oas.annotations.media.Schema;

public class JoiaAtualizacaoDTO {

    @Schema(description = "Nome da joia", example = "Anel Coração")
    private String nome;

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

    private Double precoFinal;

    private Boolean isAtivo;

    public JoiaAtualizacaoDTO(String nome, Integer quantidadeMinima, Integer quantidade, Long categoriaId, Integer banhoId, Integer materialId, Double precoFinal, Boolean isAtivo) {
        this.nome = nome;
        this.quantidadeMinima = quantidadeMinima;
        this.quantidade = quantidade;
        this.categoriaId = categoriaId;
        this.banhoId = banhoId;
        this.materialId = materialId;
        this.precoFinal = precoFinal;
        this.isAtivo = isAtivo;
    }

    public JoiaAtualizacaoDTO() {}

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
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

    public Double getPrecoFinal() {
        return precoFinal;
    }

    public void setPrecoFinal(Double precoFinal) {
        this.precoFinal = precoFinal;
    }

    public Boolean getIsAtivo() {
        return isAtivo;
    }

    public void setAtivo(Boolean ativo) {
        isAtivo = ativo;
    }
}