package com.caeca.estoque.dto.brevo;

import java.util.Map;

public record ContatoEventoDto(
        TipoOperacao tipo,
        Long idRevendedora,
        String email,
        Map<String, Object> atributos
) {

    public enum TipoOperacao {
        CRIAR,
        ATUALIZAR
    }
}
