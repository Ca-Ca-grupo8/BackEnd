package com.caeca.estoque.producer;

import com.caeca.estoque.config.rabbitMq.RabbitMQConfig;
import com.caeca.estoque.dto.brevo.BrevoContatoRequest;
import com.caeca.estoque.producer.interfaceProducer.ContatoProducer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class RabbitMqContatoProducer implements ContatoProducer {

    /** Lista do Brevo que recebe as revendedoras com maleta em consignação. */
    public static final long LISTA_MALETA_EM_CONSIGNACAO = 5L;

    private final RabbitTemplate rabbitTemplate;

    public RabbitMqContatoProducer(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    @Override
    public void sincronizarContatoMaletaEmConsignacao(Map<String, Object> atributos) {
        var request = new BrevoContatoRequest(
                (String) atributos.get("EMAIL"),
                List.of(LISTA_MALETA_EM_CONSIGNACAO),
                true,
                atributos
        );

        log.info("Publicando sincronizacao de contato. email={} listas={}", request.email(), request.listIds());

        rabbitTemplate.convertAndSend(
                RabbitMQConfig.EXCHANGE_NOTIFICACAO,
                RabbitMQConfig.ROUTING_KEY_CONTATO,
                request
        );
    }
}
