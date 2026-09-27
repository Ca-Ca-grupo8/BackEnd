package com.caeca.estoque.dto.brevo;

import java.util.List;
import java.util.Map;

public record BrevoEmailRequest(
        List<Destinatario> to,
        Long templateId,
        Map<String, String> params
) {
    public record Destinatario(String email, String name) {}
}
