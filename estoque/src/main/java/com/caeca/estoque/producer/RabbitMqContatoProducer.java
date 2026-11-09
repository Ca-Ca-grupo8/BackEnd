package com.caeca.estoque.producer;

import com.caeca.estoque.config.rabbitMq.RabbitMQConfig;
import com.caeca.estoque.dto.brevo.ContatoEventoDto;
import com.caeca.estoque.dto.brevo.ContatoEventoDto.TipoOperacao;
import com.caeca.estoque.producer.interfaceProducer.ContatoProducer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
public class RabbitMqContatoProducer implements ContatoProducer {

    private static final DateTimeFormatter DATA_ATRIBUTO_BREVO = DateTimeFormatter.ISO_LOCAL_DATE;

    private final RabbitTemplate rabbitTemplate;

    public RabbitMqContatoProducer(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    @Override
    public void criarContatoRevendedora(Long idRevendedora, String nome, String email, LocalDate dataNascimento) {
        var atributos = new HashMap<String, Object>();
        atributos.put("ID_REVENDEDORA", idRevendedora);
        atributos.put("NOME", nome);
        atributos.put("EMAIL", email);
        if (dataNascimento != null) {
            atributos.put("DATA_NASCIMENTO", dataNascimento.format(DATA_ATRIBUTO_BREVO));
        }

        publicar(new ContatoEventoDto(TipoOperacao.CRIAR, idRevendedora, email, atributos));
    }

    @Override
    public void atualizarContatoMaleta(Long idRevendedora, Map<String, Object> atributos) {
        publicar(new ContatoEventoDto(TipoOperacao.ATUALIZAR, idRevendedora, null, atributos));
    }

    private void publicar(ContatoEventoDto evento) {
        log.info("Publicando sincronizacao de contato. tipo={} idRevendedora={}", evento.tipo(), evento.idRevendedora());

        rabbitTemplate.convertAndSend(
                RabbitMQConfig.EXCHANGE_NOTIFICACAO,
                RabbitMQConfig.ROUTING_KEY_CONTATO,
                evento
        );
    }
}
