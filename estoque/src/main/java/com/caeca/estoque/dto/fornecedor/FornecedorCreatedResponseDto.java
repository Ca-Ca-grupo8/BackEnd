package com.caeca.estoque.dto.fornecedor;

import java.time.LocalDateTime;

public class FornecedorCreatedResponseDto {

    private Long idFornecedor;
    private String nome;
    private Boolean isAtivo;
    private LocalDateTime dataCadastro;

    public FornecedorCreatedResponseDto(Long idFornecedor, String nome, Boolean isAtivo, LocalDateTime dataCadastro) {
        this.idFornecedor = idFornecedor;
        this.nome = nome;
        this.isAtivo = isAtivo;
        this.dataCadastro = dataCadastro;
    }

    public FornecedorCreatedResponseDto() {
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
}
