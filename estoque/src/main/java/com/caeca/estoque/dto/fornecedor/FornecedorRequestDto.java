package com.caeca.estoque.dto.fornecedor;

import com.caeca.estoque.dto.endereco.EnderecoRequestDto;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class FornecedorRequestDto {

    @Schema(example = "Joias Brasil Ltda", description = "Nome do fornecedor")
    @NotBlank
    @Size(max = 150)
    private String nome;

    @Schema(example = "11999990000", description = "Telefone do fornecedor")
    @NotBlank
    @Size(max = 20)
    private String telefone;

    @Schema(example = "@joiasbrasil", description = "Instagram do fornecedor")
    @Size(max = 100)
    private String instagram;

    public FornecedorRequestDto() {
    }

    public FornecedorRequestDto(String nome, String telefone, String instagram) {
        this.nome = nome;
        this.telefone = telefone;
        this.instagram = instagram;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    public String getInstagram() {
        return instagram;
    }

    public void setInstagram(String instagram) {
        this.instagram = instagram;
    }
}
