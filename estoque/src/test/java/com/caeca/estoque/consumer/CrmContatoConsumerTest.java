package com.caeca.estoque.consumer;

import com.caeca.estoque.client.BrevoClient;
import com.caeca.estoque.dto.brevo.BrevoAtualizarContatoRequest;
import com.caeca.estoque.dto.brevo.BrevoContatoRequest;
import com.caeca.estoque.dto.brevo.ContatoEventoDto;
import com.caeca.estoque.dto.brevo.ContatoEventoDto.TipoOperacao;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

@ExtendWith(MockitoExtension.class)
class CrmContatoConsumerTest {

    @Mock
    private BrevoClient brevoClient;

    private CrmContatoConsumer consumer() {
        var consumer = new CrmContatoConsumer(brevoClient);
        ReflectionTestUtils.setField(consumer, "apiKey", "chave-teste");
        return consumer;
    }

    private ContatoEventoDto criar() {
        return new ContatoEventoDto(TipoOperacao.CRIAR, 42L, "joias@exemplo.com",
                Map.of("ID_REVENDEDORA", 42L, "NOME", "Joias"));
    }

    private ContatoEventoDto atualizar() {
        return new ContatoEventoDto(TipoOperacao.ATUALIZAR, 42L, null,
                Map.of("ID_ULTIMA_MALETA", 9L));
    }

    @Test
    @DisplayName("CRIAR deve criar o contato na lista 5 com ext_id = id da revendedora")
    void deveCriarContatoComExtId() {
        consumer().processarContato(criar());

        ArgumentCaptor<BrevoContatoRequest> captor = ArgumentCaptor.forClass(BrevoContatoRequest.class);
        verify(brevoClient, times(1)).criarOuAtualizarContato(eq("chave-teste"), captor.capture());

        BrevoContatoRequest request = captor.getValue();
        assertEquals("joias@exemplo.com", request.email());
        assertEquals(List.of(5L), request.listIds());
        assertEquals(true, request.updateEnabled());
        assertEquals("42", request.extId());
        assertEquals(Map.of("ID_REVENDEDORA", 42L, "NOME", "Joias"), request.attributes());
    }

    @Test
    @DisplayName("ATUALIZAR deve atualizar o contato localizado por ext_id, sem tocar nas listas")
    void deveAtualizarContatoPorExtId() {
        consumer().processarContato(atualizar());

        ArgumentCaptor<BrevoAtualizarContatoRequest> captor = ArgumentCaptor.forClass(BrevoAtualizarContatoRequest.class);
        verify(brevoClient, times(1)).atualizarContato(eq("chave-teste"), eq("42"), eq("ext_id"), captor.capture());

        assertEquals(Map.of("ID_ULTIMA_MALETA", 9L), captor.getValue().attributes());
        verifyNoMoreInteractions(brevoClient);
    }

    @Test
    @DisplayName("Deve relancar a falha do Brevo para o RabbitMQ aplicar o retry")
    void deveRelancarFalhaDoBrevo() {
        doThrow(new RuntimeException("Brevo fora do ar")).when(brevoClient)
                .atualizarContato(any(), any(), any(), any());

        assertThrows(RuntimeException.class, () -> consumer().processarContato(atualizar()));
    }
}
