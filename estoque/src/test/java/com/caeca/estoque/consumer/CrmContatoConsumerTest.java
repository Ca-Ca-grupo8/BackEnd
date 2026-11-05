package com.caeca.estoque.consumer;

import com.caeca.estoque.client.BrevoClient;
import com.caeca.estoque.dto.brevo.BrevoContatoRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class CrmContatoConsumerTest {

    @Mock
    private BrevoClient brevoClient;

    private CrmContatoConsumer consumer() {
        var consumer = new CrmContatoConsumer(brevoClient);
        ReflectionTestUtils.setField(consumer, "apiKey", "chave-teste");
        return consumer;
    }

    private BrevoContatoRequest request() {
        return new BrevoContatoRequest("joias@exemplo.com", List.of(5L), true,
                Map.of("ID_ULTIMA_MALETA", 9L));
    }

    @Test
    @DisplayName("Deve repassar o contato ao Brevo com a api key configurada")
    void deveCriarOuAtualizarContato() {
        var request = request();

        consumer().processarContato(request);

        verify(brevoClient, times(1)).criarOuAtualizarContato(eq("chave-teste"), eq(request));
    }

    @Test
    @DisplayName("Deve relancar a falha do Brevo para o RabbitMQ aplicar o retry")
    void deveRelancarFalhaDoBrevo() {
        doThrow(new RuntimeException("Brevo fora do ar")).when(brevoClient)
                .criarOuAtualizarContato(any(), any());

        assertThrows(RuntimeException.class, () -> consumer().processarContato(request()));
    }
}
