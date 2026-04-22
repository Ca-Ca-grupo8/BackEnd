package com.caeca.estoque.dto.banho;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class BanhoRequestDto {

    @NotBlank(message = "O nome do banho é obrigatório")
    @Size(max = 45, message = "O nome deve ter no máximo 45 caracteres")
    private String nome;

    public BanhoRequestDto() {}

    public BanhoRequestDto(String nome) {
        this.nome = nome;
    }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
}
