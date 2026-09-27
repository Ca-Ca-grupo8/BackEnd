package com.caeca.estoque.dto.brevo;

import java.util.Map;

public record NotificacaoEventoDto(
        String emailDestino,
        String nomeRevendedora,
        Long templateId,
        Map<String, String> parametros

) {
}
