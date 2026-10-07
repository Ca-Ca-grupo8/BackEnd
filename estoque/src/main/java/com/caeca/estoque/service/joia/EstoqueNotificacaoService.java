package com.caeca.estoque.service.joia;

import com.caeca.estoque.database.model.joia.Joia;
import com.caeca.estoque.database.model.notificacaoEnvio.TipoReferencia;
import com.caeca.estoque.service.NotificacaoBrevo.NotificacaoBrevoService;
import com.caeca.estoque.service.notificacaoEnvio.NotificacaoEnvioService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

@Service
public class EstoqueNotificacaoService {

    private final NotificacaoBrevoService notificacaoBrevoService;
    private final NotificacaoEnvioService notificacaoEnvioService;

    @Value("${brevo.internal.recipient:eduardonuneslima07@gmail.com}")
    private String internalRecipient;

    @Value("${brevo.internal.recipient.name:Equipe Caeca}")
    private String internalRecipientName;

    public EstoqueNotificacaoService(NotificacaoBrevoService notificacaoBrevoService,
                                    NotificacaoEnvioService notificacaoEnvioService) {
        this.notificacaoBrevoService = notificacaoBrevoService;
        this.notificacaoEnvioService = notificacaoEnvioService;
    }

    public void notificarEstoqueBaixo(Joia joia) {
        if (registrarEnvioEstoque(joia, NotificacaoBrevoService.TEMPLATE_ESTOQUE_BAIXO, true)) {
            Map<String, String> params = new HashMap<>();
            params.put("nomeProduto", joia.getNome() != null ? joia.getNome() : "");
            params.put("quantidadeAtual", String.valueOf(joia.getQuantidade() != null ? joia.getQuantidade() : 0));
            params.put("estoqueMinimo", String.valueOf(joia.getQuantidadeMinima() != null ? joia.getQuantidadeMinima() : 0));
            params.put("categoriaProduto", joia.getCategoria() != null && joia.getCategoria().getNome() != null ? joia.getCategoria().getNome() : "");
            params.put("materialProduto", joia.getMaterial() != null && joia.getMaterial().getNome() != null ? joia.getMaterial().getNome() : "");
            params.put("banhoProduto", joia.getBanho() != null && joia.getBanho().getNome() != null ? joia.getBanho().getNome() : "");
            notificacaoBrevoService.notificacaoEstoqueBaixo(internalRecipient, internalRecipientName, params);
        }
    }

    public void notificarPecaParada(Joia joia, Integer quantidadeParada, Double valorCustoTotal, LocalDate dataUltimaSaida) {
        if (registrarEnvioEstoque(joia, NotificacaoBrevoService.TEMPLATE_PECA_PARADA, true)) {
            Map<String, String> params = new HashMap<>();
            params.put("nomeProduto", joia.getNome() != null ? joia.getNome() : "");
            params.put("quantidadeParada", String.valueOf(quantidadeParada != null ? quantidadeParada : (joia.getQuantidade() != null ? joia.getQuantidade() : 0)));
            params.put("valorCustoTotal", valorCustoTotal != null ? formatarMoeda(valorCustoTotal) : "0.00");
            params.put("dataUltimaSaida", dataUltimaSaida != null ? dataUltimaSaida.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) : "");
            notificacaoBrevoService.notificacaoPecaParada(internalRecipient, internalRecipientName, params);
        }
    }

    private boolean registrarEnvioEstoque(Joia joia, long templateId, boolean permiteReenvio) {
        if (joia == null || joia.getId() == null) {
            return false;
        }
        return notificacaoEnvioService.registrarEnvio(
                TipoReferencia.ESTOQUE,
                joia.getId(),
                templateId,
                permiteReenvio);
    }

    private String formatarMoeda(double valor) {
        return BigDecimal.valueOf(valor)
                .setScale(2, RoundingMode.HALF_UP)
                .toString();
    }
}
