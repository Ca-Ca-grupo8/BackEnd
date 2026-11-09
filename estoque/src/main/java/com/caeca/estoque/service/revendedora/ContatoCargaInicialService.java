package com.caeca.estoque.service.revendedora;

import com.caeca.estoque.database.model.movimentacaoRevendedora.MovimentacaoRevendedora;
import com.caeca.estoque.database.model.revendedora.Revendedora;
import com.caeca.estoque.database.repository.movimentacaoRevendedora.MovimentacaoRevendedoraRepository;
import com.caeca.estoque.database.repository.revendedora.RevendedoraRepository;
import com.caeca.estoque.service.NotificacaoBrevo.NotificacaoBrevoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

/**
 * Envia para o Brevo os contatos de todas as revendedoras já existentes, para que a automação de
 * inatividade enxergue quem foi cadastrado antes da sincronização.
 *
 * <p>Roda uma única vez, na inicialização, quando {@code app.contatos.carga-inicial.enabled=true}.
 * Depois da execução, desligue a flag. Cada revendedora é publicada como CRIAR, que faz upsert
 * por {@code ext_id}: cria o contato se não existir e atualiza se já existir.
 */
@Slf4j
@Component
@ConditionalOnProperty(name = "app.contatos.carga-inicial.enabled", havingValue = "true")
public class ContatoCargaInicialService implements ApplicationRunner {

    private final RevendedoraRepository revendedoraRepository;
    private final MovimentacaoRevendedoraRepository movimentacaoRepository;
    private final NotificacaoBrevoService notificacaoBrevoService;

    public ContatoCargaInicialService(RevendedoraRepository revendedoraRepository,
                                      MovimentacaoRevendedoraRepository movimentacaoRepository,
                                      NotificacaoBrevoService notificacaoBrevoService) {
        this.revendedoraRepository = revendedoraRepository;
        this.movimentacaoRepository = movimentacaoRepository;
        this.notificacaoBrevoService = notificacaoBrevoService;
    }

    @Override
    public void run(ApplicationArguments args) {
        executar();
    }

    public void executar() {
        int publicadas = 0;
        int falhas = 0;

        for (Revendedora revendedora : revendedoraRepository.findAll()) {
            try {
                notificacaoBrevoService.criarContatoRevendedora(revendedora, dataUltimaMaleta(revendedora));
                publicadas++;
            } catch (RuntimeException e) {
                falhas++;
                log.error("Falha na carga inicial do contato da revendedora {}.", revendedora.getId(), e);
            }
        }

        log.info("Carga inicial de contatos concluida. publicadas={} falhas={}", publicadas, falhas);
    }

    /** Mesma regra da automação: data da última maleta; sem maleta, a data de cadastro. */
    private LocalDate dataUltimaMaleta(Revendedora revendedora) {
        MovimentacaoRevendedora ultima = movimentacaoRepository
                .findFirstByRevendedoraIdOrderByIdMovimentacaoRevendedoraDesc(revendedora.getId());

        if (ultima != null && ultima.getData() != null) {
            return ultima.getData().toLocalDate();
        }
        return revendedora.getDataCadastro();
    }
}
