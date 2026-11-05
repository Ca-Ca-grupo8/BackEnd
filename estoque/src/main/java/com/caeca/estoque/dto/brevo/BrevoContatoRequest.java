package com.caeca.estoque.dto.brevo;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;
import java.util.Map;

/**
 * Corpo de {@code POST /v3/contacts} da API do Brevo: cria o contato ou, se já existir
 * ({@code updateEnabled}), atualiza seus atributos e o adiciona às listas informadas.
 */
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record BrevoContatoRequest(
        String email,
        List<Long> listIds,
        boolean updateEnabled,
        Map<String, Object> attributes
) {}
