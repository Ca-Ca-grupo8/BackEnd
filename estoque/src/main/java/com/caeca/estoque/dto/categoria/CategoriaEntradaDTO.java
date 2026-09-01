package com.caeca.estoque.dto.categoria;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class CategoriaEntradaDTO {

    @NotBlank
    @Size(max = 100)
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
