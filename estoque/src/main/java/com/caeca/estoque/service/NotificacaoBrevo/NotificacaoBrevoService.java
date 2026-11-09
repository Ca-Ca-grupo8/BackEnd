package com.caeca.estoque.service.NotificacaoBrevo;

import com.caeca.estoque.database.model.movimentacaoRevendedora.MovimentacaoRevendedora;
import com.caeca.estoque.database.model.revendedora.Revendedora;
import com.caeca.estoque.dto.brevo.NotificacaoEventoDto;
import com.caeca.estoque.dto.maleta.MaletaNotificacaoMapper;
import com.caeca.estoque.producer.interfaceProducer.ContatoProducer;
import com.caeca.estoque.producer.interfaceProducer.NotificacaoProducer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class NotificacaoBrevoService {

    public static final long TEMPLATE_LOTE_EM_CONSIGNACAO = 3L;
    public static final long TEMPLATE_FECHAMENTO_LOTE = 6L;
    public static final long TEMPLATE_META_ATINGIDA = 8L;
    public static final long TEMPLATE_ESTOQUE_BAIXO = 9L;
    public static final long TEMPLATE_PECA_PARADA = 10L;
    public static final long TEMPLATE_CONTATO_MALETA_EM_CONSIGNACAO = 12L;
    public static final long TEMPLATE_CONTATO_REVENDEDORA_CRIADA = 13L;

    private static final DateTimeFormatter DATA_ATRIBUTO_BREVO = DateTimeFormatter.ISO_LOCAL_DATE;

    private final NotificacaoProducer notificacaoProducer;
    private final ContatoProducer contatoProducer;

    public NotificacaoBrevoService(NotificacaoProducer notificacaoProducer, ContatoProducer contatoProducer) {
        this.notificacaoProducer = notificacaoProducer;
        this.contatoProducer = contatoProducer;
    }

    public void notificacaoLoteEmConsignacao(MovimentacaoRevendedora maleta, byte[] comprovante) {
        enviarParaMaleta(maleta, TEMPLATE_LOTE_EM_CONSIGNACAO, Map.of(
                "nomeRevendedora", MaletaNotificacaoMapper.nomeRevendedora(maleta),
                "dataVencimento", MaletaNotificacaoMapper.dataVencimento(maleta)
        ), comprovante == null ? List.of() : List.of(new NotificacaoEventoDto.Anexo(
                "comprovante-maleta-" + maleta.getIdMovimentacaoRevendedora() + ".pdf",
                Base64.getEncoder().encodeToString(comprovante)
        )));
    }

    public void criarContatoRevendedora(Revendedora revendedora) {
        criarContatoRevendedora(revendedora, revendedora.getDataCadastro());
    }

    public void criarContatoRevendedora(Revendedora revendedora, LocalDate dataUltimaMaleta) {
        contatoProducer.criarContatoRevendedora(
                revendedora.getId(),
                revendedora.getNome(),
                revendedora.getEmail(),
                revendedora.getTelefone(),
                revendedora.isAtivo(),
                revendedora.getDataNascimento(),
                dataUltimaMaleta
        );
    }

    public void atualizarContatoRevendedora(Revendedora revendedora, String emailAnterior) {
        boolean emailMudou = emailAnterior != null && !emailAnterior.equals(revendedora.getEmail());

        var atributos = new HashMap<String, Object>();
        atributos.put("NOME", revendedora.getNome());
        atributos.put("TELEFONE", revendedora.getTelefone());
        atributos.put("ATIVO", revendedora.isAtivo());
        if (revendedora.getDataNascimento() != null) {
            atributos.put("DATA_NASCIMENTO", revendedora.getDataNascimento().format(DATA_ATRIBUTO_BREVO));
        }
        if (emailMudou) {
            atributos.put("EMAIL", revendedora.getEmail());
        }

        contatoProducer.atualizarContatoRevendedora(
                revendedora.getId(),
                emailMudou ? revendedora.getEmail() : null,
                atributos
        );
    }

    /**
     * Mantém no contato a última maleta da revendedora: id, status e data. Chamado na criação e em
     * toda mudança de status da maleta mais recente.
     */
    public void atualizarUltimaMaleta(MovimentacaoRevendedora maleta) {
        var atributos = new HashMap<String, Object>();
        atributos.put("ID_ULTIMA_MALETA", maleta.getIdMovimentacaoRevendedora());
        atributos.put("STATUS_MALETA", maleta.getStatus() != null ? maleta.getStatus().getNome().name() : "");
        atributos.put("DATA_ULTIMA_MALETA", formatarDataAtributo(maleta.getData()));
        atributos.put("DATA_VENCIMENTO_MALETA", formatarDataAtributo(maleta.getDataLimite()));

        contatoProducer.atualizarContatoMaleta(maleta.getRevendedora().getId(), atributos);
    }

    public void sincronizarContatoMaletaEmConsignacao(MovimentacaoRevendedora maleta) {
        contatoProducer.atualizarContatoMaleta(maleta.getRevendedora().getId(), Map.of(
                "ID_ULTIMA_MALETA", maleta.getIdMovimentacaoRevendedora(),
                "STATUS_MALETA", maleta.getStatus() != null ? maleta.getStatus().getNome().name() : "",
                "DATA_VENCIMENTO_MALETA", formatarDataAtributo(maleta.getDataLimite()),
                "CREATION_DATE", formatarDataAtributo(maleta.getData())
        ));
    }

    private static String formatarDataAtributo(LocalDateTime data) {
        return data == null ? "" : data.format(DATA_ATRIBUTO_BREVO);
    }

    public void notificacaoMetaAtingida(MovimentacaoRevendedora maleta, Map<String, String> params, boolean metaAtingida) {
        enviarParaMaleta(maleta, metaAtingida ? TEMPLATE_META_ATINGIDA : TEMPLATE_FECHAMENTO_LOTE, params);
    }

    public void notificacaoEstoqueBaixo(String email, String nome, Map<String, String> params) {
        notificacaoProducer.enviarInterno(email, nome, TEMPLATE_ESTOQUE_BAIXO, params);
    }

    public void notificacaoPecaParada(String email, String nome, Map<String, String> params) {
        notificacaoProducer.enviarInterno(email, nome, TEMPLATE_PECA_PARADA, params);
    }

    private void enviarParaMaleta(MovimentacaoRevendedora maleta, long templateId, Map<String, String> params) {
        notificacaoProducer.enviarExterno(
                MaletaNotificacaoMapper.emailRevendedora(maleta),
                MaletaNotificacaoMapper.nomeRevendedora(maleta),
                templateId,
                params
        );
    }

    private void enviarParaMaleta(MovimentacaoRevendedora maleta, long templateId, Map<String, String> params, List<NotificacaoEventoDto.Anexo> anexos) {
        notificacaoProducer.enviarExternoComAnexos(
                MaletaNotificacaoMapper.emailRevendedora(maleta),
                MaletaNotificacaoMapper.nomeRevendedora(maleta),
                templateId,
                params,
                anexos
        );
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
            case 12 -> "Sync de contato (consignação)";
            case 13 -> "Criação de contato (revendedora)";
            default -> "Desconhecido (" + templateId + ")";
        };
    }
}