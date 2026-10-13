package com.caeca.estoque.service.movimentacaoRevendedora;

import com.caeca.estoque.database.model.movimentacao.Movimentacao;
import com.caeca.estoque.database.model.motivoMovimentacao.MotivoMovimentacao;
import com.caeca.estoque.database.model.tipoMovimentacao.TipoMovimentacaoEnum;

import java.util.List;
import java.util.Objects;

public final class MaletaCalculos {

    private MaletaCalculos() {
    }

    public static long quantidadePorTipo(List<Movimentacao> movimentacoes, TipoMovimentacaoEnum tipo) {
        return lista(movimentacoes).stream()
                .filter(m -> isTipo(m, tipo))
                .filter(m -> m.getQuantidade() != null)
                .mapToLong(Movimentacao::getQuantidade)
                .sum();
    }

    public static double valorPorTipo(List<Movimentacao> movimentacoes, TipoMovimentacaoEnum tipo) {
        return lista(movimentacoes).stream()
                .filter(m -> isTipo(m, tipo))
                .filter(m -> m.getQuantidade() != null)
                .mapToDouble(m -> (m.getPrecoSaida() != null ? m.getPrecoSaida() : 0.0) * m.getQuantidade())
                .sum();
    }

    public static long quantidadeVendida(List<Movimentacao> movimentacoes) {
        long saidas = quantidadePorTipo(movimentacoes, TipoMovimentacaoEnum.SAIDA);
        long reentradas = quantidadePorTipo(movimentacoes, TipoMovimentacaoEnum.REENTRADA);
        return Math.max(0L, saidas - reentradas);
    }

    public static double valorTotalVendas(List<Movimentacao> movimentacoes) {
        return valorPorTipo(movimentacoes, TipoMovimentacaoEnum.SAIDA)
                - valorPorTipo(movimentacoes, TipoMovimentacaoEnum.REENTRADA);
    }

    public static TipoMovimentacaoEnum tipoDe(Movimentacao movimentacao) {
        if (movimentacao == null) {
            return null;
        }

        MotivoMovimentacao motivo = movimentacao.getMotivoMovimentacao();
        if (motivo == null || motivo.getTipoMovimentacao() == null) {
            return null;
        }

        return motivo.getTipoMovimentacao().getNome();
    }

    public static boolean isTipo(Movimentacao movimentacao, TipoMovimentacaoEnum tipo) {
        return tipo != null && tipoDe(movimentacao) == tipo;
    }

    public static List<Movimentacao> lista(List<Movimentacao> movimentacoes) {
        if (movimentacoes == null) {
            return List.of();
        }

        return movimentacoes.stream()
                .filter(Objects::nonNull)
                .toList();
    }
}
