package com.caeca.estoque.dto.brevo;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

/**
 * Corpo de {@code POST /v3/contacts} da API do Brevo: cria o contato ou, se já existir
 * ({@code updateEnabled}), atualiza seus atributos e o adiciona às listas informadas.
 *
 * <p>{@code extId} é o id da revendedora no sistema, usado depois para localizar o contato
 * nas atualizações de maleta.
 */
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record BrevoContatoRequest(
        String email,
        List<Long> listIds,
        boolean updateEnabled,
        @JsonProperty("ext_id") String extId,
        Map<String, Object> attributes
) {}
