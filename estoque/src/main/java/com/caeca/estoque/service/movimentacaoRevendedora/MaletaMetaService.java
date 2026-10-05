package com.caeca.estoque.service.movimentacaoRevendedora;

import com.caeca.estoque.database.model.movimentacao.Movimentacao;
import com.caeca.estoque.database.model.movimentacaoRevendedora.MovimentacaoRevendedora;
import com.caeca.estoque.database.model.motivoMovimentacao.MotivoMovimentacao;
import com.caeca.estoque.database.model.tipoMovimentacao.TipoMovimentacaoEnum;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import com.caeca.estoque.dto.maleta.MaletaNotificacaoMapper;

@Component
public class MaletaMetaService {

    private static final double META_PERCENTUAL_PADRAO = 0.90;

    public boolean bateuMeta(MovimentacaoRevendedora maleta) {
        double percentual = calcularPercentualVendido(maleta);
        return percentual >= META_PERCENTUAL_PADRAO;
    }

    public Map<String, String> montarParametrosMeta(MovimentacaoRevendedora maleta) {
        Map<String, String> params = new HashMap<>();
        params.put("nomeRevendedora", maleta.getRevendedora() != null ? maleta.getRevendedora().getNome() : "");
        params.put("mes", MaletaNotificacaoMapper.mesReferencia(maleta));
        params.put("quantidadeVendida", String.valueOf(calcularQuantidadeVendida(maleta)));
        params.put("valorTotalVendas", formatarMoeda(calcularValorTotalVendas(maleta)));
        return params;
    }

    private double calcularPercentualVendido(MovimentacaoRevendedora maleta) {
        long total = calcularQuantidadeTotal(maleta);
        if (total <= 0) {
            return 0.0;
        }
        long vendidas = calcularQuantidadeVendida(maleta);
        return (double) vendidas / (double) total;
    }

    private long calcularQuantidadeVendida(MovimentacaoRevendedora maleta) {
        long saidas = calcularQuantidadePorTipo(maleta, TipoMovimentacaoEnum.SAIDA);
        long reentradas = calcularQuantidadePorTipo(maleta, TipoMovimentacaoEnum.REENTRADA);
        return Math.max(0L, saidas - reentradas);
    }

    private long calcularQuantidadeTotal(MovimentacaoRevendedora maleta) {
        return calcularQuantidadePorTipo(maleta, TipoMovimentacaoEnum.SAIDA);
    }

    private long calcularQuantidadePorTipo(MovimentacaoRevendedora maleta, TipoMovimentacaoEnum tipo) {
        return movimentacoesDaMaleta(maleta).stream()
                .filter(m -> isTipo(m, tipo))
                .filter(m -> m.getQuantidade() != null)
                .mapToLong(Movimentacao::getQuantidade)
                .sum();
    }

    private double calcularValorTotalVendas(MovimentacaoRevendedora maleta) {
        double saidas = calcularValorPorTipo(maleta, TipoMovimentacaoEnum.SAIDA);
        double reentradas = calcularValorPorTipo(maleta, TipoMovimentacaoEnum.REENTRADA);
        return saidas - reentradas;
    }

    private double calcularValorPorTipo(MovimentacaoRevendedora maleta, TipoMovimentacaoEnum tipo) {
        return movimentacoesDaMaleta(maleta).stream()
                .filter(m -> isTipo(m, tipo))
                .filter(m -> m.getQuantidade() != null)
                .mapToDouble(m -> (m.getPrecoSaida() != null ? m.getPrecoSaida() : 0.0) * m.getQuantidade())
                .sum();
    }

    private List<Movimentacao> movimentacoesDaMaleta(MovimentacaoRevendedora maleta) {
        if (maleta == null || maleta.getMovimentacoes() == null) {
            return List.of();
        }
        return maleta.getMovimentacoes().stream()
                .filter(Objects::nonNull)
                .toList();
    }

    private boolean isTipo(Movimentacao movimentacao, TipoMovimentacaoEnum tipo) {
        MotivoMovimentacao motivo = movimentacao.getMotivoMovimentacao();
        if (motivo == null || motivo.getTipoMovimentacao() == null) {
            return false;
        }
        return motivo.getTipoMovimentacao().getNome() == tipo;
    }

    private String formatarMoeda(double valor) {
        return BigDecimal.valueOf(valor)
                .setScale(2, RoundingMode.HALF_UP)
                .toString();
    }
}
