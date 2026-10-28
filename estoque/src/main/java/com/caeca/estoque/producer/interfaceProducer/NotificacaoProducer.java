package com.caeca.estoque.producer.interfaceProducer;

import com.caeca.estoque.dto.brevo.NotificacaoEventoDto;

import java.util.List;
import java.util.Map;

public interface NotificacaoProducer {

    default void enviarExterno(String email, String nome, Long templateId, Map<String, String> params) {
        publicar(email, nome, templateId, params, List.of(), PublicoNotificacao.EXTERNO);
    }

    default void enviarExternoComAnexos(String email, String nome, Long templateId, Map<String, String> params, List<NotificacaoEventoDto.Anexo> anexos) {
        publicar(email, nome, templateId, params, anexos, PublicoNotificacao.EXTERNO);
    }

    default void enviarInterno(String email, String nome, Long templateId, Map<String, String> params) {
        publicar(email, nome, templateId, params, List.of(), PublicoNotificacao.INTERNO);
    }

    void publicar(String email, String nome, Long templateId, Map<String, String> params, List<NotificacaoEventoDto.Anexo> anexos, PublicoNotificacao publico);
}
