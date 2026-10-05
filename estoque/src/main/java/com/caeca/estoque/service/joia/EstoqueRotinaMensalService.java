package com.caeca.estoque.service.joia;

import com.caeca.estoque.database.model.joia.Joia;
import com.caeca.estoque.database.repository.joia.JoiaRepository;
import com.caeca.estoque.database.repository.movimentacao.MovimentacaoRepository;
import com.caeca.estoque.dto.joia.JoiaParadaDto;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.logging.Logger;

@Service
public class EstoqueRotinaMensalService {

    private static final Logger LOG = Logger.getLogger(EstoqueRotinaMensalService.class.getName());
    private static final long DIAS_PARADA = 90L;

    private final JoiaRepository joiaRepository;
    private final EstoqueNotificacaoService estoqueNotificacaoService;

    public EstoqueRotinaMensalService(JoiaRepository joiaRepository,
                                     EstoqueNotificacaoService estoqueNotificacaoService) {
        this.joiaRepository = joiaRepository;
        this.estoqueNotificacaoService = estoqueNotificacaoService;
    }

    @Scheduled(cron = "${app.estoque.mensal.cron}", zone = "America/Sao_Paulo")
    public void executar() {
        LocalDateTime dataLimite = LocalDateTime.now().minusDays(DIAS_PARADA);

        List<JoiaParadaDto> joiasParadas = joiaRepository.findJoiasParadas(dataLimite);

        for (JoiaParadaDto item : joiasParadas) {
            LOG.warning(String.valueOf(item.dataUltimaSaida()));

            try {
                estoqueNotificacaoService.notificarPecaParada(
                        item.joia(),
                        item.quantidade(),
                        item.valorCustoTotal(),
                        item.dataUltimaSaida().toLocalDate()
                );
            } catch (Exception e) {
                LOG.warning("Falha ao notificar peca parada para joia " + item.joia().getId() + ": " + e.getMessage());
            }
        }
    }
}
