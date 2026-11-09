package com.caeca.estoque.producer;

import com.caeca.estoque.config.rabbitMq.RabbitMQConfig;
import com.caeca.estoque.dto.brevo.ContatoEventoDto;
import com.caeca.estoque.dto.brevo.ContatoEventoDto.TipoOperacao;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import java.time.LocalDate;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class RabbitMqContatoProducerTest {

    @Mock
    private RabbitTemplate rabbitTemplate;

    private ContatoEventoDto capturarEvento() {
        ArgumentCaptor<Object> captor = ArgumentCaptor.forClass(Object.class);
        verify(rabbitTemplate).convertAndSend(
                eq(RabbitMQConfig.EXCHANGE_NOTIFICACAO),
                eq(RabbitMQConfig.ROUTING_KEY_CONTATO),
                captor.capture());
        return (ContatoEventoDto) captor.getValue();
    }

    @Test
    @DisplayName("Criar deve publicar CRIAR com id, email e atributos completos incluindo data de nascimento")
    void devePublicarCriacaoComDataDeNascimento() {
        new RabbitMqContatoProducer(rabbitTemplate)
                .criarContatoRevendedora(42L, "Joias", "joias@exemplo.com", LocalDate.of(1990, 3, 7));

        ContatoEventoDto evento = capturarEvento();
        assertEquals(TipoOperacao.CRIAR, evento.tipo());
        assertEquals(42L, evento.idRevendedora());
        assertEquals("joias@exemplo.com", evento.email());
        assertEquals(Map.of(
                "ID_REVENDEDORA", 42L,
                "NOME", "Joias",
                "EMAIL", "joias@exemplo.com",
                "DATA_NASCIMENTO", "1990-03-07"), evento.atributos());
    }

    @Test
    @DisplayName("Criar sem data de nascimento deve omitir o atributo DATA_NASCIMENTO")
    void deveOmitirDataDeNascimentoQuandoNula() {
        new RabbitMqContatoProducer(rabbitTemplate)
                .criarContatoRevendedora(42L, "Joias", "joias@exemplo.com", null);

        Map<String, Object> atributos = capturarEvento().atributos();
        assertFalse(atributos.containsKey("DATA_NASCIMENTO"));
        assertTrue(atributos.containsKey("ID_REVENDEDORA"));
    }

    @Test
    @DisplayName("Atualizar deve publicar ATUALIZAR com o id da revendedora e só os atributos informados")
    void devePublicarAtualizacao() {
        Map<String, Object> atributos = Map.of("ID_ULTIMA_MALETA", 9L);

        new RabbitMqContatoProducer(rabbitTemplate).atualizarContatoMaleta(42L, atributos);

        ContatoEventoDto evento = capturarEvento();
        assertEquals(TipoOperacao.ATUALIZAR, evento.tipo());
        assertEquals(42L, evento.idRevendedora());
        assertEquals(null, evento.email());
        assertEquals(atributos, evento.atributos());
    }
}
