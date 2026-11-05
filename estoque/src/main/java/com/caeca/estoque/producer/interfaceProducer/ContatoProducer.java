package com.caeca.estoque.producer.interfaceProducer;

import java.util.Map;

public interface ContatoProducer {

    /**
     * Publica a sincronização do contato da maleta na lista de consignação do Brevo.
     *
     * @param atributos atributos do contato; o chave {@code EMAIL} identifica o contato
     */
    void sincronizarContatoMaletaEmConsignacao(Map<String, Object> atributos);
}
