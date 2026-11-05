package com.caeca.estoque.consumer;

import com.caeca.estoque.client.BrevoClient;
import com.caeca.estoque.config.rabbitMq.RabbitMQConfig;
import com.caeca.estoque.dto.brevo.BrevoContatoRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class CrmContatoConsumer {

    private final BrevoClient brevoClient;

    @Value("${brevo.api.key}")
    private String apiKey;

    public CrmContatoConsumer(BrevoClient brevoClient) {
        this.brevoClient = brevoClient;
    }

    @RabbitListener(queues = RabbitMQConfig.QUEUE_CONTATO)
    public void processarContato(BrevoContatoRequest request) {
        try {
            brevoClient.criarOuAtualizarContato(apiKey, request);

            log.info("Contato sincronizado no Brevo. email={} listas={}", request.email(), request.listIds());
        } catch (Exception e) {
            log.error("Falha ao sincronizar contato no Brevo. email={} listas={}", request.email(), request.listIds(), e);

            throw e;
        }
    }
}
