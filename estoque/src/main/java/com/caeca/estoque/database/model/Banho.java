package com.caeca.estoque.database.model;

import jakarta.persistence.*;

@Entity
public class Banho {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_banho")
    private Integer idBanho;

    @Column(name = "nome", nullable = false, length = 45)
    private String nome;

    public Banho() {}

    public Banho(Integer idBanho, String nome) {
        this.idBanho = idBanho;
        this.nome = nome;
    }

    public Integer getIdBanho() { return idBanho; }
    public void setIdBanho(Integer idBanho) { this.idBanho = idBanho; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
}
