package com.caeca.estoque.service.dashboard;

import com.caeca.estoque.database.model.tipoMovimentacao.TipoMovimentacaoEnum;
import com.caeca.estoque.database.model.historicoEstoque.HistoricoEstoque;
import com.caeca.estoque.database.repository.historicoEstoque.HistoricoEstoqueRepository;
import com.caeca.estoque.database.repository.joia.JoiaRepository;
import com.caeca.estoque.database.repository.movimentacao.MovimentacaoRepository;
import com.caeca.estoque.database.repository.tipoMovimentacao.MovimentacaoDashboardRepository;
import com.caeca.estoque.dto.dashboard.*;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.TemporalAdjusters;
import java.util.List;
import java.util.Map;

@Service
public class DashboardService {
    private final MovimentacaoDashboardRepository dashboardRepository;
    private final MovimentacaoRepository movimentacaoRepository;
    private final JoiaRepository joiaRepository;
    private final HistoricoEstoqueRepository historicoEstoqueRepository;

    public DashboardService(MovimentacaoDashboardRepository dashboardRepository,
                             MovimentacaoRepository movimentacaoRepository,
                             JoiaRepository joiaRepository,
                             HistoricoEstoqueRepository historicoEstoqueRepository) {
        this.dashboardRepository = dashboardRepository;
        this.movimentacaoRepository = movimentacaoRepository;
        this.joiaRepository = joiaRepository;
        this.historicoEstoqueRepository = historicoEstoqueRepository;
    }

    public Map<String, Object> buscarKpisEgraficoGeral(FiltroDashboardDto filtro) {
        PeriodoFiltro periodo = resolverPeriodo(filtro);

        LocalDateTime inicioAnterior = periodo.inicio().minusMonths(1);
        LocalDateTime fimAnterior = periodo.fim().minusMonths(1);

        List<TipoMovimentacaoEnum> tipoEntrada = List.of(TipoMovimentacaoEnum.ENTRADA, TipoMovimentacaoEnum.REENTRADA);
        List<TipoMovimentacaoEnum> tipoSaida = List.of(TipoMovimentacaoEnum.SAIDA);

        Long categoriaId = filtro.categoriaId();

        long entradasAtuais = dashboardRepository.somarQuantidadePorTipo(tipoEntrada, periodo.inicio(), periodo.fim(), categoriaId);
        long saidasAtuais = dashboardRepository.somarQuantidadePorTipo(tipoSaida, periodo.inicio(), periodo.fim(), categoriaId);

        long entradasPassadas = dashboardRepository.somarQuantidadePorTipo(tipoEntrada, inicioAnterior, fimAnterior, categoriaId);
        long saidasPassadas = dashboardRepository.somarQuantidadePorTipo(tipoSaida, inicioAnterior, fimAnterior, categoriaId);

        long totalEstoque = joiaRepository.somarTotalEstoquePorCategoria(categoriaId);

        // 5. Dados do Gráfico de Barras (Formatados em Map)
        List<Object[]> dadosGraficoRaw = dashboardRepository
                .buscarDadosParaGrafico(periodo.inicio(), periodo.fim(), filtro.categoriaId());
        List<Map<String, Object>> graficoFormatted = dadosGraficoRaw.stream()
                .map(linha -> Map.of(
                        "dataAgrupada", linha[0],
                        "totalEntradas", linha[1],
                        "totalSaidas", linha[2]
                ))
                .toList();

        // Histórico de estoque: estoque no início do período (se não existir, aproxima)
        long estoquePassado;
        java.util.Optional<HistoricoEstoque> historicoPassado = historicoEstoqueRepository
                .findTopByDataLessThanEqualOrderByDataDesc(periodo.inicio().toLocalDate());
        if (historicoPassado.isPresent()) {
            estoquePassado = historicoPassado.get().getTotalEstoque();
        } else {
            estoquePassado = totalEstoque - entradasAtuais + saidasAtuais;
        }
        if (estoquePassado < 0) {
            estoquePassado = 0;
        }

        Map<String, Object> kpis = Map.of(
                "totalEntradas", entradasAtuais,
                "totalSaidas", saidasAtuais,
                "totalEmEstoque", totalEstoque,
                "entradasPassadas", entradasPassadas,
                "saidasPassadas", saidasPassadas,
                "estoquePassado", estoquePassado
        );

        return Map.of(
                "kpis", kpis,
                "grafico", graficoFormatted
        );
    }

    public ValorSaidaDto calcularValorSaida(FiltroDashboardDto filtro){
        PeriodoFiltro periodo = resolverPeriodo(filtro);
        LocalDateTime inicio = periodo.inicio();
        LocalDateTime fim = periodo.fim();

        Double atualBusca = movimentacaoRepository.somarValorTotalDasSaidas(inicio, fim, filtro.categoriaId());
        Double atual = (atualBusca != null) ? atualBusca : 0.0;

        Double anteriorBusca = movimentacaoRepository.somarValorTotalDasSaidas(
                inicio.minusMonths(1), fim.minusMonths(1), filtro.categoriaId());
        Double anterior = (anteriorBusca != null) ? anteriorBusca : 0.0;

        Double varianca = 0.0;

        if (anterior > 0.0) {
            varianca = ((atual - anterior) / anterior) * 100;
        } else if (atual > 0.0) {
            varianca = 100.0;
        }

        ValorSaidaDto dto = new ValorSaidaDto();
        dto.setValorAtual(atual);
        dto.setValorAntigo(anterior);
        dto.setVarianca(varianca);
        return dto;
    }

