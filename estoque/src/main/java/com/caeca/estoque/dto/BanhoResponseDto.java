package com.caeca.estoque.dto;

public class BanhoResponseDto {

    private Integer idBanho;
    private String nome;

    public BanhoResponseDto() {}

    public BanhoResponseDto(Integer idBanho, String nome) {
        this.idBanho = idBanho;
        this.nome = nome;
    }

    public Integer getIdBanho() { return idBanho; }
    public void setIdBanho(Integer idBanho) { this.idBanho = idBanho; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
}
