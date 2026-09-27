package com.caeca.estoque.consumer;

import com.caeca.estoque.client.BrevoClient;
import com.caeca.estoque.dto.brevo.BrevoEmailRequest;
import com.caeca.estoque.dto.brevo.NotificacaoEventoDto;
import lombok.Value;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CrmNotificadorConsumer {
    private final BrevoClient brevoClient;

    @Value("${brevo.api.key}")
    private String apiKey;

    public CrmNotificadorConsumer(BrevoClient brevoClient) {
        this.brevoClient = brevoClient;
    }

    @Async
    @EventListener
    public void processarNotificacao(NotificacaoEventoDto evento) {

        var destinatario = new BrevoEmailRequest.Destinatario(evento.emailDestino(), evento.nomeRevendedora());
        var requestBrevo = new BrevoEmailRequest(
                List.of(destinatario),
                evento.templateId(),
                evento.parametros()
        );

        brevoClient.enviarEmailTransacional(apiKey, requestBrevo);
    }
}
