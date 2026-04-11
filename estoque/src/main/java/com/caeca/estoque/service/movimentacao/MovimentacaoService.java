package com.caeca.estoque.service.movimentacao;

import com.caeca.estoque.database.model.movimentacao.Movimentacao;
import com.caeca.estoque.database.repository.movimentacao.MovimentacaoRepository;
import com.caeca.estoque.dto.movimentacao.MovimentacaoRequestDto;
import com.caeca.estoque.exception.BadRequestException;
import com.caeca.estoque.exception.NotFoundException;
import com.caeca.estoque.strategy.movimentacao.MovimentacaoContext;
import com.caeca.estoque.strategy.movimentacao.MovimentacaoStrategy;
import jakarta.validation.Valid;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MovimentacaoService {

    private final List<MovimentacaoStrategy> movimentacaoStrategies;
    private final MovimentacaoRepository movimentacaoRepository;
    private final TipoService tipoService;

    public MovimentacaoService(List<MovimentacaoStrategy> movimentacaoStrategies, MovimentacaoRepository movimentacaoRepository, TipoService tipoService) {
        this.movimentacaoStrategies = movimentacaoStrategies;
        this.movimentacaoRepository = movimentacaoRepository;
        this.tipoService = tipoService;
    }

    public List<Movimentacao> listar() {
        return movimentacaoRepository.findAll();
    }

    public Movimentacao buscarPorId(Long id) {
        return movimentacaoRepository.findById(id.intValue())
                .orElseThrow(() -> new NotFoundException("Movimentação com o Id " + id + " inexistente"));
    }

    public Movimentacao cadastrar(MovimentacaoRequestDto request) {
        TipoMovimentacao tipoMovimentacao = tipoService.buscarPorId(request.getIdTipoMovimentacao());

        MovimentacaoContext context = new MovimentacaoContext(movimentacaoStrategies
                .stream()
                .filter(m -> m.suporta(tipoMovimentacao))
                .findFirst()
                .orElseThrow(() -> new BadRequestException("Tipo de movimentação inválido")));

        Movimentacao movimentacao = context.executar(request);

        movimentacaoRepository.save(movimentacao);

        context.atualizarEstoque(movimentacao);

        return movimentacao;
    }
}
