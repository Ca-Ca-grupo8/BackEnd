package com.caeca.estoque.service.movimentacaoRevendedora;

import com.caeca.estoque.database.model.movimentacaoRevendedora.MovimentacaoRevendedora;
import com.caeca.estoque.database.model.statusMaleta.StatusMaletaEnum;
import com.caeca.estoque.database.repository.movimentacaoRevendedora.MovimentacaoRevendedoraRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.logging.Logger;

@Service
public class MaletaRotinaDiariaService {

    private static final Logger LOG = Logger.getLogger(MaletaRotinaDiariaService.class.getName());

    public static final long DIAS_ALERTA_LIMITE = 7L;
    public static final long DIAS_ALERTA_PROXIMO = 3L;

    private final MovimentacaoRevendedoraRepository mrRepository;
    private final MaletaNotificacaoService maletaNotificacaoService;

    @Value("${app.maleta.cron}")
    private String cron;

    public MaletaRotinaDiariaService(MovimentacaoRevendedoraRepository mrRepository,
                                     MaletaNotificacaoService maletaNotificacaoService) {
        this.mrRepository = mrRepository;
        this.maletaNotificacaoService = maletaNotificacaoService;
    }

    public void executar(LocalDate hoje) {
        List<MovimentacaoRevendedora> comVencimento = mrRepository.findByDataLimiteBetween(
                LocalDateTime.of(hoje, java.time.LocalTime.MIN),
                LocalDateTime.of(hoje.plusDays(DIAS_ALERTA_LIMITE), java.time.LocalTime.MAX)
        );

        for (MovimentacaoRevendedora maleta : comVencimento) {
            long diasRestantes = diasAteVencimento(maleta, hoje);

            if (diasRestantes == DIAS_ALERTA_LIMITE || diasRestantes == DIAS_ALERTA_PROXIMO) {
                maletaNotificacaoService.notificarAlertaVencimento(maleta, diasRestantes);
            }
        }

        List<MovimentacaoRevendedora> vencidas = mrRepository.findByDataLimiteBefore(
                LocalDateTime.of(hoje, java.time.LocalTime.MIN)
        );

        for (MovimentacaoRevendedora maleta : vencidas) {
            if (isEmConsignacao(maleta)) {
                maletaNotificacaoService.notificarCobrancaAtraso(maleta);
            }
        }

        LOG.info("Rotina diaria de maletas executada. vencimento=" + comVencimento.size()
                + " vencidas=" + vencidas.size()
                + " cron=" + cron);
    }

    public long diasAteVencimento(MovimentacaoRevendedora maleta, LocalDate hoje) {
        if (maleta.getDataLimite() == null) {
            return 0L;
        }

        return ChronoUnit.DAYS.between(hoje, maleta.getDataLimite().toLocalDate());
    }

    private boolean isEmConsignacao(MovimentacaoRevendedora maleta) {
        return maleta.getStatus() != null
                && maleta.getStatus().getNome() == StatusMaletaEnum.EM_CONSIGNACAO;
    }
}