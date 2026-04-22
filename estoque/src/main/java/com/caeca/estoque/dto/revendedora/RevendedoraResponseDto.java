package com.caeca.estoque.dto.revendedora;

import com.caeca.estoque.database.model.endereco.Endereco;
import com.caeca.estoque.dto.endereco.EnderecoResponseDTO;

import java.time.LocalDate;

public class RevendedoraResponseDto {
    private Long id;
    private String nome;
    private String email;
    private String telefone;
    private boolean ativo;
    private EnderecoResponseDTO endereco;
    private LocalDate dataCadastro;
    private LocalDate dataAtualizacao;

    public RevendedoraResponseDto() {
    }

    public RevendedoraResponseDto(Long id, String nome, String email, String telefone, boolean ativo, EnderecoResponseDTO endereco, LocalDate dataCadastro, LocalDate dataAtualizacao) {
        this.id = id;
        this.nome = nome;
        this.email = email;
        this.telefone = telefone;
        this.ativo = ativo;
        this.endereco = endereco;
        this.dataCadastro = dataCadastro;
        this.dataAtualizacao = dataAtualizacao;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public boolean isAtivo() {
        return ativo;
    }

    public void setAtivo(boolean ativo) {
        this.ativo = ativo;
    }

    public EnderecoResponseDTO getEndereco() {
        return endereco;
    }

    public void setEndereco(EnderecoResponseDTO endereco) {
        this.endereco = endereco;
    }

    public LocalDate getDataCadastro() {
        return dataCadastro;
    }

    public void setDataCadastro(LocalDate dataCadastro) {
        this.dataCadastro = dataCadastro;
    }

    public LocalDate getDataAtualizacao() {
        return dataAtualizacao;
    }

    public void setDataAtualizacao(LocalDate dataAtualizacao) {
        this.dataAtualizacao = dataAtualizacao;
    }
}
