package com.caeca.estoque.consumer;

import com.caeca.estoque.client.BrevoClient;
import com.caeca.estoque.dto.brevo.BrevoEmailRequest;
import com.caeca.estoque.dto.brevo.NotificacaoEventoDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class CrmNotificadorConsumerTest {

    @Mock
    private BrevoClient brevoClient;

    private CrmNotificadorConsumer consumer() {
        CrmNotificadorConsumer consumer = new CrmNotificadorConsumer(brevoClient);
        ReflectionTestUtils.setField(consumer, "apiKey", "chave-de-teste");

        return consumer;
    }

    private NotificacaoEventoDto evento() {
        return new NotificacaoEventoDto(
                "joias@exemplo.com",
                "Joias Silva",
                3L,
                Map.of("nomeRevendedora", "Joias Silva", "dataVencimento", "15/01/2026")
        );
    }

    @Test
    @DisplayName("Deve enviar o e-mail ao Brevo com a chave configurada e o template do evento")
    void deveEnviarEmailAoBrevo() {
        consumer().processarNotificacao(evento());

        ArgumentCaptor<BrevoEmailRequest> captor = ArgumentCaptor.forClass(BrevoEmailRequest.class);
        verify(brevoClient, times(1)).enviarEmailTransacional(eq("chave-de-teste"), captor.capture());

        BrevoEmailRequest request = captor.getValue();

        assertEquals(3L, request.templateId());
        assertEquals(1, request.to().size());
        assertEquals("joias@exemplo.com", request.to().get(0).email());
        assertEquals("Joias Silva", request.to().get(0).name());
        assertEquals("15/01/2026", request.params().get("dataVencimento"));
    }

    @Test
    @DisplayName("Deve absorver a falha do Brevo sem propagar a excecao")
    void deveAbsorverFalhaDoBrevo() {
        doThrow(new RuntimeException("Brevo fora do ar")).when(brevoClient)
                .enviarEmailTransacional(any(), any());

        assertDoesNotThrow(() -> consumer().processarNotificacao(evento()));

        verify(brevoClient, times(1)).enviarEmailTransacional(any(), any());
    }
}