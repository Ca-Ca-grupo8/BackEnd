package com.caeca.estoque.consumer;

import com.caeca.estoque.client.BrevoClient;
import com.caeca.estoque.config.rabbitMq.RabbitMQConfig;
import com.caeca.estoque.dto.brevo.BrevoEmailRequest;
import com.caeca.estoque.dto.brevo.NotificacaoEventoDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Consome os eventos de notificação e dispara o envio no Brevo.
 *
 * <p>O envio é fire-and-forget: a notificação é um efeito colateral do fluxo de
 * negócio e nunca pode derrubar o request que a originou. Por isso a chamada ao
 * Feign fica dentro de um try/catch que registra a falha no log em vez de propagar
 * a exceção.
 */
@Slf4j
@Component
public class CrmNotificadorConsumer {

    private static final Logger LOG = Logger.getLogger(CrmNotificadorConsumer.class.getName());

    private final BrevoClient brevoClient;

    @Value("${brevo.api.key}")
    private String apiKey;

    public CrmNotificadorConsumer(BrevoClient brevoClient) {
        this.brevoClient = brevoClient;
    }

    @RabbitListener(queues = RabbitMQConfig.QUEUE_NOTIFICACAO_BREVO)
    public void processarNotificacao(NotificacaoEventoDto evento) {

        log.warn("chegou no consumer");

        var destinatario = new BrevoEmailRequest.Destinatario(evento.emailDestino(), evento.nomeRevendedora());
        var requestBrevo = new BrevoEmailRequest(
                List.of(destinatario),
                evento.templateId(),
                evento.parametros()
        );

        try {
            brevoClient.enviarEmailTransacional(apiKey, requestBrevo);

            LOG.info("Notificacao enviada ao Brevo. template=" + evento.templateId()
                    + " revendedora=" + evento.nomeRevendedora()
                    + " status=enviado");
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "Falha ao enviar notificacao ao Brevo. template=" + evento.templateId()
                    + " revendedora=" + evento.nomeRevendedora()
                    + " status=falha", e);

            throw e;
        }
    }
}