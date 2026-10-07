package com.caeca.estoque.service.revendedora;

import com.caeca.estoque.database.model.movimentacaoRevendedora.MovimentacaoRevendedora;
import com.caeca.estoque.database.model.notificacaoEnvio.TipoReferencia;
import com.caeca.estoque.database.model.revendedora.Revendedora;
import com.caeca.estoque.database.repository.movimentacaoRevendedora.MovimentacaoRevendedoraRepository;
import com.caeca.estoque.database.repository.revendedora.RevendedoraRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RevendedoraRotinaDiariaServiceTest {

    private static final LocalDate HOJE = LocalDate.of(2026, 9, 15);

    @Mock
    private RevendedoraRepository revendedoraRepository;

    @Mock
    private RevendedoraNotificacaoService revendedoraNotificacaoService;

    @Mock
    private MovimentacaoRevendedoraRepository movimentacaoRevendedoraRepository;

    @InjectMocks
    private RevendedoraRotinaDiariaService revendedoraRotinaDiariaService;

    @Nested
    @DisplayName("nome")
    class nome {

        @Test
        @DisplayName("Deve retornar REVENDEDORA como identificador da rotina")
        void deveRetornarRevendedoraComoIdentificadorDaRotina() {
            assertEquals(TipoReferencia.REVENDEDORA, revendedoraRotinaDiariaService.nome());
        }
    }

    @Nested
    @DisplayName("notificarAniversariantes")
    class notificarAniversariantes {

        @Test
        @DisplayName("Deve notificar a revendedora que faz aniversário no dia informado")
        void deveNotificarARevendedoraQueFazAniversarioNoDiaInformado() {
            Revendedora aniversariante = revendedora(1L, null);
            when(revendedoraRepository.findAniversariantes(15, 9)).thenReturn(List.of(aniversariante));
            when(revendedoraRepository.findByAtivoTrue()).thenReturn(List.of());

            revendedoraRotinaDiariaService.executar(HOJE);

            verify(revendedoraNotificacaoService, times(1)).notificarAniversario(aniversariante);
        }

        @Test
        @DisplayName("Deve filtrar os aniversariantes pelo dia e mês da data de execução")
        void deveFiltrarOsAniversariantesPeloDiaEMesDaDataDeExecucao() {
            when(revendedoraRepository.findAniversariantes(15, 9)).thenReturn(List.of());
            when(revendedoraRepository.findByAtivoTrue()).thenReturn(List.of());

            revendedoraRotinaDiariaService.executar(HOJE);

            verify(revendedoraRepository).findAniversariantes(15, 9);
        }

        @Test
        @DisplayName("Deve não notificar quando ninguém faz aniversário no dia")
        void deveNaoNotificarQuandoNinguemFazAniversarioNoDia() {
            when(revendedoraRepository.findAniversariantes(15, 9)).thenReturn(List.of());
            when(revendedoraRepository.findByAtivoTrue()).thenReturn(List.of());

            revendedoraRotinaDiariaService.executar(HOJE);

            verify(revendedoraNotificacaoService, never()).notificarAniversario(any());
        }

        @Test
        @DisplayName("Deve notificar as demais revendedoras quando uma falhar ao enviar")
        void deveNotificarAsDemaisRevendedorasQuandoUmaFalharAoEnviar() {
            Revendedora primeira = revendedora(1L, null);
            Revendedora segunda = revendedora(2L, null);
            when(revendedoraRepository.findAniversariantes(15, 9)).thenReturn(List.of(primeira, segunda));
            when(revendedoraRepository.findByAtivoTrue()).thenReturn(List.of());
            doThrow(new RuntimeException("falha no envio"))
                    .when(revendedoraNotificacaoService).notificarAniversario(primeira);

            revendedoraRotinaDiariaService.executar(HOJE);

            verify(revendedoraNotificacaoService, times(1)).notificarAniversario(primeira);
            verify(revendedoraNotificacaoService, times(1)).notificarAniversario(segunda);
        }
    }

    @Nested
    @DisplayName("notificarRevendedorasInativas")
    class notificarRevendedorasInativas {

        @Test
        @DisplayName("Deve notificar inatividade quando a revendedora estiver sem atividade há 60 dias")
        void deveNotificarInatividadeQuandoARevendedoraEstiverSemAtividadeHa60Dias() {
            Revendedora revendedora = revendedora(1L, null);
            when(revendedoraRepository.findByAtivoTrue()).thenReturn(List.of(revendedora));
            when(movimentacaoRevendedoraRepository
                    .findFirstByRevendedoraIdOrderByIdMovimentacaoRevendedoraDesc(1L))
                    .thenReturn(venda(LocalDateTime.of(2026, 7, 17, 0, 0)));

            revendedoraRotinaDiariaService.executar(HOJE);

            verify(revendedoraNotificacaoService, times(1)).notificarInatividade(revendedora, 60L);
        }

        @Test
        @DisplayName("Deve não notificar inatividade quando a revendedora estiver sem atividade há menos de 60 dias")
        void deveNaoNotificarInatividadeQuandoEstiverSemAtividadeHaMenosDe60Dias() {
            Revendedora revendedora = revendedora(1L, null);
            when(revendedoraRepository.findByAtivoTrue()).thenReturn(List.of(revendedora));
            when(movimentacaoRevendedoraRepository
                    .findFirstByRevendedoraIdOrderByIdMovimentacaoRevendedoraDesc(1L))
                    .thenReturn(venda(LocalDateTime.of(2026, 7, 18, 0, 0)));

            revendedoraRotinaDiariaService.executar(HOJE);

            verify(revendedoraNotificacaoService, never())
                    .notificarInatividade(eq(revendedora), anyLong());
        }

        @Test
        @DisplayName("Deve usar a data de cadastro quando a revendedora não tiver nenhuma venda")
        void deveUsarADataDeCadastroQuandoARevendedoraNaoTiverNenhumaVenda() {
            Revendedora revendedora = revendedora(1L, LocalDate.of(2026, 7, 1));
            when(revendedoraRepository.findByAtivoTrue()).thenReturn(List.of(revendedora));
            when(movimentacaoRevendedoraRepository
                    .findFirstByRevendedoraIdOrderByIdMovimentacaoRevendedoraDesc(1L))
                    .thenReturn(null);

            revendedoraRotinaDiariaService.executar(HOJE);

            verify(revendedoraNotificacaoService, times(1)).notificarInatividade(revendedora, 76L);
        }

        @Test
        @DisplayName("Deve usar a data de cadastro quando a última venda não tiver data")
        void deveUsarADataDeCadastroQuandoAUltimaVendaNaoTiverData() {
            Revendedora revendedora = revendedora(1L, LocalDate.of(2026, 7, 1));
            when(revendedoraRepository.findByAtivoTrue()).thenReturn(List.of(revendedora));
            when(movimentacaoRevendedoraRepository
                    .findFirstByRevendedoraIdOrderByIdMovimentacaoRevendedoraDesc(1L))
                    .thenReturn(venda(null));

            revendedoraRotinaDiariaService.executar(HOJE);

            verify(revendedoraNotificacaoService, times(1)).notificarInatividade(revendedora, 76L);
        }

        @Test
        @DisplayName("Deve ignorar a revendedora sem venda e sem data de cadastro em vez de estourar")
        void deveIgnorarARevendedoraSemVendaNemDataDeCadastroEmVezDeEstourar() {
            Revendedora revendedora = revendedora(1L, null);
            when(revendedoraRepository.findByAtivoTrue()).thenReturn(List.of(revendedora));
            when(movimentacaoRevendedoraRepository
                    .findFirstByRevendedoraIdOrderByIdMovimentacaoRevendedoraDesc(1L))
                    .thenReturn(null);

            assertDoesNotThrow(() -> revendedoraRotinaDiariaService.executar(HOJE));

            verify(revendedoraNotificacaoService, never())
                    .notificarInatividade(eq(revendedora), anyLong());
        }

        @Test
        @DisplayName("Deve notificar as demais revendedoras quando uma falhar ao enviar")
        void deveNotificarAsDemaisRevendedorasQuandoUmaFalharAoEnviarInatividade() {
            Revendedora primeira = revendedora(1L, null);
            Revendedora segunda = revendedora(2L, null);
            when(revendedoraRepository.findByAtivoTrue()).thenReturn(List.of(primeira, segunda));
            when(movimentacaoRevendedoraRepository
                    .findFirstByRevendedoraIdOrderByIdMovimentacaoRevendedoraDesc(1L))
                    .thenReturn(venda(LocalDateTime.of(2026, 1, 1, 0, 0)));
            when(movimentacaoRevendedoraRepository
                    .findFirstByRevendedoraIdOrderByIdMovimentacaoRevendedoraDesc(2L))
                    .thenReturn(venda(LocalDateTime.of(2026, 1, 1, 0, 0)));
            doThrow(new RuntimeException("falha no envio"))
                    .when(revendedoraNotificacaoService).notificarInatividade(eq(primeira), anyLong());

            revendedoraRotinaDiariaService.executar(HOJE);

            verify(revendedoraNotificacaoService, times(1)).notificarInatividade(eq(primeira), anyLong());
            verify(revendedoraNotificacaoService, times(1)).notificarInatividade(eq(segunda), anyLong());
        }
    }

    private Revendedora revendedora(Long id, LocalDate dataCadastro) {
        Revendedora revendedora = new Revendedora();
        revendedora.setId(id);
        revendedora.setNome("Revendedora " + id);
        revendedora.setAtivo(true);
        revendedora.setDataCadastro(dataCadastro);
        return revendedora;
    }

    private MovimentacaoRevendedora venda(LocalDateTime data) {
        MovimentacaoRevendedora venda = new MovimentacaoRevendedora();
        venda.setData(data);
        return venda;
    }
}
