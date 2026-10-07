package com.caeca.estoque.service.revendedora;

import com.caeca.estoque.database.model.notificacaoEnvio.TipoReferencia;
import com.caeca.estoque.database.model.revendedora.Revendedora;
import com.caeca.estoque.service.NotificacaoBrevo.NotificacaoBrevoService;
import com.caeca.estoque.service.notificacaoEnvio.NotificacaoEnvioService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class RevendedoraNotificacaoService {

    private final NotificacaoBrevoService notificacaoBrevoService;
    private final NotificacaoEnvioService notificacaoEnvioService;

    @Value("${brevo.internal.recipient:eduardonuneslima07@gmail.com}")
    private String internalRecipient;

    @Value("${brevo.internal.recipient.name:Equipe Caeca}")
    private String internalRecipientName;

    public RevendedoraNotificacaoService(NotificacaoBrevoService notificacaoBrevoService,
                                        NotificacaoEnvioService notificacaoEnvioService) {
        this.notificacaoBrevoService = notificacaoBrevoService;
        this.notificacaoEnvioService = notificacaoEnvioService;
    }

    public void notificarAniversario(Revendedora revendedora) {
        if (registrarEnvio(revendedora, NotificacaoBrevoService.TEMPLATE_ANIVERSARIO, true)) {
            notificacaoBrevoService.notificacaoAniversario(revendedora.getEmail(), revendedora.getNome());
        }
    }

    public void notificarInatividade(Revendedora revendedora, long diasInatividade) {
        if (registrarEnvio(revendedora, NotificacaoBrevoService.TEMPLATE_REVENDEDORA_INATIVA, true)) {
            Map<String, String> params = new HashMap<>();
            params.put("nomeRevendedora", revendedora.getNome());
            params.put("diasInatividade", String.valueOf(diasInatividade));
            if (revendedora.getTelefone() != null) {
                params.put("telefoneRevendedora", revendedora.getTelefone());
            }
            notificacaoBrevoService.notificacaoRevendedoraInativa(internalRecipient, internalRecipientName, params);
        }
    }

    private boolean registrarEnvio(Revendedora revendedora, long templateId, boolean permiteReenvio) {
        if (revendedora == null || revendedora.getId() == null) {
            return false;
        }
        return notificacaoEnvioService.registrarEnvio(
                TipoReferencia.REVENDEDORA,
                revendedora.getId(),
                templateId,
                permiteReenvio);
    }
}
