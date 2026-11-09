package com.caeca.estoque.dto.brevo;

import java.util.Map;

/**
 * Corpo de {@code PUT /v3/contacts/{identifier}} da API do Brevo: atualiza apenas os atributos
 * do contato. A lista não é reenviada, então os vínculos existentes são preservados.
 */
public record BrevoAtualizarContatoRequest(Map<String, Object> attributes) {}
