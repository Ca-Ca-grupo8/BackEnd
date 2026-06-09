package com.caeca.estoque.database.model.tipoMovimentacao;

import jakarta.persistence.*;
import lombok.Getter;

@Entity
@Getter
public class TipoMovimentacao {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Enumerated(EnumType.STRING)
    private TipoMovimentacaoEnum tipoMovimentacaoEnum;

    public TipoMovimentacao(Long id, TipoMovimentacaoEnum tipoMovimentacaoEnum) {
        this.id = id;
        this.tipoMovimentacaoEnum = tipoMovimentacaoEnum;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setTipoMovimentacaoEnum(TipoMovimentacaoEnum tipoMovimentacaoEnum) {
        this.tipoMovimentacaoEnum = tipoMovimentacaoEnum;
    }
}
