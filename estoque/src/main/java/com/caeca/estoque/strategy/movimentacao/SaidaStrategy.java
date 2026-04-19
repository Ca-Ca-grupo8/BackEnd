package com.caeca.estoque.strategy.movimentacao;

import com.caeca.estoque.database.model.joia.Joia;
import com.caeca.estoque.database.model.movimentacao.Movimentacao;
import com.caeca.estoque.database.model.tipoMovimentacao.TipoMovimentacao;
import com.caeca.estoque.database.model.usuario.Usuario;
import com.caeca.estoque.dto.movimentacao.MovimentacaoMapper;
import com.caeca.estoque.dto.movimentacao.MovimentacaoRequestDto;
import com.caeca.estoque.dto.movimentacaoRevendedora.MovimentacaoRevendedoraRequestDto;
import com.caeca.estoque.exception.NotFoundException;
import com.caeca.estoque.exception.QtdInsuficienteException;
import com.caeca.estoque.service.JoiaService;
import com.caeca.estoque.service.movimentacaoRevendedora.MovimentacaoRevendedoraService;
import com.caeca.estoque.service.tipoMovimentacao.TipoMovimentacaoService;
import com.caeca.estoque.service.usuario.UsuarioService;
import org.springframework.stereotype.Component;

@Component
public class SaidaStrategy implements MovimentacaoStrategy {
    private final JoiaService joiaService;
    private final TipoMovimentacaoService tipoService;
    private final UsuarioService usuarioService;
    private final MovimentacaoRevendedoraService mrService;

    public SaidaStrategy(JoiaService joiaService, TipoMovimentacaoService tipoService, UsuarioService usuarioService, MovimentacaoRevendedoraService mrService) {
        this.joiaService = joiaService;
        this.tipoService = tipoService;
        this.usuarioService = usuarioService;
        this.mrService = mrService;
    }

    @Override
    public boolean suporta(TipoMovimentacao tipoMovimentacao) {
        return tipoMovimentacao.getDescricao().toLowerCase().contains("saída") ||
                tipoMovimentacao.getDescricao().toLowerCase().contains("venda");
    }

    @Override
    public Movimentacao realizarMovimentacao(MovimentacaoRequestDto dto) {
        Joia joia = joiaService.obterEntityJoiaPorId(dto.getIdJoia());

        if(joia.getQuantidade() < dto.getQuantidade()) {
            throw new QtdInsuficienteException("Quantidade da jóia " + joia.getNome() + " no estoque é insufiente");
        }

        TipoMovimentacao tipo = tipoService.buscarPorId(dto.getIdTipoMovimentacao());

        Usuario usuario = usuarioService.getUsuarioLogado();


        Movimentacao movimentacao = MovimentacaoMapper.toEntity(dto);
        movimentacao.setJoia(joia);
        movimentacao.setTipoMovimentacao(tipo);
        movimentacao.setPrecoSaida(joia.getPreco());
        movimentacao.setUsuario(usuario);

        if(tipo.getDescricao().toLowerCase().contains("venda")){
            movimentacao.setMovimentacaoRevendedora(mrService.buscarOuCriarVendaPorRevendedora(
                    new MovimentacaoRevendedoraRequestDto(dto.getIdRevendedora()));
        }

        return movimentacao;
    }

    @Override
    public void atualizarEstoque(Movimentacao movimentacao) {
        joiaService.diminuirEstoque(movimentacao.getJoia().getId(), movimentacao.getQuantidade());
    }
}
