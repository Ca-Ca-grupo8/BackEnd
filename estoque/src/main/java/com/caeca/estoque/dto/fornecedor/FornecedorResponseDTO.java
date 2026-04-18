package com.caeca.estoque.dto.fornecedor;

import com.caeca.estoque.dto.endereco.EnderecoResponseDTO;

import java.time.LocalDateTime;

public class FornecedorResponseDTO {
    private Long idFornecedor;
    private String nome;
    private String email;
    private String telefone;
    private Boolean isAtivo;
    private LocalDateTime dataCadastro;
    private EnderecoResponseDTO endereco;

    public FornecedorResponseDTO(Long idFornecedor, String nome, String email, String telefone, Boolean isAtivo, LocalDateTime dataCadastro, EnderecoResponseDTO endereco) {
        this.idFornecedor = idFornecedor;
        this.nome = nome;
        this.email = email;
        this.telefone = telefone;
        this.isAtivo = isAtivo;
        this.dataCadastro = dataCadastro;
        this.endereco = endereco;
    }

    public FornecedorResponseDTO() {
    }

    public Long getIdFornecedor() {
        return idFornecedor;
    }

    public void setIdFornecedor(Long idFornecedor) {
        this.idFornecedor = idFornecedor;
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
        return isAtivo;
    }

    public void setAtivo(Boolean ativo) {
        isAtivo = ativo;
    }

    public LocalDateTime getDataCadastro() {
        return dataCadastro;
    }

    public void setDataCadastro(LocalDateTime dataCadastro) {
        this.dataCadastro = dataCadastro;
    }

    public EnderecoResponseDTO getEndereco() {
        return endereco;
    }

    public void setEndereco(EnderecoResponseDTO endereco) {
        this.endereco = endereco;
    }
}
