package com.caeca.estoque.dto.notificacaoEnvio;

import com.caeca.estoque.database.model.notificacaoEnvio.TipoReferencia;

import java.time.LocalDateTime;

public record NotificacaoEnvioDto(
        Long id,
        LocalDateTime dataEnvio,
        TipoReferencia tipoReferencia,
        Long referenciaId,
        Long templateId,
        String nomeTemplate,
        boolean permiteReenvio
) {}
