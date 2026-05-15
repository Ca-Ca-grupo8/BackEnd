package com.caeca.estoque.database.model.joia;

import com.caeca.estoque.database.model.banho.Banho;
import com.caeca.estoque.database.model.categoria.Categoria;
import com.caeca.estoque.database.model.material.Material;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
public class Joia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nome;

    @Column(nullable = false)
    private Integer quantidade;

    @Column(nullable = false)
    private Double preco;

    private String imagem;

    @ManyToOne
    private Categoria categoria;

    @ManyToOne
    private Banho banho;

    @ManyToOne
    private Material material;

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime dataAtualizacao;

    @Column(nullable = false)
    private Boolean isAtivo;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime dataCadastro;

    @Column(nullable = false)
    private Integer quantidadeMinima;

    public Joia(Long id, String nome, Integer quantidade, Double preco, String imagem, Categoria categoria, Banho banho, Material material, LocalDateTime dataAtualizacao, Boolean isAtivo, LocalDateTime dataCadastro, Integer quantidadeMinima) {
        this.id = id;
        this.nome = nome;
        this.quantidade = quantidade;
        this.preco = preco;
        this.imagem = imagem;
        this.categoria = categoria;
        this.banho = banho;
        this.material = material;
        this.dataAtualizacao = dataAtualizacao;
        this.isAtivo = isAtivo;
        this.dataCadastro = dataCadastro;
        this.quantidadeMinima = quantidadeMinima;
    }

    public Joia() {}


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

    public Integer getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(Integer quantidade) {
        this.quantidade = quantidade;
    }

    public Double getPreco() {
        return preco;
    }

    public void setPreco(Double preco) {
        this.preco = preco;
    }

    public String getImagem() {
        return imagem;
    }

    public void setImagem(String imagem) {
        this.imagem = imagem;
    }

    public Categoria getCategoria() {
        return categoria;
    }

    public void setCategoria(Categoria categoria) {
        this.categoria = categoria;
    }

    public Banho getBanho() {
        return banho;
    }

    public void setBanho(Banho banho) {
        this.banho = banho;
    }

    public Material getMaterial() {
        return material;
    }

    public void setMaterial(Material material) {
        this.material = material;
    }

    public LocalDateTime getDataAtualizacao() {
        return dataAtualizacao;
    }

    public void setDataAtualizacao(LocalDateTime dataAtualizacao) {
        this.dataAtualizacao = dataAtualizacao;
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

    public Integer getQuantidadeMinima() {
        return quantidadeMinima;
    }

    public void setQuantidadeMinima(Integer quantidadeMinima) {
        this.quantidadeMinima = quantidadeMinima;
    }
}
