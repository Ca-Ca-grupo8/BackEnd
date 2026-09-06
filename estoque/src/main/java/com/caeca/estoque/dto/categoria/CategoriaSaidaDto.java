package com.caeca.estoque.dto.categoria;

public class CategoriaSaidaDto {

    private Long id;
    private String nome;

    public CategoriaSaidaDto(Long id, String nome) {
        this.id = id;
        this.nome = nome;
    }

    public CategoriaSaidaDto() {}

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
}
