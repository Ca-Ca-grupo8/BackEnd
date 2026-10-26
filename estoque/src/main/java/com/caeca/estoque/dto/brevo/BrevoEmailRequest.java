package com.caeca.estoque.dto.brevo;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record BrevoEmailRequest(
        List<Destinatario> to,
        Long templateId,
        Map<String, String> params,
        List<Anexo> attachment
) {
    public record Destinatario(String email, String name) {}

    public record Anexo(String name, String content) {}
}
