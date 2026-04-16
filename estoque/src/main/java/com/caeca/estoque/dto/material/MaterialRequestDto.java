package com.caeca.estoque.dto.material;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class MaterialRequestDto {
    @NotBlank
    @Size(min = 1, max = 45)
    private String nome;

    public MaterialRequestDto() {
    }

    public MaterialRequestDto(String nome) {
        this.nome = nome;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }
}
