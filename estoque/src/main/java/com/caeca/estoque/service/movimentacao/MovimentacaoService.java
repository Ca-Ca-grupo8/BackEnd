package com.caeca.estoque.service.movimentacao;

import com.caeca.estoque.database.model.movimentacao.Movimentacao;
import com.caeca.estoque.database.model.movimentacaoRevendedora.MovimentacaoRevendedora;
import com.caeca.estoque.database.model.motivoMovimentacao.MotivoMovimentacao;
import com.caeca.estoque.database.model.tipoMovimentacao.TipoMovimentacaoEnum;
import com.caeca.estoque.database.repository.movimentacao.MovimentacaoRepository;
import com.caeca.estoque.dto.movimentacao.MovimentacaoContextDto;
import com.caeca.estoque.dto.movimentacao.MovimentacaoMapper;
import com.caeca.estoque.dto.movimentacao.MovimentacaoRequestDto;
import com.caeca.estoque.dto.movimentacao.MovimentacaoResponseDto;
import com.caeca.estoque.exception.BadRequestException;
import com.caeca.estoque.exception.NotFoundException;
import com.caeca.estoque.service.joia.JoiaService;
import com.caeca.estoque.service.motivoMovimentacao.MotivoMovimentacaoService;
import com.caeca.estoque.service.paginacao.PaginacaoService;
import com.caeca.estoque.service.usuario.UsuarioService;
import com.caeca.estoque.strategy.movimentacao.MovimentacaoContext;
import com.caeca.estoque.strategy.movimentacao.MovimentacaoStrategy;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class MovimentacaoService {

    private final List<MovimentacaoStrategy> movimentacaoStrategies;
    private final MovimentacaoRepository movimentacaoRepository;
    private final MotivoMovimentacaoService motivoMovimentacaoService;
    private final JoiaService joiaService;
    private final UsuarioService usuarioService;
    private final PaginacaoService paginacaoService;

    public MovimentacaoService(List<MovimentacaoStrategy> movimentacaoStrategies, MovimentacaoRepository movimentacaoRepository, MotivoMovimentacaoService motivoMovimentacaoService, JoiaService joiaService, UsuarioService usuarioService, PaginacaoService paginacaoService) {
        this.movimentacaoStrategies = movimentacaoStrategies;
        this.movimentacaoRepository = movimentacaoRepository;
        this.motivoMovimentacaoService = motivoMovimentacaoService;
        this.joiaService = joiaService;
        this.usuarioService = usuarioService;
        this.paginacaoService = paginacaoService;
    }

    public List<Movimentacao> listar(String nome, TipoMovimentacaoEnum tipo, LocalDate dataInicio, LocalDate dataFim) {
        LocalDateTime inicioComHora = (dataInicio != null) ? dataInicio.atStartOfDay() : null;
        LocalDateTime fimComHora = (dataFim != null) ? dataFim.atTime(LocalTime.MAX) : null;

        return movimentacaoRepository.buscarComFiltros(nome, tipo, inicioComHora, fimComHora);
    }

    public Movimentacao buscarPorId(Long id) {
        return movimentacaoRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Movimentação com o Id " + id + " inexistente"));
    }

    @Transactional
    public Movimentacao cadastrar(MovimentacaoRequestDto request, MultipartFile arquivo) {
            MotivoMovimentacao motivoMovimentacao = motivoMovimentacaoService.buscarPorId(request.getIdMotivoMovimentacao());
            MovimentacaoContextDto movimentacaoContextDto = new MovimentacaoContextDto(
                    motivoMovimentacao,
                    usuarioService.getUsuarioLogado(),
                    request,
                    arquivo
            );

            MovimentacaoContext context = new MovimentacaoContext(movimentacaoStrategies
                    .stream()
                    .filter(m -> m.suporta(motivoMovimentacao))
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
    public Page<MovimentacaoResponseDto> listarTodas(Pageable pageable) {
        return paginacaoService.buscarPaginado(
                movimentacaoRepository,
                pageable,
                MovimentacaoMapper::toResponse
        );
    }
}
