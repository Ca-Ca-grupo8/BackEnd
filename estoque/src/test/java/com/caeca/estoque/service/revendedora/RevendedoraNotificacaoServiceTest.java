package com.caeca.estoque.service.revendedora;

import com.caeca.estoque.database.model.notificacaoEnvio.TipoReferencia;
import com.caeca.estoque.database.model.revendedora.Revendedora;
import com.caeca.estoque.service.NotificacaoBrevo.NotificacaoBrevoService;
import com.caeca.estoque.service.notificacaoEnvio.NotificacaoEnvioService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RevendedoraNotificacaoServiceTest {

    @Mock
    private NotificacaoBrevoService notificacaoBrevoService;

    @Mock
    private NotificacaoEnvioService notificacaoEnvioService;

    @InjectMocks
    private RevendedoraNotificacaoService revendedoraNotificacaoService;

    @Nested
    @DisplayName("notificarAniversario")
    class notificarAniversario {

        @Test
        @DisplayName("Deve enviar o e-mail de aniversário quando o envio puder ser registrado")
        void deveEnviarOEmailDeAniversarioQuandoOEnvioPuderSerRegistrado() {
            Revendedora revendedora = revendedora(1L, "11987654321");
            when(notificacaoEnvioService.registrarEnvio(any(), anyLong(), anyLong(), anyBoolean()))
                    .thenReturn(true);

            revendedoraNotificacaoService.notificarAniversario(revendedora);

            verify(notificacaoEnvioService).registrarEnvio(
                    TipoReferencia.REVENDEDORA, 1L, NotificacaoBrevoService.TEMPLATE_ANIVERSARIO, true);
            verify(notificacaoBrevoService).notificacaoAniversario("maria@email.com", "Maria");
        }

        @Test
        @DisplayName("Deve não enviar o e-mail quando o registro do envio for recusado")
        void deveNaoEnviarOEmailQuandoORegistroDoEnvioForRecusado() {
            Revendedora revendedora = revendedora(1L, "11987654321");
            when(notificacaoEnvioService.registrarEnvio(any(), anyLong(), anyLong(), anyBoolean()))
                    .thenReturn(false);

            revendedoraNotificacaoService.notificarAniversario(revendedora);

            verify(notificacaoBrevoService, never()).notificacaoAniversario(any(), any());
        }

        @Test
        @DisplayName("Deve não enviar o e-mail quando a revendedora for nula")
        void deveNaoEnviarOEmailQuandoARevendedoraForNula() {
            revendedoraNotificacaoService.notificarAniversario(null);

            verify(notificacaoEnvioService, never()).registrarEnvio(any(), anyLong(), anyLong(), anyBoolean());
            verify(notificacaoBrevoService, never()).notificacaoAniversario(any(), any());
        }

        @Test
        @DisplayName("Deve não enviar o e-mail quando a revendedora não tiver id")
        void deveNaoEnviarOEmailQuandoARevendedoraNaoTiverId() {
            revendedoraNotificacaoService.notificarAniversario(new Revendedora());

            verify(notificacaoEnvioService, never()).registrarEnvio(any(), anyLong(), anyLong(), anyBoolean());
            verify(notificacaoBrevoService, never()).notificacaoAniversario(any(), any());
        }
    }

    @Nested
    @DisplayName("notificarInatividade")
    class notificarInatividade {

        @Test
        @DisplayName("Deve enviar o e-mail de inatividade com nome, dias e telefone")
        void deveEnviarOEmailDeInatividadeComNomeDiasETelefone() {
            Revendedora revendedora = revendedora(1L, "11987654321");
            when(notificacaoEnvioService.registrarEnvio(any(), anyLong(), anyLong(), anyBoolean()))
                    .thenReturn(true);

            revendedoraNotificacaoService.notificarInatividade(revendedora, 75L);

            verify(notificacaoEnvioService).registrarEnvio(
                    TipoReferencia.REVENDEDORA, 1L, NotificacaoBrevoService.TEMPLATE_REVENDEDORA_INATIVA, true);
            verify(notificacaoBrevoService).notificacaoRevendedoraInativa("maria@email.com", "Maria", Map.of(
                    "nomeRevendedora", "Maria",
                    "diasInatividade", "75",
                    "telefoneRevendedora", "11987654321"));
        }

        @Test
        @DisplayName("Deve omitir o telefone dos parâmetros quando a revendedora não tiver um")
        void deveOmitirOTelefoneDosParametrosQuandoARevendedoraNaoTiverUm() {
            Revendedora revendedora = revendedora(1L, null);
            when(notificacaoEnvioService.registrarEnvio(any(), anyLong(), anyLong(), anyBoolean()))
                    .thenReturn(true);

            revendedoraNotificacaoService.notificarInatividade(revendedora, 75L);

            verify(notificacaoBrevoService).notificacaoRevendedoraInativa("maria@email.com", "Maria", Map.of(
                    "nomeRevendedora", "Maria",
                    "diasInatividade", "75"));
        }

        @Test
        @DisplayName("Deve não enviar o e-mail quando o registro do envio for recusado")
        void deveNaoEnviarOEmailDeInatividadeQuandoORegistroDoEnvioForRecusado() {
            Revendedora revendedora = revendedora(1L, "11987654321");
            when(notificacaoEnvioService.registrarEnvio(any(), anyLong(), anyLong(), anyBoolean()))
                    .thenReturn(false);

            revendedoraNotificacaoService.notificarInatividade(revendedora, 75L);

            verify(notificacaoBrevoService, never()).notificacaoRevendedoraInativa(any(), any(), any());
        }

        @Test
        @DisplayName("Deve não enviar o e-mail quando a revendedora for nula")
        void deveNaoEnviarOEmailDeInatividadeQuandoARevendedoraForNula() {
            revendedoraNotificacaoService.notificarInatividade(null, 75L);

            verify(notificacaoEnvioService, never()).registrarEnvio(any(), anyLong(), anyLong(), anyBoolean());
            verify(notificacaoBrevoService, never()).notificacaoRevendedoraInativa(any(), any(), any());
        }
    }

    private Revendedora revendedora(Long id, String telefone) {
        Revendedora revendedora = new Revendedora();
        revendedora.setId(id);
        revendedora.setNome("Maria");
        revendedora.setEmail("maria@email.com");
        revendedora.setTelefone(telefone);
        return revendedora;
    }
}
