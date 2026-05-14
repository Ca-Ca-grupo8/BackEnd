package com.caeca.estoque.strategy.movimentacao;

import com.caeca.estoque.database.model.fornecedores.Fornecedor;
import com.caeca.estoque.database.model.joia.Joia;
import com.caeca.estoque.database.model.movimentacao.Movimentacao;
import com.caeca.estoque.database.model.tipoMovimentacao.TipoMovimentacao;
import com.caeca.estoque.database.repository.fornecedor.FornecedorRepository;
import com.caeca.estoque.dto.joia.JoiaMapper;
import com.caeca.estoque.dto.joia.JoiaSaidaDTO;
import com.caeca.estoque.dto.movimentacao.MovimentacaoContextDto;
import com.caeca.estoque.dto.movimentacao.MovimentacaoMapper;
import com.caeca.estoque.exception.NotFoundException;
import com.caeca.estoque.service.joia.JoiaService;
import org.springframework.stereotype.Component;

@Component
public class EntradaStrategy implements MovimentacaoStrategy {
    private final JoiaService joiaService;
    private final FornecedorRepository fornecedorRepository;

    public EntradaStrategy(JoiaService joiaService, FornecedorRepository fornecedorRepository) {
        this.joiaService = joiaService;
        this.fornecedorRepository = fornecedorRepository;
    }

    @Override
    public boolean suporta(TipoMovimentacao tipoMovimentacao) {
        return tipoMovimentacao.getDescricao().toLowerCase().contains("entrada");
    }

    @Override
    public Movimentacao realizarMovimentacao(MovimentacaoContextDto dto) {
        Joia joia;
        if(dto.dadosOriginais().getIdJoia() == null) {
            JoiaSaidaDTO joiaSaidaDTO =  joiaService.criarJoia(dto.dadosOriginais().getDadosNovaJoia());
            joia = joiaService.obterEntityJoiaPorId(joiaSaidaDTO.getId());
        } else {
            joia = joiaService.obterEntityJoiaPorId(dto.dadosOriginais().getIdJoia());
        }

        Fornecedor fornecedor = fornecedorRepository.findById(dto.dadosOriginais().getIdFornecedor())
                .orElseThrow(() -> new NotFoundException("Fornecedor não encontrado."));

        Movimentacao movimentacao = MovimentacaoMapper.toEntity(dto.dadosOriginais());
        movimentacao.setJoia(joia);
        movimentacao.setTipoMovimentacao(dto.tipo());
        movimentacao.setFornecedor(fornecedor);
        movimentacao.setUsuario(dto.usuario());

        return movimentacao;
    }

    @Override
    public void atualizarEstoque(Movimentacao movimentacao) {
        joiaService.adicionarEstoque(movimentacao.getJoia().getId(), movimentacao.getQuantidade());
    }
}
