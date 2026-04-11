package com.caeca.estoque.strategy.movimentacao;

import com.caeca.estoque.database.model.movimentacao.Movimentacao;
import com.caeca.estoque.dto.movimentacao.MovimentacaoMapper;
import com.caeca.estoque.dto.movimentacao.MovimentacaoRequestDto;
import com.caeca.estoque.exception.NotFoundException;

public class ReentradaStrategy implements MovimentacaoStrategy{
    private final JoiaService joiaService;
    private final TipoMovimentacaoService tipoService;
    private final UsuarioService usuarioService;
    private final MovimentacaoRevendedoraService mrService;

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

        MovimentacaoRevendedora movimentacaoRevendedora = mrService.buscarVendaPorRevendedoraJoia(
                dto.getIdRevendedora(),
                dto.getIdJoia());

        Movimentacao movimentacao = MovimentacaoMapper.toEntity(dto);
        movimentacao.setJoia(joia);
        movimentacao.setTipoMovimentacao(tipo);
        movimentacao.setUsuario(usuario);
        movimentacao.setMovimentacaoRevendedora(movimentacaoRevendedora);

        return movimentacao;
    }

    @Override
    public void atualizarEstoque(Movimentacao movimentacao) {
        joiaService.adicionarEstoque(movimentacao.getJoia().getIdJoia(), movimentacao.getQuantidade());
    }
}
