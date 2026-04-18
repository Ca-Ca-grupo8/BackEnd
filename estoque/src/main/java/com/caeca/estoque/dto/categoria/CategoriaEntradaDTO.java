package com.caeca.estoque.dto.categoria;

import jakarta.validation.constraints.NotBlank;

public class CategoriaEntradaDTO {

    @NotBlank
    public String nome;

    public CategoriaEntradaDTO(String nome) {
        this.nome = nome;
    }

    public CategoriaEntradaDTO() {}

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }
}
