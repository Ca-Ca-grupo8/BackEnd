package com.caeca.estoque.dto.categoria;

public class CategoriaSaidaDTO {

    private Long id;
    private String nome;

    public CategoriaSaidaDTO(Long id, String nome) {
        this.id = id;
        this.nome = nome;
    }

    public CategoriaSaidaDTO() {}

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
