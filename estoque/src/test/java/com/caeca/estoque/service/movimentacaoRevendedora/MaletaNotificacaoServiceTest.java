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
        @DisplayName("Deve enviar o template 3 quando o envio ainda nao foi registrado")
        void deveEnviarQuandoNaoRegistradoAnteriormente() {
            MovimentacaoRevendedora maleta = maleta(1L);
            byte[] comprovante = new byte[]{1, 2, 3};
            when(notificacaoEnvioService.registrarEnvio(TipoReferencia.MALETA, 1L,
                    NotificacaoBrevoService.TEMPLATE_LOTE_EM_CONSIGNACAO, false)).thenReturn(true);

            maletaNotificacaoService.notificarLoteEmConsignacao(maleta, comprovante);

            verify(notificacaoEnvioService, times(1)).registrarEnvio(TipoReferencia.MALETA, 1L,
                    NotificacaoBrevoService.TEMPLATE_LOTE_EM_CONSIGNACAO, false);
            verify(notificacaoBrevoService, times(1)).notificacaoLoteEmConsignacao(maleta, comprovante);
        }

        @Test
        @DisplayName("Deve nao enviar o template 3 quando o envio ja foi registrado")
        void deveNaoEnviarQuandoJaRegistrado() {
            MovimentacaoRevendedora maleta = maleta(1L);
            when(notificacaoEnvioService.registrarEnvio(TipoReferencia.MALETA, 1L,
                    NotificacaoBrevoService.TEMPLATE_LOTE_EM_CONSIGNACAO, false)).thenReturn(false);

            maletaNotificacaoService.notificarLoteEmConsignacao(maleta, new byte[]{1, 2, 3});

            verify(notificacaoEnvioService, times(1)).registrarEnvio(TipoReferencia.MALETA, 1L,
                    NotificacaoBrevoService.TEMPLATE_LOTE_EM_CONSIGNACAO, false);
            verify(notificacaoBrevoService, never()).notificacaoLoteEmConsignacao(any(), any());
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
    @DisplayName("notificarAlertaVencimento")
    class notificarAlertaVencimento {

        @Test
        @DisplayName("Deve enviar o template 4 com os dias restantes quando nao foi registrado")
        void deveEnviarComDiasRestantes() {
            MovimentacaoRevendedora maleta = maleta(3L);
            when(notificacaoEnvioService.registrarEnvio(TipoReferencia.MALETA, 3L,
                    NotificacaoBrevoService.TEMPLATE_ALERTA_VENCIMENTO, false)).thenReturn(true);

            maletaNotificacaoService.notificarAlertaVencimento(maleta, 7L);

            verify(notificacaoBrevoService, times(1)).notificacaoAlertaVencimento(maleta, 7L);
        }

        @Test
        @DisplayName("Deve nao enviar o template 4 quando ja foi registrado no mesmo limite")
        void deveNaoEnviarQuandoJaRegistrado() {
            MovimentacaoRevendedora maleta = maleta(3L);
            when(notificacaoEnvioService.registrarEnvio(TipoReferencia.MALETA, 3L,
                    NotificacaoBrevoService.TEMPLATE_ALERTA_VENCIMENTO, false)).thenReturn(false);

            maletaNotificacaoService.notificarAlertaVencimento(maleta, 3L);

            verify(notificacaoBrevoService, never()).notificacaoAlertaVencimento(any(), anyLong());
        }
    }

    @Nested
    @DisplayName("notificarCobrancaAtraso")
    class notificarCobrancaAtraso {

        @Test
        @DisplayName("Deve enviar o template 5 na primeira deteccao de atraso")
        void deveEnviarNaPrimeiraDeteccao() {
            MovimentacaoRevendedora maleta = maleta(4L);
            when(notificacaoEnvioService.registrarEnvio(TipoReferencia.MALETA, 4L,
                    NotificacaoBrevoService.TEMPLATE_COBRANCA_ATRASO, false)).thenReturn(true, false);

            maletaNotificacaoService.notificarCobrancaAtraso(maleta);

            verify(notificacaoBrevoService, times(1)).notificacaoCobrancaAtraso(maleta);
        }

        @Test
        @DisplayName("Deve nao reenviar o template 5 enquanto o atraso persistir")
        void deveNaoReenviarEnquantoPersistir() {
            MovimentacaoRevendedora maleta = maleta(4L);
            when(notificacaoEnvioService.registrarEnvio(TipoReferencia.MALETA, 4L,
                    NotificacaoBrevoService.TEMPLATE_COBRANCA_ATRASO, false)).thenReturn(true, false);

            maletaNotificacaoService.notificarCobrancaAtraso(maleta);
            maletaNotificacaoService.notificarCobrancaAtraso(maleta);

            verify(notificacaoBrevoService, times(1)).notificacaoCobrancaAtraso(maleta);
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

            maletaNotificacaoService.notificarLoteEmConsignacao(maleta, new byte[]{1, 2, 3});

            verify(notificacaoBrevoService, never()).notificacaoLoteEmConsignacao(any(), any());
        }
    }
}