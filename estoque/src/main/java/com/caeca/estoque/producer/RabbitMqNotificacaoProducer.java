package com.caeca.estoque.producer;

import com.caeca.estoque.config.rabbitMq.RabbitMQConfig;
import com.caeca.estoque.dto.brevo.NotificacaoEventoDto;
import com.caeca.estoque.producer.interfaceProducer.NotificacaoProducer;
import com.caeca.estoque.producer.interfaceProducer.PublicoNotificacao;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class RabbitMqNotificacaoProducer implements NotificacaoProducer {

    private final RabbitTemplate rabbitTemplate;

    public RabbitMqNotificacaoProducer(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    @Override
    public void publicar(String email, String nome, Long templateId, Map<String, String> params, List<NotificacaoEventoDto.Anexo> anexos, PublicoNotificacao publico) {
        var evento = new NotificacaoEventoDto(email, nome, templateId, params, anexos);

        log.warn("passou no convert and send: template=" + templateId + " publico=" + publico + " anexos=" + anexos.size());

        var routingKey = publico == PublicoNotificacao.INTERNO
                ? RabbitMQConfig.ROUTING_KEY_INTERNA
                : RabbitMQConfig.ROUTING_KEY_EXTERNA;

        rabbitTemplate.convertAndSend(
                RabbitMQConfig.EXCHANGE_NOTIFICACAO,
                routingKey,
                evento
        );
    }
}
