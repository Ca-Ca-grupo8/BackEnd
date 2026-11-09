package com.caeca.estoque.producer.interfaceProducer;

import java.time.LocalDate;
import java.util.Map;

public interface ContatoProducer {

    /**
     * Publica a criação do contato da revendedora na lista de revendedoras do Brevo.
     * O id da revendedora vira o {@code ext_id} do contato.
     */
    void criarContatoRevendedora(Long idRevendedora, String nome, String email, LocalDate dataNascimento);

    /**
     * Publica a atualização dos atributos da maleta no contato da revendedora (localizado por {@code ext_id}).
     *
     * @param idRevendedora id da revendedora, usado para localizar o contato
     * @param atributos     apenas os atributos que mudaram com a maleta
     */
    void atualizarContatoMaleta(Long idRevendedora, Map<String, Object> atributos);
}
