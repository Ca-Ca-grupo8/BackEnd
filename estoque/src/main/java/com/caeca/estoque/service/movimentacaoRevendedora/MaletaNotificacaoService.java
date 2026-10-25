package com.caeca.estoque.service.movimentacaoRevendedora;

import com.caeca.estoque.database.model.movimentacaoRevendedora.MovimentacaoRevendedora;
import com.caeca.estoque.database.model.notificacaoEnvio.TipoReferencia;
import com.caeca.estoque.service.NotificacaoBrevo.NotificacaoBrevoService;
import com.caeca.estoque.service.notificacaoEnvio.NotificacaoEnvioService;
import org.springframework.stereotype.Service;

@Service
public class MaletaNotificacaoService {


    private final NotificacaoBrevoService notificacaoBrevoService;
    private final NotificacaoEnvioService notificacaoEnvioService;

    public MaletaNotificacaoService(NotificacaoBrevoService notificacaoBrevoService, NotificacaoEnvioService notificacaoEnvioService) {
        this.notificacaoBrevoService = notificacaoBrevoService;
        this.notificacaoEnvioService = notificacaoEnvioService;
    }

    public void notificarLoteEmConsignacao(MovimentacaoRevendedora maleta, byte[] comprovante) {
        if (registrarEnvio(maleta, NotificacaoBrevoService.TEMPLATE_LOTE_EM_CONSIGNACAO, false)) {
            notificacaoBrevoService.notificacaoLoteEmConsignacao(maleta, comprovante);
        }
    }

    public void notificarAlertaVencimento(MovimentacaoRevendedora maleta, long diasRestantes) {
        if (registrarEnvio(maleta, NotificacaoBrevoService.TEMPLATE_ALERTA_VENCIMENTO, false)) {
            notificacaoBrevoService.notificacaoAlertaVencimento(maleta, diasRestantes);
        }
    }

    public void notificarCobrancaAtraso(MovimentacaoRevendedora maleta) {
        if (registrarEnvio(maleta, NotificacaoBrevoService.TEMPLATE_COBRANCA_ATRASO, false)) {
            notificacaoBrevoService.notificacaoCobrancaAtraso(maleta);
        }
    }

    public void notificarFechamentoLote(MovimentacaoRevendedora maleta, java.util.Map<String, String> params, boolean metaAtingida) {
        if (registrarEnvio(maleta, NotificacaoBrevoService.TEMPLATE_META_ATINGIDA, true)) {
            notificacaoBrevoService.notificacaoMetaAtingida(maleta, params, metaAtingida);
        }
    }

    private boolean registrarEnvio(MovimentacaoRevendedora maleta, long templateId, boolean permiteReenvio) {
        Long maletaId = maleta.getIdMovimentacaoRevendedora();
        return notificacaoEnvioService.registrarEnvio(
                TipoReferencia.MALETA,
                maletaId,
                templateId,
                permiteReenvio);
    }
}