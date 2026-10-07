package com.caeca.estoque.service.NotificacaoBrevo;

import com.caeca.estoque.database.model.movimentacaoRevendedora.MovimentacaoRevendedora;
import com.caeca.estoque.database.model.revendedora.Revendedora;
import com.caeca.estoque.dto.brevo.NotificacaoEventoDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import java.time.LocalDateTime;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class NotificacaoBrevoServiceTest {

    @Mock
    private ApplicationEventPublisher applicationEventPublisher;

    private NotificacaoBrevoService notificacaoBrevoService;

    @BeforeEach
    void setUp() {
        notificacaoBrevoService = new NotificacaoBrevoService(applicationEventPublisher);
    }

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

    private NotificacaoEventoDto capturarEvento() {
        ArgumentCaptor<NotificacaoEventoDto> captor = ArgumentCaptor.forClass(NotificacaoEventoDto.class);
        verify(applicationEventPublisher, times(1)).publishEvent(captor.capture());

        return captor.getValue();
    }

    @Nested
    @DisplayName("notificacaoLoteEmConsignacao")
    class notificacaoLoteEmConsignacao {

        @Test
        @DisplayName("Deve publicar o template 3 com nome da revendedora e data de vencimento")
        void devePublicarTemplateTres() {
            notificacaoBrevoService.notificacaoLoteEmConsignacao(maleta());

            NotificacaoEventoDto evento = capturarEvento();

            assertEquals(3L, evento.templateId());
            assertEquals("joias@exemplo.com", evento.emailDestino());
            assertEquals("Joias Silva", evento.nomeRevendedora());
            assertEquals(Map.of(
                    "nomeRevendedora", "Joias Silva",
                    "dataVencimento", "15/01/2026"
            ), evento.parametros());
        }
    }

    @Nested
    @DisplayName("notificacaoAlertaVencimento")
    class notificacaoAlertaVencimento {

        @Test
        @DisplayName("Deve publicar o template 4 com dias restantes e data de vencimento")
        void devePublicarTemplateQuatro() {
            notificacaoBrevoService.notificacaoAlertaVencimento(maleta(), 7L);

            NotificacaoEventoDto evento = capturarEvento();

            assertEquals(4L, evento.templateId());
            assertEquals(Map.of(
                    "nomeRevendedora", "Joias Silva",
                    "diasRestantes", "7",
                    "dataVencimento", "15/01/2026"
            ), evento.parametros());
        }
    }

    @Nested
    @DisplayName("notificacaoCobrancaAtraso")
    class notificacaoCobrancaAtraso {

        @Test
        @DisplayName("Deve publicar o template 5 com nome e data de vencimento")
        void devePublicarTemplateCinco() {
            notificacaoBrevoService.notificacaoCobrancaAtraso(maleta());

            NotificacaoEventoDto evento = capturarEvento();

            assertEquals(5L, evento.templateId());
            assertEquals(Map.of(
                    "nomeRevendedora", "Joias Silva",
                    "dataVencimento", "15/01/2026"
            ), evento.parametros());
        }
    }

    @Nested
    @DisplayName("notificacaoFechamentoLote")
    class notificacaoFechamentoLote {

        @Test
        @DisplayName("Deve publicar o template 6 com o mes e ano de criacao da maleta")
        void devePublicarTemplateSeis() {
            notificacaoBrevoService.notificacaoFechamentoLote(maleta());

            NotificacaoEventoDto evento = capturarEvento();

            assertEquals(6L, evento.templateId());
            assertEquals(Map.of(
                    "nomeRevendedora", "Joias Silva",
                    "mes", "janeiro de 2025"
            ), evento.parametros());
        }
    }

    @Nested
    @DisplayName("notificacaoBoasVindas")
    class notificacaoBoasVindas {

        @Test
        @DisplayName("Deve publicar o template 2 mantendo o comportamento existente")
        void devePublicarTemplateDois() {
            notificacaoBrevoService.notificacaoBoasVindas("nova@exemplo.com", "Revendedora Nova");

            NotificacaoEventoDto evento = capturarEvento();

            assertEquals(2L, evento.templateId());
            assertEquals("nova@exemplo.com", evento.emailDestino());
            assertEquals(Map.of("nomeRevendedora", "Revendedora Nova"), evento.parametros());
        }
    }
}