package com.caeca.estoque.dto.revendedora;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

public class RevendedoraPatchDto {
    @Size(max = 150)
    private String nome;

    @Email
    private String email;

    @Size(min = 11, max = 11)
    private String telefone;
    private Boolean ativo;
    private Long enderecoId;

    public RevendedoraPatchDto() {
    }

    public RevendedoraPatchDto(String nome, String email, String telefone, Boolean ativo, Long enderecoId) {
        this.nome = nome;
        this.email = email;
        this.telefone = telefone;
        this.ativo = ativo;
        this.enderecoId = enderecoId;
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

    public Boolean getAtivo() {
        return ativo;
    }

    public void setAtivo(Boolean ativo) {
        this.ativo = ativo;
    }

    public Long getEnderecoId() {
        return enderecoId;
    }

    public void setEnderecoId(Long enderecoId) {
        this.enderecoId = enderecoId;
    }
}
