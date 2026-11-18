package com.caeca.estoque.dto.brevo;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record BrevoContatoRequest(
        String email,
        List<Long> listIds,
        boolean updateEnabled,
        @JsonProperty("ext_id") String extId,
        Map<String, Object> attributes
) {}
