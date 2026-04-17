package com.caeca.estoque.database.model.fornecedores;

import com.caeca.estoque.database.model.endereco.Endereco;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "Fornecedor")
public class Fornecedor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_fornecedor")
    private Long id;

    @Column(name = "nome", length = 45)
    private String nome;

    @Column(name = "email", length = 45)
    private String email;

    @Column(name = "telefone", length = 45)
    private String telefone;

    @Column(name = "is_ativo", nullable = false)
    private Boolean isAtivo;

    @Column(name = "data_cadastro", nullable = false)
    private LocalDateTime dataCadastro;

    @Column(name = "data_atualizacao", nullable = false)
    private LocalDateTime dataAtualizacao;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fk_endereco", nullable = false)
    private Endereco endereco;

    public Fornecedor() {}

    public Fornecedor(Long id, String nome, String email, String telefone,
                      Boolean isAtivo, LocalDateTime dataCadastro,
                      LocalDateTime dataAtualizacao, Endereco endereco) {
        this.id = id;
        this.nome = nome;
        this.email = email;
        this.telefone = telefone;
        this.isAtivo = isAtivo;
        this.dataCadastro = dataCadastro;
        this.dataAtualizacao = dataAtualizacao;
        this.endereco = endereco;
    }

    @PrePersist
    public void prePersist() {
        this.isAtivo = true;
        this.dataCadastro = LocalDateTime.now();
        this.dataAtualizacao = LocalDateTime.now();
    }

    @PreUpdate
    public void preUpdate() {
        this.dataAtualizacao = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getTelefone() { return telefone; }
    public void setTelefone(String telefone) { this.telefone = telefone; }

    public Boolean getIsAtivo() { return isAtivo; }
    public void setIsAtivo(Boolean isAtivo) { this.isAtivo = isAtivo; }

    public LocalDateTime getDataCadastro() { return dataCadastro; }
    public void setDataCadastro(LocalDateTime dataCadastro) { this.dataCadastro = dataCadastro; }

    public LocalDateTime getDataAtualizacao() { return dataAtualizacao; }
    public void setDataAtualizacao(LocalDateTime dataAtualizacao) { this.dataAtualizacao = dataAtualizacao; }

    public Endereco getEndereco() { return endereco; }
    public void setEndereco(Endereco endereco) { this.endereco = endereco; }
}