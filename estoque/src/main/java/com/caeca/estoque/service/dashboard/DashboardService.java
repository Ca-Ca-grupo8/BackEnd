package com.caeca.estoque.service.dashboard;

import com.caeca.estoque.database.repository.movimentacao.MovimentacaoRepository;
import com.caeca.estoque.dto.dashboard.FiltroDashboardDTO;
import com.caeca.estoque.dto.dashboard.ValorSaidaDto;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.TemporalAdjusters;

@Service
public class DashboardService {
    private final MovimentacaoRepository movimentacaoRepository;

    public DashboardService(MovimentacaoRepository movimentacaoRepository) {
        this.movimentacaoRepository = movimentacaoRepository;
    }

    public ValorSaidaDto calcularValorSaida(FiltroDashboardDTO filtro){
        LocalDateTime inicio = filtro.dataInicio();
        LocalDateTime fim = filtro.dataTermino();

        if (inicio == null || fim == null) {
            LocalDateTime hoje = LocalDateTime.now();
            inicio = hoje.minusMonths(1).withDayOfMonth(1).with(LocalTime.MIN);
            fim = inicio.with(TemporalAdjusters.lastDayOfMonth()).with(LocalTime.MAX);
        }

        Double atualBusca = movimentacaoRepository.somarValorTotalDasSaidas(inicio, fim);
        Double atual = (atualBusca != null) ? atualBusca : 0.0;

        Double anteriorBusca = movimentacaoRepository.somarValorTotalDasSaidas(inicio.minusMonths(1), fim.minusMonths(1));
        Double anterior = (anteriorBusca != null) ? anteriorBusca : 0.0;

        Double varianca = 0.0;

        if (anterior > 0.0) {
            varianca = ((atual - anterior) / anterior) * 100;
        } else if (atual > 0.0) {
            varianca = 100.0;
        }

        return new ValorSaidaDto(
                atual,
                anterior,
                varianca
        );
    }
}
