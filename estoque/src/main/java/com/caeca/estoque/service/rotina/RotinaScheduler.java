package com.caeca.estoque.service.rotina;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;

@Component
public class RotinaScheduler {

    private static final Logger LOG = LoggerFactory.getLogger(RotinaScheduler.class);
    private static final ZoneId ZONA = ZoneId.of("America/Sao_Paulo");

    private final List<RotinaDiaria> rotinas;

    public RotinaScheduler(List<RotinaDiaria> rotinas) {
        this.rotinas = rotinas;
    }

    @Scheduled(cron = "${app.rotinas.cron}", zone = "America/Sao_Paulo")
    public void executar() {
        LocalDate hoje = LocalDate.now(ZONA);
        for (RotinaDiaria rotina : rotinas) {
            try {
                LOG.info("Iniciando rotina {}", rotina.nome());
                rotina.executar(hoje);
            } catch (Exception e) {
                LOG.error("Falha na rotina {}", rotina.nome(), e);
            }
        }
    }
}
