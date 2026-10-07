package com.caeca.estoque.service.revendedora;

import com.caeca.estoque.database.model.movimentacaoRevendedora.MovimentacaoRevendedora;
import com.caeca.estoque.database.model.notificacaoEnvio.TipoReferencia;
import com.caeca.estoque.database.model.revendedora.Revendedora;
import com.caeca.estoque.database.repository.movimentacaoRevendedora.MovimentacaoRevendedoraRepository;
import com.caeca.estoque.database.repository.revendedora.RevendedoraRepository;
import com.caeca.estoque.service.rotina.RotinaDiaria;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.logging.Logger;

@Service
public class RevendedoraRotinaDiariaService implements RotinaDiaria {

    private static final Logger LOG = Logger.getLogger(RevendedoraRotinaDiariaService.class.getName());

    private final RevendedoraRepository revendedoraRepository;
    private final RevendedoraNotificacaoService revendedoraNotificacaoService;
    private final MovimentacaoRevendedoraRepository movimentacaoRevendedoraRepository;

    public RevendedoraRotinaDiariaService(RevendedoraRepository revendedoraRepository,
                                          RevendedoraNotificacaoService revendedoraNotificacaoService, MovimentacaoRevendedoraRepository movimentacaoRevendedoraRepository) {
        this.revendedoraRepository = revendedoraRepository;
        this.revendedoraNotificacaoService = revendedoraNotificacaoService;
        this.movimentacaoRevendedoraRepository = movimentacaoRevendedoraRepository;
    }

    @Override
    public TipoReferencia nome() {
        return TipoReferencia.REVENDEDORA;
    }

    @Override
    public void executar(LocalDate hoje) {
        notificarAniversariantes(hoje);
        notificarRevendedorasInativas(hoje);
    }

    private void notificarAniversariantes(LocalDate hoje) {
        List<Revendedora> aniversariantes = revendedoraRepository.findAniversariantes(
                hoje.getDayOfMonth(), hoje.getMonthValue());
        for (Revendedora revendedora : aniversariantes) {
            try {
                revendedoraNotificacaoService.notificarAniversario(revendedora);
            } catch (Exception e) {
                LOG.warning("Falha ao notificar aniversario da revendedora " + revendedora.getId() + ": " + e.getMessage());
            }
        }
    }

    private void notificarRevendedorasInativas(LocalDate hoje) {
        for (Revendedora revendedora : revendedoraRepository.findByAtivoTrue()) {
            try {
                LocalDateTime ultimaAtividade = obterUltimaAtividade(revendedora);
                if (ultimaAtividade == null) {
                    continue;
                }

                long diasInativos = ChronoUnit.DAYS.between(ultimaAtividade, hoje.atStartOfDay());
                if (diasInativos >= 60) {
                    revendedoraNotificacaoService.notificarInatividade(revendedora, diasInativos);
                }
            } catch (Exception e) {
                LOG.warning("Falha ao notificar inatividade da revendedora "
                        + revendedora.getId() + ": " + e.getMessage());
            }
        }
    }

    private LocalDateTime obterUltimaAtividade(Revendedora revendedora) {
        MovimentacaoRevendedora venda = movimentacaoRevendedoraRepository
                .findFirstByRevendedoraIdOrderByIdMovimentacaoRevendedoraDesc(revendedora.getId());

        if (venda != null && venda.getData() != null) {
            return venda.getData();
        }
        if (revendedora.getDataCadastro() != null) {
            return revendedora.getDataCadastro().atStartOfDay();
        }
        return null;
    }
}
