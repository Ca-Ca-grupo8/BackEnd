package com.caeca.estoque.producer.interfaceProducer;

import java.time.LocalDate;
import java.util.Map;

public interface ContatoProducer {

    void criarContatoRevendedora(Long idRevendedora, String nome, String email, String telefone,
                                 boolean ativo, LocalDate dataNascimento, LocalDate dataUltimaMaleta);

    void atualizarContatoRevendedora(Long idRevendedora, String emailNovo, Map<String, Object> atributos);


    void atualizarContatoMaleta(Long idRevendedora, Map<String, Object> atributos);
}
