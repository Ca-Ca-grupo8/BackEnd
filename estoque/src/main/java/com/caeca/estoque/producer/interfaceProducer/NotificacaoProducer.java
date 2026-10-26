package com.caeca.estoque.producer.interfaceProducer;

import com.caeca.estoque.dto.brevo.NotificacaoEventoDto;

import java.util.List;
import java.util.Map;

public interface NotificacaoProducer {

    default void enviarMensagem(String email, String nome, Long templateId, Map<String, String> params) {
        enviarMensagem(email, nome, templateId, params, List.of());
    }

    void enviarMensagem(String email, String nome, Long templateId, Map<String, String> params, List<NotificacaoEventoDto.Anexo> anexos);
}
