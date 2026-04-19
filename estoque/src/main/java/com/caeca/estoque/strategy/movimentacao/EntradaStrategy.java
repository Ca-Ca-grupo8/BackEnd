package com.caeca.estoque.strategy.movimentacao;

import com.caeca.estoque.database.model.joia.Joia;
import com.caeca.estoque.database.model.movimentacao.Movimentacao;
import com.caeca.estoque.database.model.tipoMovimentacao.TipoMovimentacao;
import com.caeca.estoque.database.model.usuario.Usuario;
import com.caeca.estoque.dto.joia.JoiaSaidaDTO;
import com.caeca.estoque.dto.movimentacao.MovimentacaoMapper;
import com.caeca.estoque.dto.movimentacao.MovimentacaoRequestDto;
import com.caeca.estoque.exception.NotFoundException;
import com.caeca.estoque.service.JoiaService;
import com.caeca.estoque.service.tipoMovimentacao.TipoMovimentacaoService;
import com.caeca.estoque.service.usuario.UsuarioService;
import org.springframework.stereotype.Component;

@Component
public class EntradaStrategy implements MovimentacaoStrategy {
    private final JoiaService joiaService;
    private final FornecedorService fornecedorService;
    private final TipoMovimentacaoService tipoService;
    private final UsuarioService usuarioService;

    public EntradaStrategy(JoiaService joiaService, FornecedorService fornecedorService, TipoMovimentacaoService tipoService, UsuarioService usuarioService) {
        this.joiaService = joiaService;
        this.fornecedorService = fornecedorService;
        this.tipoService = tipoService;
        this.usuarioService = usuarioService;
    }

    @Override
    public boolean suporta(TipoMovimentacao tipoMovimentacao) {
        return tipoMovimentacao.getDescricao().toLowerCase().contains("entrada");
    }

    @Override
    public Movimentacao realizarMovimentacao(MovimentacaoRequestDto dto) {
        Joia joia = joiaService.obterEntityJoiaPorId(dto.getIdJoia());


        Fornecedor fornecedor = fornecedorService.findById(dto.getFkFornecedor())
                .orElseThrow(() -> new NotFoundException("Fornecedor não encontrado."));

        TipoMovimentacao tipo = tipoService.buscarPorId(dto.getIdTipoMovimentacao());

        Usuario usuario = usuarioService.getUsuarioLogado();

        Movimentacao movimentacao = MovimentacaoMapper.toEntity(dto);
        movimentacao.setJoia(joia);
        movimentacao.setTipoMovimentacao(tipo);
        movimentacao.setFornecedor(fornecedor);
        movimentacao.setUsuario(usuario);

        return movimentacao;
    }

    @Override
    public void atualizarEstoque(Movimentacao movimentacao) {
        joiaService.adicionarEstoque(movimentacao.getJoia().getId(), movimentacao.getQuantidade());
    }
}
