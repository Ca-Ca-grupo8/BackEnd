package com.caeca.estoque.config.rabbitMq;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.AmqpRejectAndDontRequeueException;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
@DisplayName("NotificacaoMessageRecoverer")
class NotificacaoMessageRecovererTest {

    private static final int MAX_VOLTAS = 3;

    @Mock
    private RabbitTemplate rabbitTemplate;

    private NotificacaoMessageRecoverer recoverer() {
        return new NotificacaoMessageRecoverer(rabbitTemplate, MAX_VOLTAS);
    }

    private Message mensagem(String fila, long voltas) {
        var props = new MessageProperties();
        props.setConsumerQueue(fila);
        if (voltas > 0) {
            props.setHeader("x-death", List.<Map<String, ?>>of(
                    Map.of("queue", fila, "reason", "rejected", "count", voltas)));
        }
        return new Message(new byte[0], props);
    }

    @Test
    @DisplayName("deve mandar para o retry quando ainda não atingiu o limite de voltas")
    void deveMandarParaRetryAteLimite() {
        var msg = mensagem(RabbitMQConfig.QUEUE_NOTIFICACAO_EXTERNA, 2);

        assertThrows(AmqpRejectAndDontRequeueException.class,
                () -> recoverer().recover(msg, new RuntimeException("Brevo fora do ar")));

        verify(rabbitTemplate, never()).send(any(String.class), any(String.class), any(Message.class));
    }

    @Test
    @DisplayName("deve mandar para retry na primeira falha, sem header x-death")
    void deveMandarParaRetryNaPrimeiraFalha() {
        var msg = mensagem(RabbitMQConfig.QUEUE_NOTIFICACAO_EXTERNA, 0);

        assertThrows(AmqpRejectAndDontRequeueException.class,
                () -> recoverer().recover(msg, new RuntimeException("Brevo fora do ar")));
    }

    @Test
    @DisplayName("deve enviar para o parking da fila externa ao atingir o limite de voltas")
    void deveEnviarParaParkingExterno() {
        var msg = mensagem(RabbitMQConfig.QUEUE_NOTIFICACAO_EXTERNA, MAX_VOLTAS);

        assertDoesNotThrow(() -> recoverer().recover(msg, new RuntimeException("Brevo fora do ar")));

        verify(rabbitTemplate).send(
                RabbitMQConfig.EXCHANGE_NOTIFICACAO_DLX,
                RabbitMQConfig.ROUTING_KEY_EXTERNA_PARKING,
                msg);
    }

    @Test
    @DisplayName("deve enviar para o parking da fila interna ao atingir o limite de voltas")
    void deveEnviarParaParkingInterno() {
        var msg = mensagem(RabbitMQConfig.QUEUE_NOTIFICACAO_INTERNA, MAX_VOLTAS);

        assertDoesNotThrow(() -> recoverer().recover(msg, new RuntimeException("Brevo fora do ar")));

        verify(rabbitTemplate).send(
                eq(RabbitMQConfig.EXCHANGE_NOTIFICACAO_DLX),
                eq(RabbitMQConfig.ROUTING_KEY_INTERNA_PARKING),
                any(Message.class));
    }
}
