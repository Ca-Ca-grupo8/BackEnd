package com.caeca.estoque.dto.brevo;

import java.util.Map;

/**
 * Evento publicado no RabbitMQ para sincronizar um contato do Brevo.
 *
 * <p>{@code CRIAR} cria o contato na lista de revendedoras (com {@code extId} e atributos
 * completos). {@code ATUALIZAR} altera só os atributos do contato já existente, localizado
 * pelo {@code extId} (id da revendedora).
 */
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
