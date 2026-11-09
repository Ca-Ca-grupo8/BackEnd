package com.caeca.estoque.consumer;

import com.caeca.estoque.client.BrevoClient;
import com.caeca.estoque.config.rabbitMq.RabbitMQConfig;
import com.caeca.estoque.dto.brevo.BrevoAtualizarContatoRequest;
import com.caeca.estoque.dto.brevo.BrevoContatoRequest;
import com.caeca.estoque.dto.brevo.ContatoEventoDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;

/** Lista do Brevo que recebe as revendedoras cadastradas. */
@Slf4j
@Component
public class CrmContatoConsumer {

    static final long LISTA_REVENDEDORAS = 5L;
    static final String IDENTIFICADOR_EXT_ID = "ext_id";

    private final BrevoClient brevoClient;

    @Value("${brevo.api.key}")
    private String apiKey;

    public CrmContatoConsumer(BrevoClient brevoClient) {
        this.brevoClient = brevoClient;
    }

    @RabbitListener(queues = RabbitMQConfig.QUEUE_CONTATO)
    public void processarContato(ContatoEventoDto evento) {
        String extId = String.valueOf(evento.idRevendedora());
        try {
            switch (evento.tipo()) {
                case CRIAR -> brevoClient.criarOuAtualizarContato(apiKey, new BrevoContatoRequest(
                        evento.email(), List.of(LISTA_REVENDEDORAS), true, extId, evento.atributos()));
                case ATUALIZAR -> brevoClient.atualizarContato(apiKey, extId, IDENTIFICADOR_EXT_ID,
                        new BrevoAtualizarContatoRequest(evento.atributos()));
            }

            log.info("Contato sincronizado no Brevo. tipo={} extId={}", evento.tipo(), extId);
        } catch (Exception e) {
            log.error("Falha ao sincronizar contato no Brevo. tipo={} extId={}", evento.tipo(), extId, e);

            throw e;
        }
    }
}
