package com.caeca.estoque.service.dashboard;

import com.caeca.estoque.database.repository.joia.JoiaRepository;
import com.caeca.estoque.database.repository.movimentacao.MovimentacaoRepository;
import com.caeca.estoque.dto.dashboard.FiltroDashboardDTO;
import com.caeca.estoque.dto.dashboard.FiltroGiroEstoque;
import com.caeca.estoque.dto.dashboard.TopCincoDto;
import com.caeca.estoque.dto.dashboard.ValorSaidaDto;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.TemporalAdjusters;
import java.util.List;
import java.util.Map;

@Service
public class DashboardService {
    private final MovimentacaoRepository movimentacaoRepository;
    private final JoiaRepository joiaRepository;

    public DashboardService(MovimentacaoRepository movimentacaoRepository, JoiaRepository joiaRepository) {
        this.movimentacaoRepository = movimentacaoRepository;
        this.joiaRepository = joiaRepository;
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

    public List<TopCincoDto> buscarTopCincoMaisVendidos(FiltroDashboardDTO filtro) {
        LocalDateTime inicio = filtro.dataInicio();
        LocalDateTime fim = filtro.dataTermino();

        if (inicio == null || fim == null) {
            LocalDateTime hoje = LocalDateTime.now();
            inicio = hoje.minusMonths(1).withDayOfMonth(1).with(LocalTime.MIN);
            fim = inicio.with(TemporalAdjusters.lastDayOfMonth()).with(LocalTime.MAX);
        }

        if(filtro.categoriaId() != null){
            return movimentacaoRepository.buscarTopCincoProdutos(
                    inicio,
                    fim,
                    filtro.categoriaId(),
                    PageRequest.of(0,5));
        }

        return movimentacaoRepository.buscarTopCincoCategorias(
                    inicio,
                    fim,
                    PageRequest.of(0, 5));
    }

    public FiltroGiroEstoque calcularGiroEstoque(FiltroDashboardDTO filtro) {
        LocalDateTime inicio = filtro.dataInicio();
        LocalDateTime fim = filtro.dataTermino();

        if (inicio == null || fim == null) {
            LocalDateTime hoje = LocalDateTime.now();
            inicio = hoje.minusMonths(1).withDayOfMonth(1).with(LocalTime.MIN);
            fim = inicio.with(TemporalAdjusters.lastDayOfMonth()).with(LocalTime.MAX);
        }

        double giroEstoque = calcularGiroEstoque(inicio, fim);

        LocalDateTime inicioAntigo = inicio.minusMonths(1);
        LocalDateTime fimAntigo = fim.minusMonths(1);

        double giroEstoqueAntigo = calcularGiroEstoque(inicioAntigo, fimAntigo);

        double varianca = 0.0;
        if (giroEstoqueAntigo > 0.0) {
            varianca = ((giroEstoque - giroEstoqueAntigo) / giroEstoqueAntigo) * 100;
        } else if (giroEstoque > 0.0) {
            varianca = 100.0;
        }

        return new FiltroGiroEstoque(
                giroEstoque,
                giroEstoqueAntigo,
                varianca
        );
    }

    private double calcularGiroEstoque(LocalDateTime inicio, LocalDateTime fim){
        long totalJoiasAtual = joiaRepository.somarTotalEstoque();

        long vendasAposFim = movimentacaoRepository.buscarTotalDeItensVendidosApos(fim);
        long entradasAposFim = movimentacaoRepository.buscarTotalDeEntradasApos(fim);

        double estoqueFinal = (double) (totalJoiasAtual + vendasAposFim - entradasAposFim);

        long totalVendido = movimentacaoRepository.buscarTotalDeItensVendidos(inicio, fim);
        long totalDeEntradas = movimentacaoRepository.buscarTotalDeEntradas(inicio, fim);

        double estoqueInicial = (double) (estoqueFinal + totalVendido - totalDeEntradas);

        double estoqueMedio = (estoqueInicial + estoqueFinal) / 2.0;

        if (estoqueMedio <= 0.0) {
            return 0.0;
        }

        return totalVendido / estoqueMedio;
    }
}
