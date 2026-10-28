package com.caeca.estoque.config.rabbitMq;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.AmqpRejectAndDontRequeueException;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.retry.MessageRecoverer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * Chamado quando as tentativas em memória se esgotam. Devolve a mensagem para o
 * ciclo de retry (DLQ com TTL) enquanto não passar do limite de voltas; depois disso
 * a envia para a fila de parking, para análise manual.
 */
@Slf4j
@Component
public class NotificacaoMessageRecoverer implements MessageRecoverer {

    private final RabbitTemplate rabbitTemplate;
    private final int maxVoltas;

    public NotificacaoMessageRecoverer(RabbitTemplate rabbitTemplate,
                                       @Value("${app.notificacao.retry.max-voltas:3}") int maxVoltas) {
        this.rabbitTemplate = rabbitTemplate;
        this.maxVoltas = maxVoltas;
    }

    @Override
    public void recover(Message message, Throwable cause) {
        MessageProperties props = message.getMessageProperties();
        String fila = props.getConsumerQueue();
        long voltas = contarVoltas(props.getXDeathHeader(), fila);

        if (voltas < maxVoltas) {
            throw new AmqpRejectAndDontRequeueException(
                    "Notificação com falha, enviada para retry. fila=" + fila + " voltas=" + voltas, cause);
        }

        props.setHeader("x-exception-message", cause.getMessage());
        rabbitTemplate.send(RabbitMQConfig.EXCHANGE_NOTIFICACAO_DLX, routingKeyParking(fila), message);

        log.error("Notificação esgotou {} voltas e foi para a fila de parking. fila={}", voltas, fila, cause);
    }

    private long contarVoltas(List<Map<String, ?>> xDeath, String fila) {
        if (xDeath == null) {
            return 0;
        }
        return xDeath.stream()
                .filter(entrada -> fila != null && fila.equals(entrada.get("queue")))
                .filter(entrada -> "rejected".equals(entrada.get("reason")))
                .mapToLong(entrada -> ((Number) entrada.get("count")).longValue())
                .sum();
    }

    private String routingKeyParking(String fila) {
        return RabbitMQConfig.QUEUE_NOTIFICACAO_INTERNA.equals(fila)
                ? RabbitMQConfig.ROUTING_KEY_INTERNA_PARKING
                : RabbitMQConfig.ROUTING_KEY_EXTERNA_PARKING;
    }
}
