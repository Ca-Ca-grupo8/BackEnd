package com.caeca.estoque.dto.brevo;

import java.util.List;
import java.util.Map;

public record NotificacaoEventoDto(
        String emailDestino,
        String nomeRevendedora,
        Long templateId,
        Map<String, String> parametros,
        List<Anexo> anexos

) {

    public NotificacaoEventoDto(String emailDestino, String nomeRevendedora, Long templateId, Map<String, String> parametros) {
        this(emailDestino, nomeRevendedora, templateId, parametros, null);
    }

    public record Anexo(String nome, String conteudoBase64) {}
}
