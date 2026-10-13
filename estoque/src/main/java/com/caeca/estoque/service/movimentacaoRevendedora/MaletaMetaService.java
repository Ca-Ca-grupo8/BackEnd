package com.caeca.estoque.service.movimentacaoRevendedora;

import com.caeca.estoque.database.model.movimentacao.Movimentacao;
import com.caeca.estoque.database.model.movimentacaoRevendedora.MovimentacaoRevendedora;
import com.caeca.estoque.database.model.tipoMovimentacao.TipoMovimentacaoEnum;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
        return MaletaCalculos.quantidadeVendida(movimentacoesDaMaleta(maleta));
    }

    private long calcularQuantidadeTotal(MovimentacaoRevendedora maleta) {
        return MaletaCalculos.quantidadePorTipo(movimentacoesDaMaleta(maleta), TipoMovimentacaoEnum.SAIDA);
    }

    private double calcularValorTotalVendas(MovimentacaoRevendedora maleta) {
        return MaletaCalculos.valorTotalVendas(movimentacoesDaMaleta(maleta));
    }

    private List<Movimentacao> movimentacoesDaMaleta(MovimentacaoRevendedora maleta) {
        if (maleta == null) {
            return List.of();
        }
        return MaletaCalculos.lista(maleta.getMovimentacoes());
    }

    private String formatarMoeda(double valor) {
        return BigDecimal.valueOf(valor)
                .setScale(2, RoundingMode.HALF_UP)
                .toString();
    }
}
