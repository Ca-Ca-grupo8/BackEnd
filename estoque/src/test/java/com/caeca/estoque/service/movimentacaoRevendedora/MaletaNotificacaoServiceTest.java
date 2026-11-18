package com.caeca.estoque.service.movimentacaoRevendedora;

import com.caeca.estoque.database.model.movimentacaoRevendedora.MovimentacaoRevendedora;
import com.caeca.estoque.database.model.notificacaoEnvio.TipoReferencia;
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
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MaletaNotificacaoServiceTest {

    @Mock
    private NotificacaoEnvioService notificacaoEnvioService;

    @Mock
    private NotificacaoBrevoService notificacaoBrevoService;

    @InjectMocks
    private MaletaNotificacaoService maletaNotificacaoService;

    private MovimentacaoRevendedora maleta(Long id) {
        MovimentacaoRevendedora maleta = new MovimentacaoRevendedora();
        maleta.setIdMovimentacaoRevendedora(id);

        return maleta;
    }

    @Nested
    @DisplayName("notificarLoteEmConsignacao")
    class notificarLoteEmConsignacao {

        @Test
        @DisplayName("Deve enviar o e-mail com comprovante quando o envio ainda nao foi registrado (sem reenvio)")
        void deveEnviarQuandoNaoRegistradoAnteriormente() {
            MovimentacaoRevendedora maleta = maleta(1L);
            byte[] comprovante = new byte[]{1, 2, 3};
            when(notificacaoEnvioService.registrarEnvio(TipoReferencia.MALETA, 1L,
                    NotificacaoBrevoService.TEMPLATE_LOTE_EM_CONSIGNACAO, false)).thenReturn(true);

            maletaNotificacaoService.notificarLoteEmConsignacao(maleta, comprovante);

            verify(notificacaoBrevoService, times(1)).notificacaoLoteEmConsignacao(maleta, comprovante);
        }

        @Test
        @DisplayName("Deve nao enviar o e-mail quando o registro de envio nao for gravado")
        void deveNaoEnviarQuandoRegistroFalhar() {
            MovimentacaoRevendedora maleta = maleta(1L);
            when(notificacaoEnvioService.registrarEnvio(TipoReferencia.MALETA, 1L,
                    NotificacaoBrevoService.TEMPLATE_LOTE_EM_CONSIGNACAO, false)).thenReturn(false);

            maletaNotificacaoService.notificarLoteEmConsignacao(maleta, null);

            verify(notificacaoBrevoService, never()).notificacaoLoteEmConsignacao(any(), any());
        }
    }

    @Nested
    @DisplayName("sincronizarContatoLoteEmConsignacao")
    class sincronizarContatoLoteEmConsignacao {

        @Test
        @DisplayName("Deve sincronizar o contato na lista do Brevo quando ainda nao foi registrado (com reenvio)")
        void deveSincronizarQuandoNaoRegistradoAnteriormente() {
            MovimentacaoRevendedora maleta = maleta(1L);
            when(notificacaoEnvioService.registrarEnvio(TipoReferencia.MALETA, 1L,
                    NotificacaoBrevoService.TEMPLATE_CONTATO_MALETA_EM_CONSIGNACAO, true)).thenReturn(true);

            maletaNotificacaoService.sincronizarContatoLoteEmConsignacao(maleta);

            verify(notificacaoBrevoService, times(1)).sincronizarContatoMaletaEmConsignacao(maleta);
        }

        @Test
        @DisplayName("Deve nao sincronizar quando o registro de envio nao for gravado")
        void deveNaoSincronizarQuandoRegistroFalhar() {
            MovimentacaoRevendedora maleta = maleta(1L);
            when(notificacaoEnvioService.registrarEnvio(TipoReferencia.MALETA, 1L,
                    NotificacaoBrevoService.TEMPLATE_CONTATO_MALETA_EM_CONSIGNACAO, true)).thenReturn(false);

            maletaNotificacaoService.sincronizarContatoLoteEmConsignacao(maleta);

            verify(notificacaoBrevoService, never()).sincronizarContatoMaletaEmConsignacao(any());
        }
    }

    @Nested
    @DisplayName("notificarFechamentoLote")
    class notificarFechamentoLote {

        @Test
        @DisplayName("Deve enviar o template de fechamento com os parametros quando o envio ainda nao foi registrado")
        void deveEnviarQuandoNaoRegistradoAnteriormente() {
            MovimentacaoRevendedora maleta = maleta(2L);
            Map<String, String> params = Map.of("mes", "janeiro de 2025");
            when(notificacaoEnvioService.registrarEnvio(TipoReferencia.MALETA, 2L,
                    NotificacaoBrevoService.TEMPLATE_META_ATINGIDA, true)).thenReturn(true);

            maletaNotificacaoService.notificarFechamentoLote(maleta, params, false);

            verify(notificacaoBrevoService, times(1)).notificacaoMetaAtingida(maleta, params, false);
        }

        @Test
        @DisplayName("Deve enviar o template de meta atingida quando a meta foi batida")
        void deveEnviarTemplateMetaAtingida() {
            MovimentacaoRevendedora maleta = maleta(2L);
            Map<String, String> params = Map.of("total", "1000");
            when(notificacaoEnvioService.registrarEnvio(TipoReferencia.MALETA, 2L,
                    NotificacaoBrevoService.TEMPLATE_META_ATINGIDA, true)).thenReturn(true);

            maletaNotificacaoService.notificarFechamentoLote(maleta, params, true);

            verify(notificacaoBrevoService, times(1)).notificacaoMetaAtingida(maleta, params, true);
        }

        @Test
        @DisplayName("Deve nao enviar quando o envio ja foi registrado")
        void deveNaoEnviarQuandoJaRegistrado() {
            MovimentacaoRevendedora maleta = maleta(2L);
            when(notificacaoEnvioService.registrarEnvio(TipoReferencia.MALETA, 2L,
                    NotificacaoBrevoService.TEMPLATE_META_ATINGIDA, true)).thenReturn(false);

            maletaNotificacaoService.notificarFechamentoLote(maleta, Map.of(), false);

            verify(notificacaoBrevoService, never()).notificacaoMetaAtingida(any(), anyMap(), anyBoolean());
        }
    }

    @Nested
    @DisplayName("registrarEnvio")
    class registrarEnvio {

        @Test
        @DisplayName("Deve nao notificar quando a gravacao do registro falhar")
        void deveNaoNotificarQuandoGravacaoFalhar() {
            MovimentacaoRevendedora maleta = maleta(5L);
            when(notificacaoEnvioService.registrarEnvio(any(), anyLong(), anyLong(), anyBoolean())).thenReturn(false);

            maletaNotificacaoService.notificarLoteEmConsignacao(maleta, null);
            maletaNotificacaoService.sincronizarContatoLoteEmConsignacao(maleta);

            verify(notificacaoBrevoService, never()).notificacaoLoteEmConsignacao(any(), any());
            verify(notificacaoBrevoService, never()).sincronizarContatoMaletaEmConsignacao(any());
        }
    }
}