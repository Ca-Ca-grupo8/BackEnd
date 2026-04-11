package com.caeca.estoque.database.model;

import jakarta.persistence.*;

@Entity
@Table(name = "Tipo_Movimentacao")
public class TipoMovimentacao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_tipo_movimentacao")
    private Integer idTipoMovimentacao;

    @Column(name = "descricao", nullable = false, length = 45)
    private String descricao;

    public TipoMovimentacao() {}

    public TipoMovimentacao(Integer idTipoMovimentacao, String descricao) {
        this.idTipoMovimentacao = idTipoMovimentacao;
        this.descricao = descricao;
    }

    public Integer getIdTipoMovimentacao() { return idTipoMovimentacao; }
    public void setIdTipoMovimentacao(Integer idTipoMovimentacao) { this.idTipoMovimentacao = idTipoMovimentacao; }

    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }
}
