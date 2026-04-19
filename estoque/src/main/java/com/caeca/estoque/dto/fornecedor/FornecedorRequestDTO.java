package com.caeca.estoque.dto.fornecedor;

import com.caeca.estoque.dto.endereco.EnderecoRequestDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class FornecedorRequestDTO {

    @Schema(example = "Joias Brasil Ltda", description = "Nome do fornecedor")
    @NotBlank
    private String nome;

    @Schema(example = "11999990000", description = "Telefone do fornecedor")
    @NotBlank
    private String telefone;

    @Schema(example = "@joiasbrasil", description = "Instagram do fornecedor")
    @Size(max = 100)
    private String instagram;

    public FornecedorRequestDTO() {
    }

    public FornecedorRequestDTO(String nome, String telefone, String instagram) {
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
