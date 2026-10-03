package com.caeca.estoque.service.movimentacaoRevendedora;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class MaletaRotinaDiariaScheduler {

    private final MaletaRotinaDiariaService maletaRotinaDiariaService;

    public MaletaRotinaDiariaScheduler(MaletaRotinaDiariaService maletaRotinaDiariaService) {
        this.maletaRotinaDiariaService = maletaRotinaDiariaService;
    }

    @Scheduled(cron = "${app.maleta.cron}")
    public void executarRotinaDiaria() {
        maletaRotinaDiariaService.executar(LocalDate.now());
    }
}