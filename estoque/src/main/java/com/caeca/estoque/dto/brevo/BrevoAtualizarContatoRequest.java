package com.caeca.estoque.dto.brevo;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.Map;

/**
 * Corpo de {@code PUT /v3/contacts/{identifier}} da API do Brevo: atualiza os atributos do contato
 * e, quando informado, o email. A lista não é reenviada, então os vínculos existentes são preservados.
 */
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record BrevoAtualizarContatoRequest(String email, Map<String, Object> attributes) {}
