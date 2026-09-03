package com.caeca.estoque.dto.categoria;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class CategoriaEntradaDto {

    @NotBlank
    @Size(max = 100)
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
