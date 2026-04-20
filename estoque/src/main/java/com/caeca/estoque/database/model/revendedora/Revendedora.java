package com.caeca.estoque.database.model.revendedora;

import com.caeca.estoque.database.model.endereco.Endereco;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import com.caeca.estoque.database.model.movimentacaoRevendedora.MovimentacaoRevendedora;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;
import java.util.List;

@Entity
@EntityListeners(AuditingEntityListener.class)
public class Revendedora {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    private String nome;

    @NotBlank
    @Email
    private String email;

    @NotBlank
    @Size(max = 11, min = 11)
    private String telefone;

    @Column(name = "is_ativo")
    private boolean ativo;

    @NotNull
    @OneToOne
    @JoinColumn(name = "fk_endereco")
    private Endereco endereco;

    @CreatedDate
    @Column(updatable = false)
    private LocalDate dataCadastro;

    @LastModifiedDate
    private LocalDate dataAtualizacao;

    @JsonManagedReference
    @OneToMany(mappedBy = "revendedora")
    private List<MovimentacaoRevendedora> vendas;

    public Revendedora() {
    }

    public Revendedora(Long id, String nome, String email, String telefone, boolean ativo, Endereco endereco, LocalDate dataCadastro, LocalDate dataAtualizacao, List<MovimentacaoRevendedora> vendas) {
        this.id = id;
        this.nome = nome;
        this.email = email;
        this.telefone = telefone;
        this.ativo = ativo;
        this.endereco = endereco;
        this.dataCadastro = dataCadastro;
        this.dataAtualizacao = dataAtualizacao;
        this.vendas = vendas;
    }

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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    public boolean isAtivo() {
        return ativo;
    }

    public void setAtivo(boolean ativo) {
        this.ativo = ativo;
    }

    public Endereco getEndereco() {
        return endereco;
    }

    public void setEndereco(Endereco endereco) {
        this.endereco = endereco;
    }

    public LocalDate getDataCadastro() {
        return dataCadastro;
    }

    public void setDataCadastro(LocalDate dataCadastro) {
        this.dataCadastro = dataCadastro;
    }

    public LocalDate getDataAtualizacao() {
        return dataAtualizacao;
    }

    public void setDataAtualizacao(LocalDate dataAtualizacao) {
        this.dataAtualizacao = dataAtualizacao;
    }

    public List<MovimentacaoRevendedora> getVendas() {
        return vendas;
    }

    public void setVendas(List<MovimentacaoRevendedora> vendas) {
        this.vendas = vendas;
    }
}
