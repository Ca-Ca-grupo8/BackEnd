package com.caeca.estoque.database.model;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class Endereco {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_endereco")
    private Long id;

    @NotBlank
    @Size(min = 8, max = 8)
    @Column(name = "cep", nullable = false, length = 8)
    private String cep;

    @NotBlank
    @Size(max = 250)
    @Column(name = "logradouro", nullable = false, length = 250)
    private String logradouro;

    @NotNull
    @Column(name = "numero", nullable = false)
    private Integer numero;

    @Size(max = 45)
    @Column(name = "complemento", length = 45)
    private String complemento;

    @NotBlank
    @Size(max = 250)
    @Column(name = "bairro", nullable = false, length = 250)
    private String bairro;

    @NotBlank
    @Size(max = 45)
    @Column(name = "cidade", nullable = false, length = 45)
    private String cidade;

    @NotBlank
    @Size(min = 2, max = 2)
    @Column(name = "uf", nullable = false, length = 2)
    private String uf;

    public Endereco() {
    }

    public Endereco(Long id, String cep, String logradouro, Integer numero, String complemento, String bairro, String cidade, String uf) {
        this.id = id;
        this.cep = cep;
        this.logradouro = logradouro;
        this.numero = numero;
        this.complemento = complemento;
        this.bairro = bairro;
        this.cidade = cidade;
        this.uf = uf;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCep() {
        return cep;
    }

    public void setCep(String cep) {
        this.cep = cep;
    }

    public String getLogradouro() {
        return logradouro;
    }

    public void setLogradouro(String logradouro) {
        this.logradouro = logradouro;
    }

    public Integer getNumero() {
        return numero;
    }

    public void setNumero(Integer numero) {
        this.numero = numero;
    }

    public String getComplemento() {
        return complemento;
    }

    public void setComplemento(String complemento) {
        this.complemento = complemento;
    }

    public String getBairro() {
        return bairro;
    }

    public void setBairro(String bairro) {
        this.bairro = bairro;
    }

    public String getCidade() {
        return cidade;
    }

    public void setCidade(String cidade) {
        this.cidade = cidade;
    }

    public String getUf() {
        return uf;
    }

    public void setUf(String uf) {
        this.uf = uf;
    }
}
