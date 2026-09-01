package com.caeca.estoque.dto.categoria;

import jakarta.validation.constraints.NotBlank;

public class CategoriaEntradaDto {

    @NotBlank
    public String nome;

    public CategoriaEntradaDto(String nome) {
        this.nome = nome;
    }

    public CategoriaEntradaDto() {}

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }
}
