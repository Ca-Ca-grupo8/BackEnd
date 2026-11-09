package com.caeca.estoque.dto.brevo;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.Map;


@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record BrevoAtualizarContatoRequest(String email, Map<String, Object> attributes) {}
