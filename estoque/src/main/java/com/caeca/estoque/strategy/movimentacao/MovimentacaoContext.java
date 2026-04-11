package com.caeca.estoque.strategy.movimentacao;

import com.caeca.estoque.database.model.movimentacao.Movimentacao;
import com.caeca.estoque.dto.movimentacao.MovimentacaoRequestDto;

public class MovimentacaoContext {
    private MovimentacaoStrategy strategy;

    public MovimentacaoContext(MovimentacaoStrategy strategy) {
        this.strategy = strategy;
    }

    public Movimentacao executar(MovimentacaoRequestDto dto){
        return strategy.realizarMovimentacao(dto);
    }

    public void atualizarEstoque(Movimentacao movimentacao){
        strategy.atualizarEstoque(movimentacao);
    }
}
