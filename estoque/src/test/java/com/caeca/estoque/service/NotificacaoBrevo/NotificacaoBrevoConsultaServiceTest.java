package com.caeca.estoque.service.NotificacaoBrevo;

import com.caeca.estoque.client.BrevoClient;
import com.caeca.estoque.dto.brevo.BrevoEmailListResponse;
import com.caeca.estoque.dto.brevo.BrevoEmailListResponse.EmailTransacional;
import com.caeca.estoque.dto.brevo.NotificacaoBrevoEnvioDto;
import com.caeca.estoque.exception.BadRequestException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.when;

class NotificacaoBrevoConsultaServiceTest {

    private BrevoClient brevoClient;
    private NotificacaoBrevoConsultaService service;

    @BeforeEach
    void setUp() {
        brevoClient = mock(BrevoClient.class);
        service = new NotificacaoBrevoConsultaService(brevoClient);
        ReflectionTestUtils.setField(service, "apiKey", "chave-de-teste");
    }

    @Test
    @DisplayName("Deve mapear os envios da Brevo com o nome do template")
    void deveMapearEnvios() {
        var envio = new EmailTransacional("2026-10-19T10:00:00Z", "revendedora@exemplo.com", "<msg@brevo>", "Assunto", 9L);
        when(brevoClient.listarEmailsTransacionais(anyString(), anyLong(), isNull(), isNull(),
                isNull(), isNull(), isNull(), isNull(), isNull()))
                .thenReturn(new BrevoEmailListResponse(1L, List.of(envio)));

        List<NotificacaoBrevoEnvioDto> envios = service.listarEnvios(null, null, null, null, null, null, null, null);

        assertEquals(1, envios.size());
        assertEquals("Estoque baixo", envios.get(0).nomeTemplate());
        assertEquals("revendedora@exemplo.com", envios.get(0).emailDestinatario());
    }

    @Test
    @DisplayName("Deve usar limit 20 por padrão e repassar a chave configurada")
    void deveUsarLimitPadrao() {
        when(brevoClient.listarEmailsTransacionais(any(), any(), any(), any(), any(), any(), any(), any(), any()))
                .thenReturn(new BrevoEmailListResponse(0L, null));

        assertEquals(List.of(), service.listarEnvios(null, null, null, null, null, null, null, null));
        verify(brevoClient).listarEmailsTransacionais(eq("chave-de-teste"), eq(20L), any(), any(),
                any(), any(), any(), any(), any());
    }

    @Test
    @DisplayName("Deve rejeitar limit fora de 1 a 100 sem chamar a Brevo")
    void deveRejeitarLimitInvalido() {
        assertThrows(BadRequestException.class,
                () -> service.listarEnvios(101L, null, null, null, null, null, null, null));
        verify(brevoClient, never()).listarEmailsTransacionais(any(), any(), any(), any(), any(), any(), any(), any(), any());
    }

    @Test
    @DisplayName("Deve exigir startDate e endDate juntos")
    void deveExigirPeriodoCompleto() {
        assertThrows(BadRequestException.class,
                () -> service.listarEnvios(null, null, null, null, null, null, "2026-10-01", null));
    }
}
