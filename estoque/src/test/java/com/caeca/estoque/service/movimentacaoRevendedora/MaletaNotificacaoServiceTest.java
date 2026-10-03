package com.caeca.estoque.service.movimentacaoRevendedora;

import com.caeca.estoque.database.model.movimentacaoRevendedora.MovimentacaoRevendedora;
import com.caeca.estoque.database.repository.notificacaoEnvio.NotificacaoEnvioRepository;
import com.caeca.estoque.service.NotificacaoBrevo.NotificacaoBrevoService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MaletaNotificacaoServiceTest {

    @Mock
    private NotificacaoEnvioRepository notificacaoEnvioRepository;

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
            when(notificacaoEnvioRepository.existsByMaletaIdAndTemplateId(1L,
                    NotificacaoBrevoService.TEMPLATE_LOTE_EM_CONSIGNACAO)).thenReturn(false);

            maletaNotificacaoService.notificarLoteEmConsignacao(maleta);

            verify(notificacaoEnvioRepository, times(1)).save(any());
            verify(notificacaoBrevoService, times(1)).notificacaoLoteEmConsignacao(maleta);
        }

        @Test
        @DisplayName("Deve nao enviar o template 3 quando o envio ja foi registrado")
        void deveNaoEnviarQuandoJaRegistrado() {
            MovimentacaoRevendedora maleta = maleta(1L);
            when(notificacaoEnvioRepository.existsByMaletaIdAndTemplateId(1L,
                    NotificacaoBrevoService.TEMPLATE_LOTE_EM_CONSIGNACAO)).thenReturn(true);

            maletaNotificacaoService.notificarLoteEmConsignacao(maleta);

            verify(notificacaoEnvioRepository, never()).save(any());
            verify(notificacaoBrevoService, never()).notificacaoLoteEmConsignacao(any());
        }
    }

    @Nested
    @DisplayName("notificarFechamentoLote")
    class notificarFechamentoLote {

        @Test
        @DisplayName("Deve enviar o template 6 quando o envio ainda nao foi registrado")
        void deveEnviarQuandoNaoRegistradoAnteriormente() {
            MovimentacaoRevendedora maleta = maleta(2L);
            when(notificacaoEnvioRepository.existsByMaletaIdAndTemplateId(2L,
                    NotificacaoBrevoService.TEMPLATE_FECHAMENTO_LOTE)).thenReturn(false);

            maletaNotificacaoService.notificarFechamentoLote(maleta);

            verify(notificacaoBrevoService, times(1)).notificacaoFechamentoLote(maleta);
        }

        @Test
        @DisplayName("Deve nao enviar o template 6 quando o envio ja foi registrado")
        void deveNaoEnviarQuandoJaRegistrado() {
            MovimentacaoRevendedora maleta = maleta(2L);
            when(notificacaoEnvioRepository.existsByMaletaIdAndTemplateId(2L,
                    NotificacaoBrevoService.TEMPLATE_FECHAMENTO_LOTE)).thenReturn(true);

            maletaNotificacaoService.notificarFechamentoLote(maleta);

            verify(notificacaoBrevoService, never()).notificacaoFechamentoLote(any());
        }
    }

    @Nested
    @DisplayName("notificarAlertaVencimento")
    class notificarAlertaVencimento {

        @Test
        @DisplayName("Deve enviar o template 4 com os dias restantes quando nao foi registrado")
        void deveEnviarComDiasRestantes() {
            MovimentacaoRevendedora maleta = maleta(3L);
            when(notificacaoEnvioRepository.existsByMaletaIdAndTemplateId(3L,
                    NotificacaoBrevoService.TEMPLATE_ALERTA_VENCIMENTO)).thenReturn(false);

            maletaNotificacaoService.notificarAlertaVencimento(maleta, 7L);

            verify(notificacaoBrevoService, times(1)).notificacaoAlertaVencimento(maleta, 7L);
        }

        @Test
        @DisplayName("Deve nao enviar o template 4 quando ja foi registrado no mesmo limite")
        void deveNaoEnviarQuandoJaRegistrado() {
            MovimentacaoRevendedora maleta = maleta(3L);
            when(notificacaoEnvioRepository.existsByMaletaIdAndTemplateId(3L,
                    NotificacaoBrevoService.TEMPLATE_ALERTA_VENCIMENTO)).thenReturn(true);

            maletaNotificacaoService.notificarAlertaVencimento(maleta, 3L);

            verify(notificacaoBrevoService, never()).notificacaoAlertaVencimento(any(), any(Long.class));
        }
    }

    @Nested
    @DisplayName("notificarCobrancaAtraso")
    class notificarCobrancaAtraso {

        @Test
        @DisplayName("Deve enviar o template 5 na primeira detecção de atraso")
        void deveEnviarNaPrimeiraDeteccao() {
            MovimentacaoRevendedora maleta = maleta(4L);
            when(notificacaoEnvioRepository.existsByMaletaIdAndTemplateId(4L,
                    NotificacaoBrevoService.TEMPLATE_COBRANCA_ATRASO)).thenReturn(false);

            maletaNotificacaoService.notificarCobrancaAtraso(maleta);

            verify(notificacaoBrevoService, times(1)).notificacaoCobrancaAtraso(maleta);
        }

        @Test
        @DisplayName("Deve nao reenviar o template 5 enquanto o atraso persistir")
        void deveNaoReenviarEnquantoPersistir() {
            MovimentacaoRevendedora maleta = maleta(4L);
            when(notificacaoEnvioRepository.existsByMaletaIdAndTemplateId(4L,
                    NotificacaoBrevoService.TEMPLATE_COBRANCA_ATRASO)).thenReturn(true);

            maletaNotificacaoService.notificarCobrancaAtraso(maleta);
            maletaNotificacaoService.notificarCobrancaAtraso(maleta);

            verify(notificacaoBrevoService, never()).notificacaoCobrancaAtraso(any());
        }
    }

    @Nested
    @DisplayName("registrarEnvio")
    class registrarEnvio {

        @Test
        @DisplayName("Deve notificar mesmo quando a gravacao do registro falhar")
        void deveNotificarQuandoGravacaoFalhar() {
            MovimentacaoRevendedora maleta = maleta(5L);
            when(notificacaoEnvioRepository.existsByMaletaIdAndTemplateId(5L,
                    NotificacaoBrevoService.TEMPLATE_LOTE_EM_CONSIGNACAO)).thenReturn(false);
            doThrow(new RuntimeException("falha de banco"))
                    .when(notificacaoEnvioRepository).save(any());

            assertDoesNotThrow(() -> maletaNotificacaoService.notificarLoteEmConsignacao(maleta));

            verify(notificacaoBrevoService, times(1)).notificacaoLoteEmConsignacao(maleta);
        }
    }
}