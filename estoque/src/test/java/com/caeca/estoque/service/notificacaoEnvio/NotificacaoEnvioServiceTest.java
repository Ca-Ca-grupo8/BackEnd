package com.caeca.estoque.service.notificacaoEnvio;

import com.caeca.estoque.database.model.notificacaoEnvio.NotificacaoEnvio;
import com.caeca.estoque.database.model.notificacaoEnvio.TipoReferencia;
import com.caeca.estoque.database.repository.notificacaoEnvio.NotificacaoEnvioRepository;
import com.caeca.estoque.dto.notificacaoEnvio.NotificacaoEnvioDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class NotificacaoEnvioServiceTest {

    @Test
    @DisplayName("Deve listar os envios registrados com o nome do template")
    void deveListarEnviosComNomeDoTemplate() {
        NotificacaoEnvioRepository repository = mock(NotificacaoEnvioRepository.class);
        NotificacaoEnvio envio = new NotificacaoEnvio(TipoReferencia.ESTOQUE, 7L, 9L, true);
        envio.setIdNotificacaoEnvio(1L);
        when(repository.buscarEnvios(eq(TipoReferencia.ESTOQUE), eq(9L), any()))
                .thenReturn(new PageImpl<>(List.of(envio)));

        var service = new NotificacaoEnvioService(repository);
        var pagina = service.listarEnvios(TipoReferencia.ESTOQUE, 9L, PageRequest.of(0, 10));

        NotificacaoEnvioDto dto = pagina.getContent().get(0);
        assertEquals(1, pagina.getTotalElements());
        assertEquals(7L, dto.referenciaId());
        assertEquals("Estoque baixo", dto.nomeTemplate());
    }
}
