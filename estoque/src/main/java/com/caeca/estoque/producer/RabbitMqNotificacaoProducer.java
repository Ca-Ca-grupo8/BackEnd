package com.caeca.estoque.producer;

import com.caeca.estoque.config.rabbitMq.RabbitMQConfig;
import com.caeca.estoque.dto.brevo.NotificacaoEventoDto;
import com.caeca.estoque.producer.interfaceProducer.NotificacaoProducer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import java.util.Map;

@Slf4j
@Component
public class RabbitMqNotificacaoProducer implements NotificacaoProducer {

    private final RabbitTemplate rabbitTemplate;

    public RabbitMqNotificacaoProducer(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    @Override
    public void enviarMensagem(String email, String nome, Long templateId, Map<String, String> params) {
        var evento = new NotificacaoEventoDto(email, nome, templateId, params);

        log.warn("passou no convert and send: " + evento);

        rabbitTemplate.convertAndSend(
                RabbitMQConfig.EXCHANGE_NOTIFICACAO,
                RabbitMQConfig.ROUTING_KEY_BREVO,
                evento
        );
    }
}