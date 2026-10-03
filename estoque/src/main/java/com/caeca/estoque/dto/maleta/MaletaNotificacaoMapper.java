package com.caeca.estoque.dto.maleta;

import com.caeca.estoque.database.model.movimentacaoRevendedora.MovimentacaoRevendedora;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/**
 * Formata os parâmetros de texto esperados pelos templates transacionais do Brevo.
 *
 * <p>Os templates recebem tudo como {@code String}, então a conversão acontece aqui
 * em um único lugar: o nome da revendedora, a data de vencimento legível e o mês de
 * referência do lote.
 */
public final class MaletaNotificacaoMapper {

    private static final DateTimeFormatter DATA_VENCIMENTO = DateTimeFormatter.ofPattern("dd/MM/yyyy", Locale.of("pt", "BR"));

    private static final String[] MESES = {
            "janeiro", "fevereiro", "março", "abril", "maio", "junho",
            "julho", "agosto", "setembro", "outubro", "novembro", "dezembro"
    };

    private MaletaNotificacaoMapper() {
    }

    /**
     * Devolve o nome da revendedora da maleta, ou string vazia se a revendedora
     * não estiver carregada — o template nunca deve quebrar por causa disso.
     */
    public static String nomeRevendedora(MovimentacaoRevendedora maleta) {
        if (maleta == null || maleta.getRevendedora() == null || maleta.getRevendedora().getNome() == null) {
            return "";
        }

        return maleta.getRevendedora().getNome();
    }

    /**
     * Devolve o e-mail da revendedora, ou string vazia quando ausente.
     */
    public static String emailRevendedora(MovimentacaoRevendedora maleta) {
        if (maleta == null || maleta.getRevendedora() == null || maleta.getRevendedora().getEmail() == null) {
            return "";
        }

        return maleta.getRevendedora().getEmail();
    }

    /**
     * Formata a data de vencimento em {@code dd/MM/yyyy}. Devolve string vazia
     * quando a maleta ainda não tem vencimento definido.
     */
    public static String dataVencimento(MovimentacaoRevendedora maleta) {
        if (maleta == null) {
            return "";
        }

        return dataVencimento(maleta.getDataLimite());
    }

    public static String dataVencimento(LocalDateTime dataLimite) {
        if (dataLimite == null) {
            return "";
        }

        return dataLimite.format(DATA_VENCIMENTO);
    }

    /**
     * Devolve o mês e ano de criação do lote por extenso, no formato
     * {@code "janeiro de 2025"}. É o período de referência usado no
     * fechamento do lote.
     */
    public static String mesReferencia(MovimentacaoRevendedora maleta) {
        if (maleta == null) {
            return "";
        }

        return mesReferencia(maleta.getData());
    }

    public static String mesReferencia(LocalDateTime data) {
        if (data == null) {
            return "";
        }

        return MESES[data.getMonthValue() - 1] + " de " + data.getYear();
    }
}