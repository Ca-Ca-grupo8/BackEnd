package com.caeca.estoque.service.joia;

import com.caeca.estoque.database.model.joia.Joia;
import com.caeca.estoque.database.model.notificacaoEnvio.TipoReferencia;
import com.caeca.estoque.database.model.usuario.StatusUsuario;
import com.caeca.estoque.database.model.usuario.Usuario;
import com.caeca.estoque.database.repository.usuario.UsuarioRepository;
import com.caeca.estoque.service.NotificacaoBrevo.NotificacaoBrevoService;
import com.caeca.estoque.service.notificacaoEnvio.NotificacaoEnvioService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class EstoqueNotificacaoService {

    private final NotificacaoBrevoService notificacaoBrevoService;
    private final NotificacaoEnvioService notificacaoEnvioService;
    private final UsuarioRepository usuarioRepository;

    public EstoqueNotificacaoService(NotificacaoBrevoService notificacaoBrevoService,
                                    NotificacaoEnvioService notificacaoEnvioService,
                                    UsuarioRepository usuarioRepository) {
        this.notificacaoBrevoService = notificacaoBrevoService;
        this.notificacaoEnvioService = notificacaoEnvioService;
        this.usuarioRepository = usuarioRepository;
    }

    public void notificarEstoqueBaixo(Joia joia) {
        if (!registrarEnvioEstoque(joia, NotificacaoBrevoService.TEMPLATE_ESTOQUE_BAIXO, true)) {
            return;
        }

        Map<String, String> params = new HashMap<>();
        params.put("nomeProduto", joia.getNome() != null ? joia.getNome() : "");
        params.put("quantidadeAtual", String.valueOf(joia.getQuantidade() != null ? joia.getQuantidade() : 0));
        params.put("estoqueMinimo", String.valueOf(joia.getQuantidadeMinima() != null ? joia.getQuantidadeMinima() : 0));
        params.put("categoriaProduto", joia.getCategoria() != null && joia.getCategoria().getNome() != null ? joia.getCategoria().getNome() : "");
        params.put("materialProduto", joia.getMaterial() != null && joia.getMaterial().getNome() != null ? joia.getMaterial().getNome() : "");
        params.put("banhoProduto", joia.getBanho() != null && joia.getBanho().getNome() != null ? joia.getBanho().getNome() : "");

        for (Usuario usuario : usuariosAtivosComEmail("estoque baixo", joia)) {
            try {
                notificacaoBrevoService.notificacaoEstoqueBaixo(usuario.getEmail(), usuario.getNome(), params);
            } catch (RuntimeException e) {
                log.error("Falha ao enviar alerta de estoque baixo para o usuario {}.", usuario.getId(), e);
            }
        }
    }

    public void notificarPecaParada(Joia joia, Integer quantidadeParada, Double valorCustoTotal, LocalDate dataUltimaSaida) {
        if (registrarEnvioEstoque(joia, NotificacaoBrevoService.TEMPLATE_PECA_PARADA, true)) {
            Map<String, String> params = new HashMap<>();
            params.put("nomeProduto", joia.getNome() != null ? joia.getNome() : "");
            params.put("quantidadeParada", String.valueOf(quantidadeParada != null ? quantidadeParada : (joia.getQuantidade() != null ? joia.getQuantidade() : 0)));
            params.put("valorCustoTotal", valorCustoTotal != null ? formatarMoeda(valorCustoTotal) : "0.00");
            params.put("dataUltimaSaida", dataUltimaSaida != null ? dataUltimaSaida.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) : "");

            for (Usuario usuario : usuariosAtivosComEmail("peca parada", joia)) {
                try {
                    notificacaoBrevoService.notificacaoPecaParada(usuario.getEmail(), usuario.getNome(), params);
                } catch (RuntimeException e) {
                    log.error("Falha ao enviar alerta de peca parada para o usuario {}.", usuario.getId(), e);
                }
            }
        }
    }

    private List<Usuario> usuariosAtivosComEmail(String alerta, Joia joia) {
        var ativos = usuarioRepository.findByStatus(StatusUsuario.ATIVO).stream()
                .filter(usuario -> usuario.getEmail() != null && !usuario.getEmail().isBlank())
                .toList();
        if (ativos.isEmpty()) {
            log.warn("Alerta de {} da joia {} sem destinatarios: nenhum usuario ativo.", alerta, joia.getId());
        }
        return ativos;
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
