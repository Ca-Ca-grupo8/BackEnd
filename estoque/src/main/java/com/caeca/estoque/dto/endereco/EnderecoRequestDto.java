package com.caeca.estoque.dto.endereco;

import jakarta.validation.constraints.*;

public class EnderecoRequestDto {

    @NotBlank(message = "CEP obrigatório")
    @Pattern(regexp = "\\d{8}", message = "CEP deve ter 8 números")
    private String cep;

    @NotBlank(message = "Logradouro obrigatório")
    @Size(max = 250)
    private String logradouro;

    @NotNull(message = "Número obrigatório")
    private Integer numero;

    @Size(max = 45)
    private String complemento;

    @NotBlank(message = "Bairro obrigatório")
    @Size(max = 250)
    private String bairro;

    @NotBlank(message = "Cidade obrigatória")
    @Size(max = 45)
    private String cidade;

    @NotBlank(message = "UF obrigatória")
    @Size(min = 2, max = 2)
    private String uf;

    public EnderecoRequestDto() {
    }

    public EnderecoRequestDto(String cep, String logradouro, Integer numero, String complemento, String bairro, String cidade, String uf) {
        this.cep = cep;
        this.logradouro = logradouro;
        this.numero = numero;
        this.complemento = complemento;
        this.bairro = bairro;
        this.cidade = cidade;
        this.uf = uf;
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