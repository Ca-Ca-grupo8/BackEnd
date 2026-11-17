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
 * <p>Falhas no envio são registradas no log e relançadas. A exceção é o que faz o
 * RabbitMQ aplicar o retry (filas {@code .retry}) e, esgotadas as tentativas, mandar a
 * mensagem para o parking, via {@code NotificacaoMessageRecoverer}. Engolir a exceção
 * aqui faria uma notificação perdida nunca ser reenviada.
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

    @RabbitListener(queues = {RabbitMQConfig.QUEUE_NOTIFICACAO_EXTERNA, RabbitMQConfig.QUEUE_NOTIFICACAO_INTERNA})
    public void processarNotificacao(NotificacaoEventoDto evento) {

        log.warn("chegou no consumer");

        var destinatario = new BrevoEmailRequest.Destinatario(evento.emailDestino(), evento.nomeRevendedora());
        var anexos = evento.anexos() == null ? null : evento.anexos().stream()
                .map(anexo -> new BrevoEmailRequest.Anexo(anexo.nome(), anexo.conteudoBase64()))
                .toList();
        var requestBrevo = new BrevoEmailRequest(
                List.of(destinatario),
                evento.templateId(),
                evento.parametros(),
                anexos
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