package com.caeca.estoque.dto.material;

public class MaterialResponseDto {
    private Integer idMaterial;
    private String nome;

    public MaterialResponseDto() {
    }

    public MaterialResponseDto(Integer idMaterial, String nome) {
        this.idMaterial = idMaterial;
        this.nome = nome;
    }

    public Integer getIdMaterial() {
        return idMaterial;
    }
    public void setIdMaterial(Integer idMaterial) {
        this.idMaterial = idMaterial;
    }

    public String getNome() {
        return nome;
    }
    public void setNome(String nome) {
        this.nome = nome;
    }
}
