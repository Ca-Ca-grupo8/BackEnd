package com.caeca.estoque.strategy.movimentacao;

import com.caeca.estoque.database.model.movimentacao.Movimentacao;
import com.caeca.estoque.database.model.tipoMovimentacao.TipoMovimentacao;
import com.caeca.estoque.dto.movimentacao.MovimentacaoContextDto;
import com.caeca.estoque.dto.movimentacao.MovimentacaoMapper;
import com.caeca.estoque.service.joia.JoiaService;
import com.caeca.estoque.service.movimentacaoRevendedora.MovimentacaoRevendedoraService;
import com.caeca.estoque.database.model.movimentacaoRevendedora.MovimentacaoRevendedora;

public class ReentradaStrategy implements MovimentacaoStrategy{
    private final JoiaService joiaService;
    private final MovimentacaoRevendedoraService mrService;

    public ReentradaStrategy(JoiaService joiaService, MovimentacaoRevendedoraService mrService) {
        this.joiaService = joiaService;
        this.mrService = mrService;
    }

    @Override
    public boolean suporta(TipoMovimentacao tipoMovimentacao) {
        return tipoMovimentacao.getDescricao().toLowerCase().contains("reentrada");
    }

    @Override
    public Movimentacao realizarMovimentacao(MovimentacaoContextDto dto) {
        Movimentacao movimentacao = MovimentacaoMapper.toEntity(dto.dadosOriginais());
        movimentacao.setJoia(joiaService.obterEntityJoiaPorId(dto.dadosOriginais().getIdJoia()));
        movimentacao.setTipoMovimentacao(dto.tipo());
        movimentacao.setUsuario(dto.usuario());

        if(dto.dadosOriginais().getIdRevendedora() != null){
            MovimentacaoRevendedora movimentacaoRevendedora = mrService.buscarVendaAbertaPorRevendedora(
                    dto.dadosOriginais().getIdRevendedora());
            movimentacao.setMovimentacaoRevendedora(movimentacaoRevendedora);
        }

        return movimentacao;
    }

    @Override
    public void atualizarEstoque(Movimentacao movimentacao) {
        joiaService.adicionarEstoque(movimentacao.getJoia().getId(), movimentacao.getQuantidade());
    }
}