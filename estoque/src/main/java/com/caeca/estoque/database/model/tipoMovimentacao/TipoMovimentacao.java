package com.caeca.estoque.database.model.tipoMovimentacao;

import jakarta.persistence.*;
import lombok.Getter;

@Entity
@Getter
public class TipoMovimentacao {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Enumerated(EnumType.STRING)
    private TipoMovimentacaoEnum nome;

    public TipoMovimentacao(Long id, TipoMovimentacaoEnum nome) {
        this.id = id;
        this.nome = nome;
    }

    public TipoMovimentacao() {
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setNome(TipoMovimentacaoEnum nome) {
        this.nome = nome;
    }
}
