package com.caeca.estoque.dto.fornecedor;

import com.caeca.estoque.dto.endereco.EnderecoRequestDTO;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class FornecedorRequestDTO {

    @NotBlank
    private String nome;

    @Email
    @NotBlank
    private String email;


    private String telefone;

    @NotNull
    private EnderecoRequestDTO endereco;

    public FornecedorRequestDTO() {
    }

    public FornecedorRequestDTO(String nome, String email, String telefone, EnderecoRequestDTO endereco) {
        this.nome = nome;
        this.email = email;
        this.telefone = telefone;
        this.endereco = endereco;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    public EnderecoRequestDTO getEndereco() {
        return endereco;
    }

    public void setEndereco(EnderecoRequestDTO endereco) {
        this.endereco = endereco;
    }
}
