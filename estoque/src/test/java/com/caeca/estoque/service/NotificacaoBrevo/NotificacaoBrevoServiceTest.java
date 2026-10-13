package com.caeca.estoque.service.NotificacaoBrevo;

import com.caeca.estoque.database.model.movimentacaoRevendedora.MovimentacaoRevendedora;
import com.caeca.estoque.database.model.revendedora.Revendedora;
import com.caeca.estoque.producer.interfaceProducer.NotificacaoProducer;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Map;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class NotificacaoBrevoServiceTest {

    @Mock
    private NotificacaoProducer notificacaoProducer;

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
    @DisplayName("notificacaoLoteEmConsignacao")
    class notificacaoLoteEmConsignacao {

        @Test
        @DisplayName("Deve enviar o template 3 com nome da revendedora e data de vencimento")
        void deveEnviarTemplateTres() {
            notificacaoBrevoService.notificacaoLoteEmConsignacao(maleta());

            verify(notificacaoProducer, times(1)).enviarMensagem(
                    "joias@exemplo.com",
                    "Joias Silva",
                    3L,
                    Map.of(
                            "nomeRevendedora", "Joias Silva",
                            "dataVencimento", "15/01/2026"
                    ));
        }
    }

    @Nested
    @DisplayName("notificacaoAlertaVencimento")
    class notificacaoAlertaVencimento {

        @Test
        @DisplayName("Deve enviar o template 4 com dias restantes e data de vencimento")
        void deveEnviarTemplateQuatro() {
            notificacaoBrevoService.notificacaoAlertaVencimento(maleta(), 7L);

            verify(notificacaoProducer, times(1)).enviarMensagem(
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

            verify(notificacaoProducer, times(1)).enviarMensagem(
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

            verify(notificacaoProducer, times(1)).enviarMensagem(
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

            verify(notificacaoProducer, times(1)).enviarMensagem(
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

            verify(notificacaoProducer, times(1)).enviarMensagem(
                    "nova@exemplo.com",
                    "Revendedora Nova",
                    2L,
                    Map.of("nomeRevendedora", "Revendedora Nova"));
        }
    }
}
