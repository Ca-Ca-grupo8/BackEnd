package com.caeca.estoque.service.movimentacaoRevendedora;

import com.caeca.estoque.database.model.movimentacaoRevendedora.MovimentacaoRevendedora;
import com.caeca.estoque.database.model.statusMaleta.StatusMaletaEnum;
import com.caeca.estoque.database.repository.movimentacaoRevendedora.MovimentacaoRevendedoraRepository;
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
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@Service
public class MovimentacaoRevendedoraService {

    private final MovimentacaoRevendedoraRepository mrRepository;
    private final RevendedoraService revendedoraService;
    private final StatusMaletaService statusMaletaService;
    private final MaletaNotificacaoService maletaNotificacaoService;
    private final MaletaMetaService maletaMetaService;


    public MovimentacaoRevendedoraService(MovimentacaoRevendedoraRepository mrRepository, RevendedoraService revendedoraService, StatusMaletaService statusMaletaService, MaletaNotificacaoService maletaNotificacaoService, MaletaMetaService maletaMetaService) {
        this.mrRepository = mrRepository;
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
        return mrRepository.findByRevendedoraIdAndStatusNome(requestDto.getIdRevendedora(), StatusMaletaEnum.ABERTO)
                .orElseGet(() -> {
                    MovimentacaoRevendedora movimentacaoRevendedora = MovimentacaoRevendedoraMapper.toEntity(requestDto);
                    movimentacaoRevendedora.setRevendedora(revendedoraService.buscarPorId(requestDto.getIdRevendedora()));
                    movimentacaoRevendedora.setStatus(statusMaletaService.buscarPorNome(StatusMaletaEnum.ABERTO));
                    int rand = ThreadLocalRandom.current().nextInt(1000, 10000);
                    movimentacaoRevendedora.setNome((movimentacaoRevendedora.getRevendedora().getNome()
                            + "-" + LocalDate.now().getMonth()
                            + "-" + LocalDate.now().getYear()
                            + "-" + rand).toLowerCase().strip());
                    return mrRepository.save(movimentacaoRevendedora);
                });
    }

    public MovimentacaoRevendedora buscarVendaAbertaPorRevendedora(Long idRevendedora) {
        return mrRepository.findByRevendedoraIdAndStatusNome(idRevendedora, StatusMaletaEnum.ABERTO)
                .orElseThrow(() -> new NotFoundException("Venda com o id da revendora " + idRevendedora + " não encontrada."));
    }

    public MovimentacaoRevendedora buscarVendaNaoFechadaPorRevendedora(Long idRevendedora) {
        return mrRepository.findByRevendedoraIdAndStatusNomeNot(idRevendedora, StatusMaletaEnum.FINALIZADA)
                .orElseThrow(() -> new NotFoundException("Venda com o id da revendora " + idRevendedora + " não encontrada."));
    }

    public List<MovimentacaoRevendedora> listarPorIdRevendedora(Long idRevendedora){
        return mrRepository.findByRevendedoraId(idRevendedora);
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
        entity.setStatus(statusMaletaService.buscarPorNome(StatusMaletaEnum.ABERTO));
        return mrRepository.save(entity);
    }

    public Page<MovimentacaoRevendedoraResponseDto> listarTodas(Pageable pageable) {
        return mrRepository.findAll(pageable)
                .map(MovimentacaoRevendedoraMapper::toResponse);
    }

    private void notificarMudancaDeStatus(MovimentacaoRevendedora maleta) {
        StatusMaletaEnum status = maleta.getStatus() != null ? maleta.getStatus().getNome() : null;

        if (status == StatusMaletaEnum.EM_CONSIGNACAO) {
            maletaNotificacaoService.notificarLoteEmConsignacao(maleta);
            return;
        }

        if (status == StatusMaletaEnum.FINALIZADA) {
            if (maletaMetaService.bateuMeta(maleta)) {
                maletaNotificacaoService.notificarMetaAtingida(maleta, maletaMetaService.montarParametrosMeta(maleta));
                return;
            }
            maletaNotificacaoService.notificarFechamentoLote(maleta);
        }
    }
}
