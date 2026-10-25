package com.caeca.estoque.service.NotificacaoBrevo;

import com.caeca.estoque.database.model.movimentacaoRevendedora.MovimentacaoRevendedora;
import com.caeca.estoque.dto.brevo.NotificacaoEventoDto;
import com.caeca.estoque.dto.maleta.MaletaNotificacaoMapper;
import com.caeca.estoque.producer.interfaceProducer.NotificacaoProducer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Base64;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class NotificacaoBrevoService {

    public static final long TEMPLATE_BOAS_VINDAS = 2L;
    public static final long TEMPLATE_LOTE_EM_CONSIGNACAO = 3L;
    public static final long TEMPLATE_ALERTA_VENCIMENTO = 4L;
    public static final long TEMPLATE_COBRANCA_ATRASO = 5L;
    public static final long TEMPLATE_FECHAMENTO_LOTE = 6L;
    public static final long TEMPLATE_ANIVERSARIO = 7L;
    public static final long TEMPLATE_META_ATINGIDA = 8L;
    public static final long TEMPLATE_ESTOQUE_BAIXO = 9L;
    public static final long TEMPLATE_PECA_PARADA = 10L;
    public static final long TEMPLATE_REVENDEDORA_INATIVA = 11L;

    private final NotificacaoProducer notificacaoProducer;

    public NotificacaoBrevoService(NotificacaoProducer notificacaoProducer) {
        this.notificacaoProducer = notificacaoProducer;
    }

    public void notificacaoBoasVindas(String email, String nome) {
        notificacaoProducer.enviarMensagem(email, nome, TEMPLATE_BOAS_VINDAS, Map.of("nomeRevendedora", nome));
    }

    public void notificacaoLoteEmConsignacao(MovimentacaoRevendedora maleta, byte[] comprovante) {
        log.warn("passou no envio");
        enviarParaMaleta(maleta, TEMPLATE_LOTE_EM_CONSIGNACAO, Map.of(
                "nomeRevendedora", MaletaNotificacaoMapper.nomeRevendedora(maleta),
                "dataVencimento", MaletaNotificacaoMapper.dataVencimento(maleta)
        ), comprovante == null ? List.of() : List.of(new NotificacaoEventoDto.Anexo(
                "comprovante-maleta-" + maleta.getIdMovimentacaoRevendedora() + ".pdf",
                Base64.getEncoder().encodeToString(comprovante)
        )));
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

    private void enviarParaMaleta(MovimentacaoRevendedora maleta, long templateId, Map<String, String> params) {
        notificacaoProducer.enviarMensagem(
                MaletaNotificacaoMapper.emailRevendedora(maleta),
                MaletaNotificacaoMapper.nomeRevendedora(maleta),
                templateId,
                params
        );
    }

    private void enviarParaMaleta(MovimentacaoRevendedora maleta, long templateId, Map<String, String> params, List<NotificacaoEventoDto.Anexo> anexos) {
        notificacaoProducer.enviarMensagem(
                MaletaNotificacaoMapper.emailRevendedora(maleta),
                MaletaNotificacaoMapper.nomeRevendedora(maleta),
                templateId,
                params,
                anexos
        );
    }

    public void notificacaoAniversario(String email, String nome) {
        notificacaoProducer.enviarMensagem(email, nome, TEMPLATE_ANIVERSARIO, Map.of("nomeRevendedora", nome));
    }

    public void notificacaoMetaAtingida(MovimentacaoRevendedora maleta, Map<String, String> params, boolean metaAtingida) {
        enviarParaMaleta(maleta, metaAtingida ? TEMPLATE_META_ATINGIDA : TEMPLATE_FECHAMENTO_LOTE, params);
    }

    public void notificacaoEstoqueBaixo(String email, String nome, Map<String, String> params) {
        notificacaoProducer.enviarMensagem(email, nome, TEMPLATE_ESTOQUE_BAIXO, params);
    }

    public void notificacaoPecaParada(String email, String nome, Map<String, String> params) {
        notificacaoProducer.enviarMensagem(email, nome, TEMPLATE_PECA_PARADA, params);
    }

    public void notificacaoRevendedoraInativa(String email, String nome, Map<String, String> params) {
        notificacaoProducer.enviarMensagem(email, nome, TEMPLATE_REVENDEDORA_INATIVA, params);
    }

    public static String nomeTemplate(Long templateId) {
        if (templateId == null) {
            return null;
        }
        return switch (templateId.intValue()) {
            case 2 -> "Boas-vindas";
            case 3 -> "Lote em consignação";
            case 4 -> "Alerta de vencimento";
            case 5 -> "Cobrança de atraso";
            case 6 -> "Fechamento de lote";
            case 7 -> "Aniversário";
            case 8 -> "Meta atingida";
            case 9 -> "Estoque baixo";
            case 10 -> "Peça parada";
            case 11 -> "Revendedora inativa";
            default -> "Desconhecido (" + templateId + ")";
        };
    }
}