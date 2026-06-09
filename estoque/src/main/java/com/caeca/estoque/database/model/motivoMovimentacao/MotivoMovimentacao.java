package com.caeca.estoque.database.model.motivoMovimentacao;

import com.caeca.estoque.database.model.tipoMovimentacao.TipoMovimentacao;
import jakarta.persistence.*;

@Entity
@Table(name = "Motivo_Movimentacao")
public class MotivoMovimentacao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_motivo_movimentacao")
    private Integer idMotivoMovimentacao;

    @Column(name = "descricao", nullable = false, length = 45)
    private String descricao;

    @ManyToOne
    @JoinColumn(name = "tipo_movimentacao_id")
    private TipoMovimentacao tipoMovimentacao;

    public MotivoMovimentacao() {}

    public MotivoMovimentacao(Integer idMotivoMovimentacao, String descricao, TipoMovimentacao tipoMovimentacao) {
        this.idMotivoMovimentacao = idMotivoMovimentacao;
        this.descricao = descricao;
        this.tipoMovimentacao = tipoMovimentacao;
    }

    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }

    public Integer getIdMotivoMovimentacao() {
        return idMotivoMovimentacao;
    }

    public void setIdMotivoMovimentacao(Integer idMotivoMovimentacao) {
        this.idMotivoMovimentacao = idMotivoMovimentacao;
    }

    public TipoMovimentacao getTipoMovimentacao() {
        return tipoMovimentacao;
    }

    public void setTipoMovimentacao(TipoMovimentacao tipoMovimentacao) {
        this.tipoMovimentacao = tipoMovimentacao;
    }
}