    public List<TopCincoDto> buscarTopCincoMaisVendidos(FiltroDashboardDto filtro) {
        PeriodoFiltro periodo = resolverPeriodo(filtro);
        LocalDateTime inicio = periodo.inicio();
        LocalDateTime fim = periodo.fim();

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

    public FiltroGiroEstoqueDto calcularGiroEstoque(FiltroDashboardDto filtro) {
        PeriodoFiltro periodo = resolverPeriodo(filtro);
        LocalDateTime inicio = periodo.inicio();
        LocalDateTime fim = periodo.fim();

        double giroEstoque = calcularGiroEstoque(inicio, fim, filtro.categoriaId());

        LocalDateTime inicioAntigo = inicio.minusMonths(1);
        LocalDateTime fimAntigo = fim.minusMonths(1);

        double giroEstoqueAntigo = calcularGiroEstoque(inicioAntigo, fimAntigo, filtro.categoriaId());

        double varianca = 0.0;
        if (giroEstoqueAntigo > 0.0) {
            varianca = ((giroEstoque - giroEstoqueAntigo) / giroEstoqueAntigo) * 100;
        } else if (giroEstoque > 0.0) {
            varianca = 100.0;
        }

        return new FiltroGiroEstoqueDto(
                giroEstoque,
                giroEstoqueAntigo,
                varianca
        );
    }

    private double calcularGiroEstoque(LocalDateTime inicio, LocalDateTime fim, Long categoriaId){
        long totalJoiasAtual = joiaRepository.somarTotalEstoquePorCategoria(categoriaId);

        long vendasAposFim = movimentacaoRepository.buscarTotalDeItensVendidosApos(fim, categoriaId);
        long entradasAposFim = movimentacaoRepository.buscarTotalDeEntradasApos(fim, categoriaId);

        double estoqueFinal = (double) (totalJoiasAtual + vendasAposFim - entradasAposFim);

        long totalVendido = movimentacaoRepository.buscarTotalDeItensVendidos(inicio, fim, categoriaId);
        long totalDeEntradas = movimentacaoRepository.buscarTotalDeEntradas(inicio, fim, categoriaId);

        double estoqueInicial = (double) (estoqueFinal + totalVendido - totalDeEntradas);

        double estoqueMedio = (estoqueInicial + estoqueFinal) / 2.0;

        if (estoqueMedio <= 0.0) {
            return 0.0;
        }

        return totalVendido / estoqueMedio;
    }

    public List<GraficoMotivoDto> obterGraficoMotivosSaida(LocalDateTime inicio, LocalDateTime fim, Long categoriaId) {
        return movimentacaoRepository.buscarGraficoMotivosSaida(inicio, fim, categoriaId);
    }

    public List<GraficoMotivoDto> obterGraficoMotivosReentrada(LocalDateTime inicio, LocalDateTime fim, Long categoriaId) {
        return movimentacaoRepository.buscarGraficoMotivosReentrada(inicio, fim, categoriaId);
    }

    public List<TopCincoMaisFriasDto> buscarTopCincoMaisFrios(FiltroDashboardDto filtro) {

        PeriodoFiltro periodo = resolverPeriodo(filtro);
        LocalDateTime inicio = periodo.inicio();
        LocalDateTime fim = periodo.fim();

        return movimentacaoRepository.buscarProdutosFrios(
                inicio,
                fim,
                filtro.categoriaId(),
                PageRequest.of(0, 5)
        );
    }

    public IndicePecasFriasDto buscarIndicePecasFrias(
            FiltroDashboardDto filtro
    ) {

        PeriodoFiltro periodo = resolverPeriodo(filtro);

        LocalDateTime inicioMesAnterior =
                periodo.inicio().minusMonths(1);

        LocalDateTime fimMesAnterior =
                periodo.fim().minusMonths(1);

        Number resultadoAtual =
                movimentacaoRepository.buscarIndiceAtual(
                        periodo.inicio(),
                        periodo.fim(),
                        filtro.categoriaId()
                );

        Number resultadoAnterior =
                movimentacaoRepository.buscarIndiceMesAnterior(
                        inicioMesAnterior,
                        fimMesAnterior,
                        filtro.categoriaId()
                );

        double indiceAtual =
                resultadoAtual != null
                        ? resultadoAtual.doubleValue()
                        : 0.0;

        double indiceAnterior =
                resultadoAnterior != null
                        ? resultadoAnterior.doubleValue()
                        : 0.0;

        double diferenca =
                indiceAtual - indiceAnterior;

        return new IndicePecasFriasDto(
                indiceAtual,
                indiceAnterior,
                diferenca
        );
    }

    private PeriodoFiltro resolverPeriodo(FiltroDashboardDto filtro) {
        LocalDateTime inicio = filtro.dataInicio();
        LocalDateTime fim = filtro.dataTermino();

        if (inicio == null || fim == null) {
            LocalDateTime hoje = LocalDateTime.now();
            inicio = hoje.minusMonths(1).withDayOfMonth(1).with(LocalTime.MIN);
            fim = inicio.with(TemporalAdjusters.lastDayOfMonth()).with(LocalTime.MAX);
        }

        return new PeriodoFiltro(inicio, fim);
    }

    private record PeriodoFiltro(LocalDateTime inicio, LocalDateTime fim) {}
}
