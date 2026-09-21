package com.caeca.estoque.database.model.movimentacaoRevendedora;

import com.caeca.estoque.database.model.movimentacao.Movimentacao;
import com.caeca.estoque.database.model.revendedora.Revendedora;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.List;

@Entity
public class MovimentacaoRevendedora {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idMovimentacaoRevendedora;

    private String nome;

    private boolean isFinalizada = false;

    @JsonBackReference
    @ManyToOne
    private Revendedora revendedora;

    @OneToMany(mappedBy = "movimentacaoRevendedora", fetch = FetchType.LAZY)
    private List<Movimentacao> movimentacoes;

    private LocalDateTime dataLimite;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime data;

    public MovimentacaoRevendedora() {
    }

    public MovimentacaoRevendedora(Long idMovimentacaoRevendedora, String nome, boolean isFinalizada, Revendedora revendedora, List<Movimentacao> movimentacoes, LocalDateTime data) {
        this.idMovimentacaoRevendedora = idMovimentacaoRevendedora;
        this.nome = nome;
        this.isFinalizada = isFinalizada;
        this.revendedora = revendedora;
        this.movimentacoes = movimentacoes;
        this.data = data;
    }

    public Long getIdMovimentacaoRevendedora() {
        return idMovimentacaoRevendedora;
    }

    public void setIdMovimentacaoRevendedora(Long idMovimentacaoRevendedora) {
        this.idMovimentacaoRevendedora = idMovimentacaoRevendedora;
    }

    public boolean getIsFinalizada() {
        return isFinalizada;
    }

    public void setFinalizada(boolean finalizada) {
        isFinalizada = finalizada;
    }

    public Revendedora getRevendedora() {
        return revendedora;
    }

    public void setRevendedora(Revendedora revendedora) {
        this.revendedora = revendedora;
    }

    public List<Movimentacao> getMovimentacoes() {
        return movimentacoes;
    }

    public void setMovimentacoes(List<Movimentacao> movimentacoes) {
        this.movimentacoes = movimentacoes;
    }

    public LocalDateTime getData() {
        return data;
    }

    public void setData(LocalDateTime data) {
        this.data = data;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public LocalDateTime getDataLimite() {
        return dataLimite;
    }

    public void setDataLimite(LocalDateTime dataLimite) {
        this.dataLimite = dataLimite;
    }

    public boolean isFinalizada() {
        return isFinalizada;
    }
}
