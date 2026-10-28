package com.caeca.estoque.config.rabbitMq;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.core.Queue;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("RabbitMQConfig")
class RabbitMQConfigTest {

    private final RabbitMQConfig config = new RabbitMQConfig();

    @Test
    @DisplayName("fila externa deve encaminhar falhas para o retry via DLX")
    void filaExternaDeveTerDlx() {
        Queue queue = config.queueExterna();

        assertEquals(RabbitMQConfig.EXCHANGE_NOTIFICACAO_DLX, queue.getArguments().get("x-dead-letter-exchange"));
        assertEquals(RabbitMQConfig.ROUTING_KEY_EXTERNA_RETRY, queue.getArguments().get("x-dead-letter-routing-key"));
    }

    @Test
    @DisplayName("fila interna deve encaminhar falhas para o retry via DLX")
    void filaInternaDeveTerDlx() {
        Queue queue = config.queueInterna();

        assertEquals(RabbitMQConfig.EXCHANGE_NOTIFICACAO_DLX, queue.getArguments().get("x-dead-letter-exchange"));
        assertEquals(RabbitMQConfig.ROUTING_KEY_INTERNA_RETRY, queue.getArguments().get("x-dead-letter-routing-key"));
    }

    @Test
    @DisplayName("fila de retry deve expirar com o TTL configurado e voltar para a exchange principal")
    void filaRetryDeveExpirarEVoltar() {
        Queue queue = config.queueExternaRetry(300000);

        assertEquals(300000, queue.getArguments().get("x-message-ttl"));
        assertEquals(RabbitMQConfig.EXCHANGE_NOTIFICACAO, queue.getArguments().get("x-dead-letter-exchange"));
        assertEquals(RabbitMQConfig.ROUTING_KEY_EXTERNA, queue.getArguments().get("x-dead-letter-routing-key"));
    }

    @Test
    @DisplayName("fila de parking deve ser durável e sem TTL")
    void filaParkingSemTtl() {
        Queue queue = config.queueInternaParking();

        assertEquals(true, queue.isDurable());
        assertEquals(null, queue.getArguments().get("x-message-ttl"));
    }
}
