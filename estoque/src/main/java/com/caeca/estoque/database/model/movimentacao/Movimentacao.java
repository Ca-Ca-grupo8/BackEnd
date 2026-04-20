package com.caeca.estoque.database.model.movimentacao;

import com.caeca.estoque.database.model.fornecedores.Fornecedor;
import com.caeca.estoque.database.model.joia.Joia;
import com.caeca.estoque.database.model.movimentacaoRevendedora.MovimentacaoRevendedora;
import com.caeca.estoque.database.model.tipoMovimentacao.TipoMovimentacao;
import com.caeca.estoque.database.model.usuario.Usuario;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.ManyToAny;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
public class Movimentacao {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idMovimentacao;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime data;

    private Integer quantidade;

    private Double precoSaida;

    @ManyToOne()
    private Joia joia;

    @ManyToOne()
    private TipoMovimentacao tipoMovimentacao;

    @ManyToOne()
    private Usuario usuario;

    @ManyToOne()
    private Fornecedor fornecedor;

    @ManyToOne()
    private MovimentacaoRevendedora movimentacaoRevendedora;

    public Movimentacao() {
    }

    public Movimentacao(Long idMovimentacao, LocalDateTime data, Integer quantidade, Double precoSaida, Joia joia, TipoMovimentacao tipoMovimentacao, Usuario usuario, Fornecedor fornecedor, MovimentacaoRevendedora movimentacaoRevendedora) {
        this.idMovimentacao = idMovimentacao;
        this.data = data;
        this.quantidade = quantidade;
        this.precoSaida = precoSaida;
        this.joia = joia;
        this.tipoMovimentacao = tipoMovimentacao;
        this.usuario = usuario;
        this.fornecedor = fornecedor;
        this.movimentacaoRevendedora = movimentacaoRevendedora;
    }

    public Long getIdMovimentacao() {
        return idMovimentacao;
    }

    public void setIdMovimentacao(Long idMovimentacao) {
        this.idMovimentacao = idMovimentacao;
    }

    public LocalDateTime getData() {
        return data;
    }

    public void setData(LocalDateTime data) {
        this.data = data;
    }

    public Integer getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(Integer quantidade) {
        this.quantidade = quantidade;
    }

    public Double getPrecoSaida() {
        return precoSaida;
    }

    public void setPrecoSaida(Double precoSaida) {
        this.precoSaida = precoSaida;
    }

    public Joia getJoia() {
        return joia;
    }

    public void setJoia(Joia joia) {
        this.joia = joia;
    }

    public TipoMovimentacao getTipoMovimentacao() {
        return tipoMovimentacao;
    }

    public void setTipoMovimentacao(TipoMovimentacao tipoMovimentacao) {
        this.tipoMovimentacao = tipoMovimentacao;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public Fornecedor getFornecedor() {
        return fornecedor;
    }

    public void setFornecedor(Fornecedor fornecedor) {
        this.fornecedor = fornecedor;
    }

    public MovimentacaoRevendedora getMovimentacaoRevendedora() {
        return movimentacaoRevendedora;
    }

    public void setMovimentacaoRevendedora(MovimentacaoRevendedora movimentacaoRevendedora) {
        this.movimentacaoRevendedora = movimentacaoRevendedora;
    }
}
