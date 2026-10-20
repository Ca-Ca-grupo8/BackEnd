package com.caeca.estoque.dto.brevo;

public record NotificacaoBrevoEnvioDto(
        String dataEnvio,
        String emailDestinatario,
        String assunto,
        Long templateId,
        String nomeTemplate,
        String messageId
) {}
