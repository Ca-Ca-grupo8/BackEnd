package com.caeca.estoque.service.movimentacao;

import com.caeca.estoque.database.model.movimentacao.Movimentacao;
import com.caeca.estoque.database.model.movimentacaoRevendedora.MovimentacaoRevendedora;
import com.caeca.estoque.database.model.tipoMovimentacao.TipoMovimentacao;
import com.caeca.estoque.database.repository.movimentacao.MovimentacaoRepository;
import com.caeca.estoque.dto.movimentacao.MovimentacaoContextDto;
import com.caeca.estoque.dto.movimentacao.MovimentacaoRequestDto;
import com.caeca.estoque.exception.BadRequestException;
import com.caeca.estoque.exception.NotFoundException;
import com.caeca.estoque.service.joia.JoiaService;
import com.caeca.estoque.service.tipoMovimentacao.TipoMovimentacaoService;
import com.caeca.estoque.service.usuario.UsuarioService;
import com.caeca.estoque.strategy.movimentacao.MovimentacaoContext;
import com.caeca.estoque.strategy.movimentacao.MovimentacaoStrategy;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@Service
public class MovimentacaoService {

    private final List<MovimentacaoStrategy> movimentacaoStrategies;
    private final MovimentacaoRepository movimentacaoRepository;
    private final TipoMovimentacaoService tipoService;
    private final JoiaService joiaService;
    private final UsuarioService usuarioService;

    public MovimentacaoService(List<MovimentacaoStrategy> movimentacaoStrategies, MovimentacaoRepository movimentacaoRepository, TipoMovimentacaoService tipoService, JoiaService joiaService, UsuarioService usuarioService) {
        this.movimentacaoStrategies = movimentacaoStrategies;
        this.movimentacaoRepository = movimentacaoRepository;
        this.tipoService = tipoService;
        this.joiaService = joiaService;
        this.usuarioService = usuarioService;
    }

    public List<Movimentacao> listar() {
        return movimentacaoRepository.findAll();
    }

    public Movimentacao buscarPorId(Long id) {
        return movimentacaoRepository.findById(id.intValue())
                .orElseThrow(() -> new NotFoundException("Movimentação com o Id " + id + " inexistente"));
    }

    @Transactional
    public Movimentacao cadastrar(MovimentacaoRequestDto request, MultipartFile arquivo) {
            TipoMovimentacao tipoMovimentacao = tipoService.buscarPorId(request.getIdTipoMovimentacao());
            MovimentacaoContextDto movimentacaoContextDto = new MovimentacaoContextDto(
                    tipoMovimentacao,
                    usuarioService.getUsuarioLogado(),
                    request,
                    arquivo
            );

            MovimentacaoContext context = new MovimentacaoContext(movimentacaoStrategies
                    .stream()
                    .filter(m -> m.suporta(tipoMovimentacao))
                    .findFirst()
                    .orElseThrow(() -> new BadRequestException("Tipo de movimentação inválido")));

            Movimentacao movimentacao = context.executar(movimentacaoContextDto);

            movimentacaoRepository.save(movimentacao);

            context.atualizarEstoque(movimentacao);

            MovimentacaoRevendedora sessao = movimentacao.getMovimentacaoRevendedora();
            if (sessao != null) {
                if (sessao.getMovimentacoes() == null) {
                    sessao.setMovimentacoes(new ArrayList<>());
                }
                sessao.getMovimentacoes().add(movimentacao);
            }

            movimentacaoRepository.flush();

            return movimentacao;
    }
}
