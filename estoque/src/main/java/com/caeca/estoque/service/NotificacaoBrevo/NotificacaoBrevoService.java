package com.caeca.estoque.service.NotificacaoBrevo;

import com.caeca.estoque.database.model.movimentacaoRevendedora.MovimentacaoRevendedora;
import com.caeca.estoque.dto.brevo.NotificacaoEventoDto;
import com.caeca.estoque.dto.maleta.MaletaNotificacaoMapper;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * Publica os eventos de notificação consumidos pelo {@code CrmNotificadorConsumer},
 * que por sua vez chama o Brevo.
 *
 * <p>Cada método corresponde a uma regra de negócio de evento da maleta e monta
 * o template com os parâmetros que aquele template espera. O envio é assíncrono:
 * publicar o evento não espera resposta do Brevo.
 */
@Service
public class NotificacaoBrevoService {

    public static final long TEMPLATE_BOAS_VINDAS = 2L;
    public static final long TEMPLATE_LOTE_EM_CONSIGNACAO = 3L;
    public static final long TEMPLATE_ALERTA_VENCIMENTO = 4L;
    public static final long TEMPLATE_COBRANCA_ATRASO = 5L;
    public static final long TEMPLATE_FECHAMENTO_LOTE = 6L;

    private final ApplicationEventPublisher applicationEventPublisher;

    public NotificacaoBrevoService(ApplicationEventPublisher applicationEventPublisher) {
        this.applicationEventPublisher = applicationEventPublisher;
    }

    public void notificacaoBoasVindas(String email, String nome) {
        enviar(email, nome, TEMPLATE_BOAS_VINDAS, Map.of("nomeRevendedora", nome));
    }

    public void notificacaoLoteEmConsignacao(MovimentacaoRevendedora maleta) {
        enviarParaMaleta(maleta, TEMPLATE_LOTE_EM_CONSIGNACAO, Map.of(
                "nomeRevendedora", MaletaNotificacaoMapper.nomeRevendedora(maleta),
                "dataVencimento", MaletaNotificacaoMapper.dataVencimento(maleta)
        ));
    }

    public void notificacaoAlertaVencimento(MovimentacaoRevendedora maleta, long diasRestantes) {
        enviarParaMaleta(maleta, TEMPLATE_ALERTA_VENCIMENTO, Map.of(
                "nomeRevendedora", MaletaNotificacaoMapper.nomeRevendedora(maleta),
                "diasRestantes", String.valueOf(diasRestantes),
                "dataVencimento", MaletaNotificacaoMapper.dataVencimento(maleta)
        ));
    }

    public void notificacaoCobrancaAtraso(MovimentacaoRevendedora maleta) {
        enviarParaMaleta(maleta, TEMPLATE_COBRANCA_ATRASO, Map.of(
                "nomeRevendedora", MaletaNotificacaoMapper.nomeRevendedora(maleta),
                "dataVencimento", MaletaNotificacaoMapper.dataVencimento(maleta)
        ));
    }

    public void notificacaoFechamentoLote(MovimentacaoRevendedora maleta) {
        enviarParaMaleta(maleta, TEMPLATE_FECHAMENTO_LOTE, Map.of(
                "nomeRevendedora", MaletaNotificacaoMapper.nomeRevendedora(maleta),
                "mes", MaletaNotificacaoMapper.mesReferencia(maleta)
        ));
    }

    private void enviar(String email, String nome, Long templateId, Map<String, String> params) {
        var evento = new NotificacaoEventoDto(email, nome, templateId, params);
        applicationEventPublisher.publishEvent(evento);
    }

    private void    enviarParaMaleta(MovimentacaoRevendedora maleta, long templateId, Map<String, String> params) {
        enviar(
                MaletaNotificacaoMapper.emailRevendedora(maleta),
                MaletaNotificacaoMapper.nomeRevendedora(maleta),
                templateId,
                params
        );
    }
}