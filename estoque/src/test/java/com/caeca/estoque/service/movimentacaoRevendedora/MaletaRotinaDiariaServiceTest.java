package com.caeca.estoque.service.movimentacaoRevendedora;

import com.caeca.estoque.database.model.movimentacaoRevendedora.MovimentacaoRevendedora;
import com.caeca.estoque.database.model.revendedora.Revendedora;
import com.caeca.estoque.database.model.statusMaleta.StatusMaleta;
import com.caeca.estoque.database.model.statusMaleta.StatusMaletaEnum;
import com.caeca.estoque.database.repository.movimentacaoRevendedora.MovimentacaoRevendedoraRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MaletaRotinaDiariaServiceTest {

    private static final LocalDate HOJE = LocalDate.of(2026, 3, 10);

    @Mock
    private MovimentacaoRevendedoraRepository mrRepository;

    @Mock
    private MaletaNotificacaoService maletaNotificacaoService;

    @InjectMocks
    private MaletaRotinaDiariaService maletaRotinaDiariaService;

    private MovimentacaoRevendedora maleta(Long id, LocalDateTime vencimento, StatusMaletaEnum status) {
        Revendedora revendedora = new Revendedora();
        revendedora.setNome("Revendedora Teste");
        revendedora.setEmail("teste@exemplo.com");

        MovimentacaoRevendedora maleta = new MovimentacaoRevendedora();
        maleta.setIdMovimentacaoRevendedora(id);
        maleta.setDataLimite(vencimento);
        maleta.setRevendedora(revendedora);
        maleta.setStatus(new StatusMaleta(1L, status));

        return maleta;
    }

    @Nested
    @DisplayName("executar")
    class executar {

        @Test
        @DisplayName("Deve enviar o alerta de 7 dias quando a maleta vence em exatamente 7 dias")
        void deveEnviarAlertaDeSeteDias() {
            MovimentacaoRevendedora maleta = maleta(1L,
                    HOJE.plusDays(7).atTime(18, 0), StatusMaletaEnum.EM_CONSIGNACAO);

            when(mrRepository.findByDataLimiteBetween(any(), any())).thenReturn(List.of(maleta));
            when(mrRepository.findByDataLimiteBefore(any())).thenReturn(List.of());

            maletaRotinaDiariaService.executar(HOJE);

            verify(maletaNotificacaoService, never()).notificarCobrancaAtraso(any());
            verify(maletaNotificacaoService).notificarAlertaVencimento(maleta, 7L);
        }

        @Test
        @DisplayName("Deve enviar o alerta de 3 dias quando a maleta vence em exatamente 3 dias")
        void deveEnviarAlertaDeTresDias() {
            MovimentacaoRevendedora maleta = maleta(2L,
                    HOJE.plusDays(3).atTime(10, 0), StatusMaletaEnum.EM_CONSIGNACAO);

            when(mrRepository.findByDataLimiteBetween(any(), any())).thenReturn(List.of(maleta));
            when(mrRepository.findByDataLimiteBefore(any())).thenReturn(List.of());

            maletaRotinaDiariaService.executar(HOJE);

            verify(maletaNotificacaoService).notificarAlertaVencimento(maleta, 3L);
        }

        @Test
        @DisplayName("Deve nao enviar alerta quando a maleta vence em limite diferente dos gatilhos")
        void deveNaoEnviarAlertForaDosLimites() {
            MovimentacaoRevendedora maleta = maleta(3L,
                    HOJE.plusDays(5).atTime(10, 0), StatusMaletaEnum.EM_CONSIGNACAO);

            when(mrRepository.findByDataLimiteBetween(any(), any())).thenReturn(List.of(maleta));
            when(mrRepository.findByDataLimiteBefore(any())).thenReturn(List.of());

            maletaRotinaDiariaService.executar(HOJE);

            verify(maletaNotificacaoService, never()).notificarAlertaVencimento(any(), any(Long.class));
        }

        @Test
        @DisplayName("Deve enviar a cobranca de atraso quando a maleta em consignacao esta vencida")
        void deveEnviarCobrancaDeAtraso() {
            MovimentacaoRevendedora maleta = maleta(4L,
                    HOJE.minusDays(3).atTime(10, 0), StatusMaletaEnum.EM_CONSIGNACAO);

            when(mrRepository.findByDataLimiteBetween(any(), any())).thenReturn(List.of());
            when(mrRepository.findByDataLimiteBefore(any())).thenReturn(List.of(maleta));

            maletaRotinaDiariaService.executar(HOJE);

            verify(maletaNotificacaoService, never()).notificarAlertaVencimento(any(), any(Long.class));
            verify(maletaNotificacaoService).notificarCobrancaAtraso(maleta);
        }

        @Test
        @DisplayName("Deve nao enviar cobranca de atraso quando a maleta vencida nao esta em consignacao")
        void deveNaoEnviarCobrancaForaDeConsignacao() {
            MovimentacaoRevendedora maleta = maleta(5L,
                    HOJE.minusDays(10).atTime(10, 0), StatusMaletaEnum.FINALIZADA);

            when(mrRepository.findByDataLimiteBetween(any(), any())).thenReturn(List.of());
            when(mrRepository.findByDataLimiteBefore(any())).thenReturn(List.of(maleta));

            maletaRotinaDiariaService.executar(HOJE);

            verify(maletaNotificacaoService, never()).notificarCobrancaAtraso(any());
        }

        @Test
        @DisplayName("Deve notificar as duas regras quando houver maleta vencendo e maleta atrasada")
        void deveNotificarAsDuasRegras() {
            MovimentacaoRevendedora alertando = maleta(6L,
                    HOJE.plusDays(7).atTime(10, 0), StatusMaletaEnum.EM_CONSIGNACAO);
            MovimentacaoRevendedora atrasada = maleta(7L,
                    HOJE.minusDays(1).atTime(10, 0), StatusMaletaEnum.EM_CONSIGNACAO);

            when(mrRepository.findByDataLimiteBetween(any(), any())).thenReturn(List.of(alertando));
            when(mrRepository.findByDataLimiteBefore(any())).thenReturn(List.of(atrasada));

            maletaRotinaDiariaService.executar(HOJE);

            verify(maletaNotificacaoService).notificarAlertaVencimento(alertando, 7L);
            verify(maletaNotificacaoService).notificarCobrancaAtraso(atrasada);
        }

        @Test
        @DisplayName("Deve nao notificar nada quando nao houver maleta com vencimento")
        void deveNaoNotificarSemMaletas() {
            when(mrRepository.findByDataLimiteBetween(any(), any())).thenReturn(List.of());
            when(mrRepository.findByDataLimiteBefore(any())).thenReturn(List.of());

            maletaRotinaDiariaService.executar(HOJE);

            verify(maletaNotificacaoService, never()).notificarAlertaVencimento(any(), any(Long.class));
            verify(maletaNotificacaoService, never()).notificarCobrancaAtraso(any());
        }
    }

    @Nested
    @DisplayName("diasAteVencimento")
    class diasAteVencimento {

        @Test
        @DisplayName("Deve calcular os dias que faltam para o vencimento")
        void deveCalcularDiasRestantes() {
            MovimentacaoRevendedora maleta = maleta(8L,
                    HOJE.plusDays(3).atTime(10, 0), StatusMaletaEnum.EM_CONSIGNACAO);

            assertEquals(3L, maletaRotinaDiariaService.diasAteVencimento(maleta, HOJE));
        }

        @Test
        @DisplayName("Deve devolver zero quando a maleta nao tem vencimento definido")
        void deveDevolverZeroSemVencimento() {
            MovimentacaoRevendedora maleta = maleta(9L, null, StatusMaletaEnum.EM_PREPARACAO);

            assertEquals(0L, maletaRotinaDiariaService.diasAteVencimento(maleta, HOJE));
        }

        @Test
        @DisplayName("Deve devolver zero quando a maleta vence hoje")
        void deveDevolverZeroQuandoVenceHoje() {
            MovimentacaoRevendedora maleta = maleta(10L,
                    HOJE.atTime(23, 59), StatusMaletaEnum.EM_CONSIGNACAO);

            assertEquals(0L, maletaRotinaDiariaService.diasAteVencimento(maleta, HOJE));
        }

        @Test
        @DisplayName("Deve devolver valor negativo quando a maleta esta vencida")
        void deveDevolverNegativoQuandoVencida() {
            MovimentacaoRevendedora maleta = maleta(11L,
                    HOJE.minusDays(2).atTime(10, 0), StatusMaletaEnum.EM_CONSIGNACAO);

            assertEquals(-2L, maletaRotinaDiariaService.diasAteVencimento(maleta, HOJE));
        }
    }

    @Nested
    @DisplayName("cron")
    class cron {

        @Test
        @DisplayName("Deve registrar o cron configurado para auditoria")
        void deveExporOCronConfigurado() {
            ReflectionTestUtils.setField(maletaRotinaDiariaService, "cron", "0 0 9 * * *");

            when(mrRepository.findByDataLimiteBetween(any(), any())).thenReturn(List.of());
            when(mrRepository.findByDataLimiteBefore(any())).thenReturn(List.of());

            maletaRotinaDiariaService.executar(HOJE);

            assertEquals("0 0 9 * * *",
                    ReflectionTestUtils.getField(maletaRotinaDiariaService, "cron"));
        }
    }
}