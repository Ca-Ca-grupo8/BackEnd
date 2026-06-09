package com.caeca.estoque.strategy.movimentacao;

import com.caeca.estoque.database.model.movimentacao.Movimentacao;
import com.caeca.estoque.database.model.motivoMovimentacao.MotivoMovimentacao;
import com.caeca.estoque.dto.movimentacao.MovimentacaoContextDto;

public interface MovimentacaoStrategy {
    public boolean suporta(MotivoMovimentacao motivoMovimentacao);
    public Movimentacao realizarMovimentacao(MovimentacaoContextDto dto);
    public void atualizarEstoque(Movimentacao movimentacao);
}
