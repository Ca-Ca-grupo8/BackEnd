package com.caeca.estoque.service.movimentacaoRevendedora;

import com.caeca.estoque.database.model.movimentacaoRevendedora.MovimentacaoRevendedora;
import com.caeca.estoque.database.model.statusMaleta.StatusMaletaEnum;
import com.caeca.estoque.database.repository.movimentacao.MovimentacaoRepository;
import com.caeca.estoque.database.repository.movimentacaoRevendedora.MovimentacaoRevendedoraRepository;
import com.caeca.estoque.dto.movimentacaoRevendedora.JoiaSaidaMaletaDto;
import com.caeca.estoque.dto.movimentacaoRevendedora.MaletaResumoDto;
import com.caeca.estoque.dto.movimentacaoRevendedora.MovimentacaoRevendedoraMapper;
import com.caeca.estoque.dto.movimentacaoRevendedora.MovimentacaoRevendedoraRequestDto;
import com.caeca.estoque.dto.movimentacaoRevendedora.MovimentacaoRevendedoraResponseDto;
import com.caeca.estoque.dto.movimentacaoRevendedora.MovimentacaoRevendedoraStatusPatchDto;
import com.caeca.estoque.exception.NotFoundException;
import com.caeca.estoque.service.revendedora.RevendedoraService;
import com.caeca.estoque.service.statusMaleta.StatusMaletaService;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

@Service
public class MovimentacaoRevendedoraService {

    private static final List<StatusMaletaEnum> STATUS_FORA_DO_RESUMO = List.of(StatusMaletaEnum.FINALIZADA, StatusMaletaEnum.ERRO);
    private static final List<StatusMaletaEnum> STATUS_EM_ANDAMENTO = List.of(
            StatusMaletaEnum.EM_PREPARACAO, StatusMaletaEnum.EM_CONSIGNACAO, StatusMaletaEnum.EM_ACERTO);
    private static final int TOP_PROXIMOS_VENCIMENTOS = 5;

    private final MovimentacaoRevendedoraRepository mrRepository;
    private final MovimentacaoRepository movimentacaoRepository;
    private final RevendedoraService revendedoraService;
    private final StatusMaletaService statusMaletaService;
    private final MaletaNotificacaoService maletaNotificacaoService;
    private final MaletaMetaService maletaMetaService;


    public MovimentacaoRevendedoraService(MovimentacaoRevendedoraRepository mrRepository, MovimentacaoRepository movimentacaoRepository, RevendedoraService revendedoraService, StatusMaletaService statusMaletaService, MaletaNotificacaoService maletaNotificacaoService, MaletaMetaService maletaMetaService) {
        this.mrRepository = mrRepository;
        this.movimentacaoRepository = movimentacaoRepository;
        this.revendedoraService = revendedoraService;
        this.statusMaletaService = statusMaletaService;
        this.maletaNotificacaoService = maletaNotificacaoService;
        this.maletaMetaService = maletaMetaService;
    }

    public List<MovimentacaoRevendedora> listar() {
        return mrRepository.findAll();
    }

    public MovimentacaoRevendedora buscarPorId(Long id) {
        return mrRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Venda com o id " + id+ " não encontrada."));
    }

    public MovimentacaoRevendedora buscarOuCriarVendaPorRevendedora(MovimentacaoRevendedoraRequestDto requestDto){
        return mrRepository.findByRevendedoraIdAndStatusNome(requestDto.getIdRevendedora(), StatusMaletaEnum.EM_PREPARACAO)
                .orElseGet(() -> {
                    MovimentacaoRevendedora movimentacaoRevendedora = MovimentacaoRevendedoraMapper.toEntity(requestDto);
                    movimentacaoRevendedora.setRevendedora(revendedoraService.buscarPorId(requestDto.getIdRevendedora()));
                    movimentacaoRevendedora.setStatus(statusMaletaService.buscarPorNome(StatusMaletaEnum.EM_PREPARACAO));
                    int rand = ThreadLocalRandom.current().nextInt(1000, 10000);
                    movimentacaoRevendedora.setNome((movimentacaoRevendedora.getRevendedora().getNome()
                            + "-" + LocalDate.now().getMonth()
                            + "-" + LocalDate.now().getYear()
                            + "-" + rand).toLowerCase().strip());
                    return mrRepository.save(movimentacaoRevendedora);
                });
    }

