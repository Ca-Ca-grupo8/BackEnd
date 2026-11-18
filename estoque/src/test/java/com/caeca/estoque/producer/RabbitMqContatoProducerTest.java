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
import java.util.HashMap;
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
    @DisplayName("Criar deve publicar CRIAR com todos os atributos da revendedora e as datas formatadas")
    void devePublicarCriacaoComTodosOsAtributos() {
        new RabbitMqContatoProducer(rabbitTemplate).criarContatoRevendedora(
                42L, "Joias", "joias@exemplo.com", "11987654321", true,
                LocalDate.of(1990, 3, 7), LocalDate.of(2026, 10, 1));

        ContatoEventoDto evento = capturarEvento();
        assertEquals(TipoOperacao.CRIAR, evento.tipo());
        assertEquals(42L, evento.idRevendedora());
        assertEquals("joias@exemplo.com", evento.email());
        assertEquals(Map.of(
                "ID_REVENDEDORA", 42L,
                "NOME", "Joias",
                "EMAIL", "joias@exemplo.com",
                "TELEFONE", "11987654321",
                "ATIVO", true,
                "DATA_NASCIMENTO", "1990-03-07",
                "DATA_ULTIMA_MALETA", "2026-10-01"), evento.atributos());
    }

    @Test
    @DisplayName("Criar sem data de nascimento, telefone ou maleta deve omitir esses atributos")
    void deveOmitirAtributosNulos() {
        new RabbitMqContatoProducer(rabbitTemplate).criarContatoRevendedora(
                42L, "Joias", "joias@exemplo.com", null, false, null, null);

        Map<String, Object> atributos = capturarEvento().atributos();
        assertFalse(atributos.containsKey("DATA_NASCIMENTO"));
        assertFalse(atributos.containsKey("TELEFONE"));
        assertFalse(atributos.containsKey("DATA_ULTIMA_MALETA"));
        assertEquals(false, atributos.get("ATIVO"));
        assertTrue(atributos.containsKey("ID_REVENDEDORA"));
    }

    @Test
    @DisplayName("Atualizar revendedora deve publicar ATUALIZAR com o novo email quando informado")
    void devePublicarAtualizacaoDaRevendedoraComNovoEmail() {
        Map<String, Object> atributos = Map.of("NOME", "Joias");

        new RabbitMqContatoProducer(rabbitTemplate).atualizarContatoRevendedora(42L, "novo@exemplo.com", atributos);

        ContatoEventoDto evento = capturarEvento();
        assertEquals(TipoOperacao.ATUALIZAR, evento.tipo());
        assertEquals(42L, evento.idRevendedora());
        assertEquals("novo@exemplo.com", evento.email());
        assertEquals(atributos, evento.atributos());
    }

    @Test
    @DisplayName("Atualizar revendedora sem troca de email deve publicar ATUALIZAR com email nulo")
    void devePublicarAtualizacaoDaRevendedoraSemEmail() {
        new RabbitMqContatoProducer(rabbitTemplate).atualizarContatoRevendedora(42L, null, Map.of("ATIVO", true));

        ContatoEventoDto evento = capturarEvento();
        assertEquals(null, evento.email());
    }

    @Test
    @DisplayName("Atualizar maleta deve publicar ATUALIZAR com o id da revendedora e só os atributos informados")
    void devePublicarAtualizacao() {
        Map<String, Object> atributos = new HashMap<>(Map.of("ID_ULTIMA_MALETA", 9L));

        new RabbitMqContatoProducer(rabbitTemplate).atualizarContatoMaleta(42L, atributos);

        ContatoEventoDto evento = capturarEvento();
        assertEquals(TipoOperacao.ATUALIZAR, evento.tipo());
        assertEquals(42L, evento.idRevendedora());
        assertEquals(null, evento.email());
        assertEquals(atributos, evento.atributos());
    }
}
