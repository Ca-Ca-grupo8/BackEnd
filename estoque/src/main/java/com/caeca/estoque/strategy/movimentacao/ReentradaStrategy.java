package com.caeca.estoque.strategy.movimentacao;

import com.caeca.estoque.database.model.movimentacao.Movimentacao;
import com.caeca.estoque.database.model.tipoMovimentacao.TipoMovimentacao;
import com.caeca.estoque.dto.movimentacao.MovimentacaoMapper;
import com.caeca.estoque.dto.movimentacao.MovimentacaoRequestDto;
import com.caeca.estoque.exception.NotFoundException;
import com.caeca.estoque.service.movimentacaoRevendedora.MovimentacaoRevendedoraService;
import com.caeca.estoque.database.model.movimentacaoRevendedora.MovimentacaoRevendedora;
import com.caeca.estoque.service.tipoMovimentacao.TipoMovimentacaoService;

public class ReentradaStrategy implements MovimentacaoStrategy{
    private final JoiaService joiaService;
    private final TipoMovimentacaoService tipoService;
    private final UsuarioService usuarioService;
    private final MovimentacaoRevendedoraService mrService;

    public ReentradaStrategy(JoiaService joiaService, TipoMovimentacaoService tipoService, UsuarioService usuarioService, MovimentacaoRevendedoraService mrService) {
        this.joiaService = joiaService;
        this.tipoService = tipoService;
        this.usuarioService = usuarioService;
        this.mrService = mrService;
    }

    @Override
    public boolean suporta(TipoMovimentacao tipoMovimentacao) {
        return tipoMovimentacao.getDescricao().toLowerCase().contains("reentrada");
    }

    @Override
    public Movimentacao realizarMovimentacao(MovimentacaoRequestDto dto) {
        Joia joia = joiaService.findById(dto.getFkJoia())
                .orElseThrow(() -> new NotFoundException("Joia não encontrada."));

        TipoMovimentacao tipo = tipoService.buscarPorId(dto.getFkTipoMovimentacao());

        Usuario usuario = usuarioService.X();



        Movimentacao movimentacao = MovimentacaoMapper.toEntity(dto);
        movimentacao.setJoia(joia);
        movimentacao.setTipoMovimentacao(tipo);
        movimentacao.setUsuario(usuario);

        if(dto.getIdRevendedora() != null){
            MovimentacaoRevendedora movimentacaoRevendedora = mrService.buscarVendaAbertaPorRevendedora(
                    dto.getIdRevendedora());
            movimentacao.setMovimentacaoRevendedora(movimentacaoRevendedora);
        }

        return movimentacao;
    }

    @Override
    public void atualizarEstoque(Movimentacao movimentacao) {
        joiaService.adicionarEstoque(movimentacao.getJoia().getIdJoia(), movimentacao.getQuantidade());
    }
}