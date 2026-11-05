package com.caeca.estoque.producer;

import com.caeca.estoque.config.rabbitMq.RabbitMQConfig;
import com.caeca.estoque.dto.brevo.BrevoContatoRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class RabbitMqContatoProducerTest {

    @Mock
    private RabbitTemplate rabbitTemplate;

    @Test
    @DisplayName("Deve montar o contato na lista 5 com o EMAIL dos atributos e publicar na routing key de contato")
    void devePublicarContatoNaListaCinco() {
        Map<String, Object> atributos = Map.of("EMAIL", "joias@exemplo.com", "NOME", "Joias");
        ArgumentCaptor<Object> captor = ArgumentCaptor.forClass(Object.class);

        new RabbitMqContatoProducer(rabbitTemplate).sincronizarContatoMaletaEmConsignacao(atributos);

        verify(rabbitTemplate).convertAndSend(
                eq(RabbitMQConfig.EXCHANGE_NOTIFICACAO),
                eq(RabbitMQConfig.ROUTING_KEY_CONTATO),
                captor.capture());

        BrevoContatoRequest request = (BrevoContatoRequest) captor.getValue();
        assertEquals("joias@exemplo.com", request.email());
        assertEquals(List.of(5L), request.listIds());
        assertEquals(true, request.updateEnabled());
        assertEquals(atributos, request.attributes());
    }
}
