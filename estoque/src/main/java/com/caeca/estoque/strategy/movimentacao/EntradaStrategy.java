package com.caeca.estoque.strategy.movimentacao;

import com.caeca.estoque.database.model.arquivo.Arquivo;
import com.caeca.estoque.database.model.fornecedores.Fornecedor;
import com.caeca.estoque.database.model.joia.Joia;
import com.caeca.estoque.database.model.movimentacao.Movimentacao;
import com.caeca.estoque.database.model.tipoMovimentacao.TipoMovimentacao;
import com.caeca.estoque.database.repository.fornecedor.FornecedorRepository;
import com.caeca.estoque.dto.joia.JoiaAtualizacaoDTO;
import com.caeca.estoque.dto.joia.JoiaMapper;
import com.caeca.estoque.dto.joia.JoiaSaidaDTO;
import com.caeca.estoque.dto.movimentacao.MovimentacaoContextDto;
import com.caeca.estoque.dto.movimentacao.MovimentacaoMapper;
import com.caeca.estoque.dto.movimentacao.MovimentacaoRequestDto;
import com.caeca.estoque.exception.NotFoundException;
import com.caeca.estoque.service.joia.JoiaService;
import com.caeca.estoque.strategy.arquivo.ArquivoStrategy;
import org.springframework.stereotype.Component;

@Component
public class EntradaStrategy implements MovimentacaoStrategy {
    private final JoiaService joiaService;
    private final FornecedorRepository fornecedorRepository;
    private final ArquivoStrategy arquivoStrategy;

    public EntradaStrategy(JoiaService joiaService, FornecedorRepository fornecedorRepository, ArquivoStrategy arquivoStrategy) {
        this.joiaService = joiaService;
        this.fornecedorRepository = fornecedorRepository;
        this.arquivoStrategy = arquivoStrategy;
    }

    @Override
    public boolean suporta(TipoMovimentacao tipoMovimentacao) {
        return tipoMovimentacao.getDescricao().toLowerCase().contains("entrada");
    }

    @Override
    public Movimentacao realizarMovimentacao(MovimentacaoContextDto dto) {
        Joia joia;
        MovimentacaoRequestDto dados = dto.dadosOriginais();

        if(dados.getIdJoia() == null) {
            JoiaSaidaDTO joiaSaidaDTO = joiaService.criarJoia(dados.getDadosNovaJoia());
            joia = joiaService.obterEntityJoiaPorId(joiaSaidaDTO.getId());
        } else {
            joia = joiaService.obterEntityJoiaPorId(dados.getIdJoia());
        }

        Double custoOriginal = dados.getPrecoBanho() + dados.getPrecoCompraRevendedor();
        Double precoJoia = custoOriginal * dados.getMultiplicador();

        JoiaAtualizacaoDTO joiaAtualizacaoDTO = new JoiaAtualizacaoDTO();
        joiaAtualizacaoDTO.setPrecoFinal(precoJoia);
        joiaService.atualizarJoiaPorId(joia.getId(), joiaAtualizacaoDTO);

        Fornecedor fornecedor = fornecedorRepository.findById(dto.dadosOriginais().getIdFornecedor())
                .orElseThrow(() -> new NotFoundException("Fornecedor não encontrado."));

        Movimentacao movimentacao = MovimentacaoMapper.toEntity(dto.dadosOriginais());
        movimentacao.setJoia(joia);
        movimentacao.setTipoMovimentacao(dto.tipo());
        movimentacao.setFornecedor(fornecedor);
        movimentacao.setUsuario(dto.usuario());
        movimentacao.setPrecoOriginal(dto.dadosOriginais().getPrecoCompraRevendedor());
        movimentacao.setPrecoBanho(dto.dadosOriginais().getPrecoBanho());
        movimentacao.setMultiplicador(dto.dadosOriginais().getMultiplicador());

        Arquivo arquivoEntity = arquivoStrategy.adicionar(dto.arquivo());

        arquivoEntity.setJoia(joia);

        return movimentacao;
    }

    @Override
    public void atualizarEstoque(Movimentacao movimentacao) {
        joiaService.adicionarEstoque(movimentacao.getJoia().getId(), movimentacao.getQuantidade());
    }
}
