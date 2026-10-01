package com.caeca.estoque.service.NotificacaoBrevo;

import com.caeca.estoque.dto.brevo.NotificacaoEventoDto;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class NotificacaoBrevoService {
    private final ApplicationEventPublisher applicationEventPublisher;

    public NotificacaoBrevoService(ApplicationEventPublisher applicationEventPublisher) {
        this.applicationEventPublisher = applicationEventPublisher;
    }

    public void enviar(String email, String nome, Long templateId, Map<String, String> params) {
        var evento = new NotificacaoEventoDto(email, nome, templateId, params);
        applicationEventPublisher.publishEvent(evento);
    }

    public void notificacaoBoasVindas(String email, String nome){
        enviar(email, nome, 2L, Map.of("nomeRevendedora", nome));
    }
}
