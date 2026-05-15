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

    public JoiaEntradaDTO(Integer material, Integer banho, Long categoria, Integer quantidadeMinima, Integer quantidade, String nome) {
        this.material = material;
        this.banho = banho;
        this.categoria = categoria;
        this.quantidadeMinima = quantidadeMinima;
        this.quantidade = quantidade;
        this.nome = nome;
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
