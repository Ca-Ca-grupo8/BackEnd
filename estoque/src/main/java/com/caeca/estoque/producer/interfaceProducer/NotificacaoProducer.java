package com.caeca.estoque.producer.interfaceProducer;

import java.util.Map;

public interface NotificacaoProducer {
    void enviarMensagem(String email, String nome, Long templateId, Map<String, String> params);
}