    public MovimentacaoRevendedora buscarVendaAbertaPorRevendedora(Long idRevendedora) {
        return mrRepository.findByRevendedoraIdAndStatusNome(idRevendedora, StatusMaletaEnum.EM_PREPARACAO)
                .orElseThrow(() -> new NotFoundException("Venda com o id da revendora " + idRevendedora + " não encontrada."));
    }

    public MovimentacaoRevendedora buscarVendaNaoFechadaPorRevendedora(Long idRevendedora) {
        return mrRepository.findByRevendedoraIdAndStatusNomeNot(idRevendedora, StatusMaletaEnum.FINALIZADA)
                .orElseThrow(() -> new NotFoundException("Venda com o id da revendora " + idRevendedora + " não encontrada."));
    }

    public MovimentacaoRevendedora buscarVendaAtualPorRevendedora(Long idRevendedora) {
        return mrRepository.findFirstByRevendedoraIdAndStatusNomeNotInOrderByIdMovimentacaoRevendedoraDesc(
                        idRevendedora, List.of(StatusMaletaEnum.FINALIZADA, StatusMaletaEnum.ERRO))
                .orElseThrow(() -> new NotFoundException("Venda atual da revendedora " + idRevendedora + " não encontrada."));
    }

    public List<MovimentacaoRevendedora> listarPorIdRevendedora(Long idRevendedora){
        return mrRepository.findByRevendedoraId(idRevendedora);
    }

    public List<JoiaSaidaMaletaDto> listarJoiasComSaidaPorRevendedora(Long idRevendedora) {
        MovimentacaoRevendedora maleta = buscarVendaAtualPorRevendedora(idRevendedora);
        return movimentacaoRepository.buscarJoiasComSaidaNaMaleta(maleta.getIdMovimentacaoRevendedora());
    }

    public MovimentacaoRevendedora atualizarStatus(Long id, MovimentacaoRevendedoraStatusPatchDto patch) {
        MovimentacaoRevendedora response = buscarPorId(id);
        response.setStatus(statusMaletaService.buscarPorId(patch.getIdStatus()));

        mrRepository.save(response);

        notificarMudancaDeStatus(response);

        return response;
    }

    public MovimentacaoRevendedora criar(MovimentacaoRevendedoraRequestDto dto){
        MovimentacaoRevendedora entity = MovimentacaoRevendedoraMapper.toEntity(dto);
        entity.setRevendedora(revendedoraService.buscarPorId(dto.getIdRevendedora()));
        entity.setStatus(statusMaletaService.buscarPorNome(StatusMaletaEnum.EM_PREPARACAO));
        return mrRepository.save(entity);
    }

    public Page<MovimentacaoRevendedoraResponseDto> listarTodas(Pageable pageable) {
        return mrRepository.findAll(pageable)
                .map(MovimentacaoRevendedoraMapper::toResponse);
    }

    public MaletaResumoDto resumoNaoFinalizadas() {
        List<MovimentacaoRevendedora> maletas = mrRepository.findByStatusNomeNotIn(STATUS_FORA_DO_RESUMO);

        Map<StatusMaletaEnum, Long> porStatus = new EnumMap<>(StatusMaletaEnum.class);
        STATUS_EM_ANDAMENTO.forEach(status -> porStatus.put(status, 0L));
        maletas.forEach(maleta -> porStatus.merge(maleta.getStatus().getNome(), 1L, Long::sum));

        List<MaletaResumoDto.ProximoVencimentoDto> proximos = maletas.stream()
                .filter(maleta -> maleta.getDataLimite() != null)
                .sorted(Comparator.comparing(MovimentacaoRevendedora::getDataLimite))
                .limit(TOP_PROXIMOS_VENCIMENTOS)
                .map(maleta -> new MaletaResumoDto.ProximoVencimentoDto(
                        maleta.getIdMovimentacaoRevendedora(),
                        maleta.getNome(),
                        maleta.getStatus().getNome(),
                        maleta.getDataLimite()
                ))
                .toList();

        return new MaletaResumoDto(maletas.size(), porStatus, proximos);
    }

    private void notificarMudancaDeStatus(MovimentacaoRevendedora maleta) {
        StatusMaletaEnum status = maleta.getStatus() != null ? maleta.getStatus().getNome() : null;

        if (status == StatusMaletaEnum.EM_CONSIGNACAO) {
            maletaNotificacaoService.notificarLoteEmConsignacao(maleta);
            return;
        }

        if(status == StatusMaletaEnum.FINALIZADA) {
            maletaNotificacaoService.notificarFechamentoLote(maleta, maletaMetaService.montarParametrosMeta(maleta),
                    maletaMetaService.bateuMeta(maleta));
        }
    }
}
