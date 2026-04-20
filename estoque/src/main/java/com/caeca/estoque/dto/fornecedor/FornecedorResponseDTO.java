package com.caeca.estoque.dto.fornecedor;

import com.caeca.estoque.dto.endereco.EnderecoResponseDTO;

import java.time.LocalDateTime;

public class FornecedorResponseDTO {
    private Long idFornecedor;
    private String nome;
    private String instagram;
    private String telefone;
    private Boolean isAtivo;
    private LocalDateTime dataCadastro;

    public FornecedorResponseDTO() {
    }

    public FornecedorResponseDTO(Long idFornecedor, String nome, String instagram, String telefone, Boolean isAtivo, LocalDateTime dataCadastro) {
        this.idFornecedor = idFornecedor;
        this.nome = nome;
        this.instagram = instagram;
        this.telefone = telefone;
        this.isAtivo = isAtivo;
        this.dataCadastro = dataCadastro;
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

    public String getInstagram() {
        return instagram;
    }

    public void setInstagram(String instagram) {
        this.instagram = instagram;
    }
}
