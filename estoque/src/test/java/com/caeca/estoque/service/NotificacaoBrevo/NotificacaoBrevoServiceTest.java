package com.caeca.estoque.service.NotificacaoBrevo;

import com.caeca.estoque.database.model.movimentacaoRevendedora.MovimentacaoRevendedora;
import com.caeca.estoque.database.model.revendedora.Revendedora;
import com.caeca.estoque.database.model.statusMaleta.StatusMaleta;
import com.caeca.estoque.database.model.statusMaleta.StatusMaletaEnum;
import com.caeca.estoque.producer.interfaceProducer.ContatoProducer;
import com.caeca.estoque.producer.interfaceProducer.NotificacaoProducer;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

@ExtendWith(MockitoExtension.class)
class NotificacaoBrevoServiceTest {

    @Mock
    private NotificacaoProducer notificacaoProducer;

    @Mock
    private ContatoProducer contatoProducer;

    @InjectMocks
    private NotificacaoBrevoService notificacaoBrevoService;

    private MovimentacaoRevendedora maleta() {
        Revendedora revendedora = new Revendedora();
        revendedora.setNome("Joias Silva");
        revendedora.setEmail("joias@exemplo.com");

        MovimentacaoRevendedora maleta = new MovimentacaoRevendedora();
        maleta.setRevendedora(revendedora);
        maleta.setDataLimite(LocalDateTime.of(2026, 1, 15, 18, 30));
        maleta.setData(LocalDateTime.of(2025, 1, 10, 9, 0));

        return maleta;
    }

    @Nested
    @DisplayName("sincronizarContatoMaletaEmConsignacao")
    class sincronizarContatoMaletaEmConsignacao {

        @Test
        @DisplayName("Deve publicar os atributos da maleta para sincronizar o contato")
        void devePublicarAtributosDaMaleta() {
            MovimentacaoRevendedora maleta = maleta();
            maleta.setIdMovimentacaoRevendedora(9L);
            maleta.setStatus(new StatusMaleta(2L, StatusMaletaEnum.EM_CONSIGNACAO));

            notificacaoBrevoService.sincronizarContatoMaletaEmConsignacao(maleta);

            verify(contatoProducer, times(1)).sincronizarContatoMaletaEmConsignacao(Map.of(
                    "ID_ULTIMA_MALETA", 9L,
                    "NOME", "Joias Silva",
                    "EMAIL", "joias@exemplo.com",
                    "STATUS_MALETA", "EM_CONSIGNACAO",
                    "DATA_VENCIMENTO", "2026-01-15",
                    "CREATION_DATE", "2025-01-10"));
            verifyNoInteractions(notificacaoProducer);
        }
    }

    @Nested
    @DisplayName("notificacaoAlertaVencimento")
    class notificacaoAlertaVencimento {

        @Test
        @DisplayName("Deve enviar o template 4 com dias restantes e data de vencimento")
        void deveEnviarTemplateQuatro() {
            notificacaoBrevoService.notificacaoAlertaVencimento(maleta(), 7L);

            verify(notificacaoProducer, times(1)).enviarExterno(
                    "joias@exemplo.com",
                    "Joias Silva",
                    4L,
                    Map.of(
                            "nomeRevendedora", "Joias Silva",
                            "diasRestantes", "7",
                            "dataVencimento", "15/01/2026"
                    ));
        }
    }

    @Nested
    @DisplayName("notificacaoCobrancaAtraso")
    class notificacaoCobrancaAtraso {

        @Test
        @DisplayName("Deve enviar o template 5 com nome e data de vencimento")
        void deveEnviarTemplateCinco() {
            notificacaoBrevoService.notificacaoCobrancaAtraso(maleta());

            verify(notificacaoProducer, times(1)).enviarExterno(
                    "joias@exemplo.com",
                    "Joias Silva",
                    5L,
                    Map.of(
                            "nomeRevendedora", "Joias Silva",
                            "dataVencimento", "15/01/2026"
                    ));
        }
    }

    @Nested
    @DisplayName("notificacaoMetaAtingida")
    class notificacaoMetaAtingida {

        @Test
        @DisplayName("Deve enviar o template 8 quando a meta foi atingida")
        void deveEnviarTemplateOito() {
            Map<String, String> params = Map.of("mes", "janeiro de 2025");

            notificacaoBrevoService.notificacaoMetaAtingida(maleta(), params, true);

            verify(notificacaoProducer, times(1)).enviarExterno(
                    "joias@exemplo.com",
                    "Joias Silva",
                    8L,
                    params);
        }

        @Test
        @DisplayName("Deve enviar o template 6 de fechamento quando a meta nao foi atingida")
        void deveEnviarTemplateSeis() {
            Map<String, String> params = Map.of("mes", "janeiro de 2025");

            notificacaoBrevoService.notificacaoMetaAtingida(maleta(), params, false);

            verify(notificacaoProducer, times(1)).enviarExterno(
                    "joias@exemplo.com",
                    "Joias Silva",
                    6L,
                    params);
        }
    }

    @Nested
    @DisplayName("notificacaoBoasVindas")
    class notificacaoBoasVindas {

        @Test
        @DisplayName("Deve enviar o template 2 mantendo o comportamento existente")
        void deveEnviarTemplateDois() {
            notificacaoBrevoService.notificacaoBoasVindas("nova@exemplo.com", "Revendedora Nova");

            verify(notificacaoProducer, times(1)).enviarExterno(
                    "nova@exemplo.com",
                    "Revendedora Nova",
                    2L,
                    Map.of("nomeRevendedora", "Revendedora Nova"));
        }
    }
}
