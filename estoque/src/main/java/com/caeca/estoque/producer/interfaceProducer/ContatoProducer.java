package com.caeca.estoque.producer.interfaceProducer;

import java.time.LocalDate;
import java.util.Map;

public interface ContatoProducer {

    /**
     * Publica a criação (ou atualização completa, por upsert) do contato da revendedora na lista
     * de revendedoras do Brevo. O id da revendedora vira o {@code ext_id} do contato.
     *
     * @param dataUltimaMaleta data usada pela automação de inatividade; quando não há maleta,
     *                         a data de cadastro
     */
    void criarContatoRevendedora(Long idRevendedora, String nome, String email, String telefone,
                                 boolean ativo, LocalDate dataNascimento, LocalDate dataUltimaMaleta);

    /**
     * Publica a atualização dos dados da revendedora no contato já existente (localizado por {@code ext_id}).
     *
     * @param emailNovo novo email do contato, ou {@code null} para manter o atual
     * @param atributos atributos a atualizar
     */
    void atualizarContatoRevendedora(Long idRevendedora, String emailNovo, Map<String, Object> atributos);

    /**
     * Publica a atualização dos atributos da maleta no contato da revendedora (localizado por {@code ext_id}).
     *
     * @param idRevendedora id da revendedora, usado para localizar o contato
     * @param atributos     apenas os atributos que mudaram com a maleta
     */
    void atualizarContatoMaleta(Long idRevendedora, Map<String, Object> atributos);
}
