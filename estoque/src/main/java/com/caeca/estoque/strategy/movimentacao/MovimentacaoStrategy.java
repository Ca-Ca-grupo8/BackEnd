package com.caeca.estoque.strategy.movimentacao;

import com.caeca.estoque.database.model.movimentacao.Movimentacao;
import com.caeca.estoque.database.model.tipoMovimentacao.TipoMovimentacao;
import com.caeca.estoque.dto.movimentacao.MovimentacaoRequestDto;

public interface MovimentacaoStrategy {
    public boolean suporta(TipoMovimentacao tipoMovimentacao);
    public Movimentacao realizarMovimentacao(MovimentacaoRequestDto dto);
    public void atualizarEstoque(Movimentacao movimentacao);
}
