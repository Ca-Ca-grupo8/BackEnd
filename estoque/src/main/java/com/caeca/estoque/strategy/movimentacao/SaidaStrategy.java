package com.caeca.estoque.strategy.movimentacao;

import com.caeca.estoque.database.model.movimentacao.Movimentacao;
import com.caeca.estoque.database.model.tipoMovimentacao.TipoMovimentacao;
import com.caeca.estoque.dto.movimentacao.MovimentacaoContextDto;
import com.caeca.estoque.dto.movimentacao.MovimentacaoMapper;
import com.caeca.estoque.dto.movimentacaoRevendedora.MovimentacaoRevendedoraRequestDto;
import com.caeca.estoque.exception.QtdInsuficienteException;
import com.caeca.estoque.service.JoiaService;
import com.caeca.estoque.service.movimentacaoRevendedora.MovimentacaoRevendedoraService;
import com.caeca.estoque.service.tipoMovimentacao.TipoMovimentacaoService;
import com.caeca.estoque.service.usuario.UsuarioService;
import org.springframework.stereotype.Component;

@Component
public class SaidaStrategy implements MovimentacaoStrategy {
    private final JoiaService joiaService;
    private final MovimentacaoRevendedoraService mrService;

    public SaidaStrategy(JoiaService joiaService, MovimentacaoRevendedoraService mrService) {
        this.joiaService = joiaService;
        this.mrService = mrService;
    }

    @Override
    public boolean suporta(TipoMovimentacao tipoMovimentacao) {
        return tipoMovimentacao.getDescricao().toLowerCase().contains("saída") ||
                tipoMovimentacao.getDescricao().toLowerCase().contains("venda");
    }

    @Override
    public Movimentacao realizarMovimentacao(MovimentacaoContextDto dto) {
        if(dto.joia().getQuantidade() < dto.dadosOriginais().getQuantidade()) {
            throw new QtdInsuficienteException("Quantidade da jóia " + dto.joia().getNome() + " no estoque é insufiente");
        }

        Movimentacao movimentacao = MovimentacaoMapper.toEntity(dto.dadosOriginais());
        movimentacao.setJoia(dto.joia());
        movimentacao.setTipoMovimentacao(dto.tipo());
        movimentacao.setPrecoSaida(dto.joia().getPreco());
        movimentacao.setUsuario(dto.usuario());

        if(dto.tipo().getDescricao().toLowerCase().contains("venda") &&
                dto.dadosOriginais().getIdRevendedora() != null){
            movimentacao.setMovimentacaoRevendedora(mrService.buscarOuCriarVendaPorRevendedora(
                    new MovimentacaoRevendedoraRequestDto(dto.dadosOriginais().getIdRevendedora()));
        }

        return movimentacao;
    }

    @Override
    public void atualizarEstoque(Movimentacao movimentacao) {
        joiaService.diminuirEstoque(movimentacao.getJoia().getId(), movimentacao.getQuantidade());
    }
}