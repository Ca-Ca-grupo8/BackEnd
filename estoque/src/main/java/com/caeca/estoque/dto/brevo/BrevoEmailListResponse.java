package com.caeca.estoque.dto.brevo;

import java.util.List;

/**
 * Resposta de {@code GET /v3/smtp/emails} da Brevo. Só os campos usados pela aplicação.
 */
public record BrevoEmailListResponse(
        Long count,
        List<EmailTransacional> transactionalEmails
) {
    public record EmailTransacional(
            String date,
            String email,
            String messageId,
            String subject,
            Long templateId
    ) {}
}
