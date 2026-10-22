package com.caeca.estoque.service.movimentacaoRevendedora;

import com.caeca.estoque.database.model.movimentacaoRevendedora.MovimentacaoRevendedora;
import com.caeca.estoque.database.model.revendedora.Revendedora;
import com.caeca.estoque.database.model.statusMaleta.StatusMaleta;
import com.caeca.estoque.database.model.statusMaleta.StatusMaletaEnum;
import com.caeca.estoque.database.repository.movimentacao.MovimentacaoRepository;
import com.caeca.estoque.database.repository.movimentacaoRevendedora.MovimentacaoRevendedoraRepository;
import com.caeca.estoque.database.repository.revendedora.RevendedoraRepository;
import com.caeca.estoque.dto.movimentacaoRevendedora.JoiaSaidaMaletaDto;
import com.caeca.estoque.dto.movimentacaoRevendedora.MovimentacaoRevendedoraRequestDto;
import com.caeca.estoque.dto.movimentacaoRevendedora.MovimentacaoRevendedoraStatusPatchDto;
import com.caeca.estoque.exception.NotFoundException;
import com.caeca.estoque.service.revendedora.RevendedoraService;
import com.caeca.estoque.service.statusMaleta.StatusMaletaService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MovimentacaoRevendedoraServiceTest {

    @Mock
    private MovimentacaoRevendedoraRepository mrRepository;

    @Mock
    private MovimentacaoRepository movimentacaoRepository;

    @Mock
    private RevendedoraService revendedoraService;

    @Mock
    private RevendedoraRepository revendedoraRepository;

    @Mock
    private StatusMaletaService statusMaletaService;

    @Mock
    private MaletaNotificacaoService maletaNotificacaoService;

    @Mock
    private MaletaMetaService maletaMetaService;

    @InjectMocks
    private MovimentacaoRevendedoraService movimentacaoRevendedoraService;

    @Nested
    @DisplayName("listar")
    class listar {

        @Test
        @DisplayName("Deve retornar lista de movimentações com sucesso quando existirem dados")
        void deveRetornarListaQuandoExistiremDados() {
            when(mrRepository.findAll()).thenReturn(List.of(new MovimentacaoRevendedora()));

            List<MovimentacaoRevendedora> resultado = movimentacaoRevendedoraService.listar();

            assertNotNull(resultado);
            assertFalse(resultado.isEmpty());
            assertEquals(1, resultado.size());
            verify(mrRepository, times(1)).findAll();
        }

        @Test
        @DisplayName("Deve retornar lista vazia quando não existirem dados")
        void deveRetornarListaVaziaQuandoNaoExistiremDados() {
            when(mrRepository.findAll()).thenReturn(List.of());

            List<MovimentacaoRevendedora> resultado = movimentacaoRevendedoraService.listar();

            assertNotNull(resultado);
            assertTrue(resultado.isEmpty());
            verify(mrRepository, times(1)).findAll();
        }
    }

    @Nested
    @DisplayName("buscarPorId")
    class buscarPorId {

        @Test
        @DisplayName("Deve retornar movimentação quando encontrar pelo id")
        void deveRetornarMovimentacaoQuandoEncontrarPeloId() {
            MovimentacaoRevendedora movimentacao = new MovimentacaoRevendedora();
            when(mrRepository.findById(anyLong())).thenReturn(Optional.of(movimentacao));

            MovimentacaoRevendedora resultado = movimentacaoRevendedoraService.buscarPorId(1L);

            assertNotNull(resultado);
            assertEquals(movimentacao, resultado);
            verify(mrRepository, times(1)).findById(1L);
        }

        @Test
        @DisplayName("Deve lançar NotFoundException quando não encontrar pelo id")
        void deveLancarNotFoundExceptionQuandoNaoEncontrarPeloId() {
            when(mrRepository.findById(anyLong())).thenReturn(Optional.empty());

            assertThrows(NotFoundException.class,
                    () -> movimentacaoRevendedoraService.buscarPorId(1L));

            verify(mrRepository, times(1)).findById(1L);
        }
    }

    @Nested
    @DisplayName("buscarOuCriarVendaPorRevendedora")
    class buscarOuCriarVendaPorRevendedora {

        @Test
        @DisplayName("Deve retornar movimentação existente quando já houver venda aberta para a revendedora")
        void deveRetornarMovimentacaoExistenteQuandoHouverVendaAberta() {
            MovimentacaoRevendedoraRequestDto requestDto = new MovimentacaoRevendedoraRequestDto(1L);
            requestDto.setIdRevendedora(1L);

            MovimentacaoRevendedora movimentacaoExistente = new MovimentacaoRevendedora();
            when(mrRepository.findByRevendedoraIdAndStatusNome(1L, StatusMaletaEnum.EM_PREPARACAO))
                    .thenReturn(Optional.of(movimentacaoExistente));

            MovimentacaoRevendedora resultado =
                    movimentacaoRevendedoraService.buscarOuCriarVendaPorRevendedora(requestDto);

            assertNotNull(resultado);
            assertEquals(movimentacaoExistente, resultado);
            verify(mrRepository, never()).save(any(MovimentacaoRevendedora.class));
        }

        @Test
        @DisplayName("Deve criar nova movimentação quando não houver venda aberta para a revendedora")
        void deveCriarNovaMovimentacaoQuandoNaoHouverVendaAberta() {
            MovimentacaoRevendedoraRequestDto requestDto = new MovimentacaoRevendedoraRequestDto(1L);
            requestDto.setIdRevendedora(1L);

            Revendedora revendedora = new Revendedora();
            MovimentacaoRevendedora novaMovimentacao = new MovimentacaoRevendedora();

            when(mrRepository.findByRevendedoraIdAndStatusNome(1L, StatusMaletaEnum.EM_PREPARACAO))
                    .thenReturn(Optional.empty());
            when(revendedoraService.buscarPorId(1L)).thenReturn(revendedora);
            when(statusMaletaService.buscarPorNome(StatusMaletaEnum.EM_PREPARACAO))
                    .thenReturn(new StatusMaleta(1L, StatusMaletaEnum.EM_PREPARACAO));
            when(mrRepository.save(any(MovimentacaoRevendedora.class))).thenReturn(novaMovimentacao);

            MovimentacaoRevendedora resultado =
                    movimentacaoRevendedoraService.buscarOuCriarVendaPorRevendedora(requestDto);

            assertNotNull(resultado);
            verify(revendedoraService, times(1)).buscarPorId(1L);
            verify(mrRepository, times(1)).save(any(MovimentacaoRevendedora.class));
        }
    }

    @Nested
    @DisplayName("buscarVendaAbertaPorRevendedora")
    class buscarVendaAbertaPorRevendedora {

        @Test
        @DisplayName("Deve retornar movimentação quando encontrar venda aberta para a revendedora")
        void deveRetornarMovimentacaoQuandoEncontrarVendaAberta() {
            MovimentacaoRevendedora movimentacao = new MovimentacaoRevendedora();
            when(mrRepository.findByRevendedoraIdAndStatusNome(1L, StatusMaletaEnum.EM_PREPARACAO))
                    .thenReturn(Optional.of(movimentacao));

            MovimentacaoRevendedora resultado =
                    movimentacaoRevendedoraService.buscarVendaAbertaPorRevendedora(1L);

            assertNotNull(resultado);
            assertEquals(movimentacao, resultado);
        }

        @Test
        @DisplayName("Deve lançar NotFoundException quando não houver venda aberta para a revendedora")
        void deveLancarNotFoundExceptionQuandoNaoHouverVendaAberta() {
            when(mrRepository.findByRevendedoraIdAndStatusNome(1L, StatusMaletaEnum.EM_PREPARACAO))
                    .thenReturn(Optional.empty());

            assertThrows(NotFoundException.class,
                    () -> movimentacaoRevendedoraService.buscarVendaAbertaPorRevendedora(1L));
        }
    }

    @Nested
    @DisplayName("buscarVendaAtualPorRevendedora")
    class buscarVendaAtualPorRevendedora {

        @Test
        @DisplayName("Deve retornar a venda mais recente que não esteja finalizada ou com erro")
        void deveRetornarVendaAtualQuandoExistir() {
            MovimentacaoRevendedora movimentacao = new MovimentacaoRevendedora();
            when(mrRepository.findFirstByRevendedoraIdAndStatusNomeNotInOrderByIdMovimentacaoRevendedoraDesc(
                    1L, List.of(StatusMaletaEnum.FINALIZADA, StatusMaletaEnum.ERRO)))
                    .thenReturn(Optional.of(movimentacao));

            MovimentacaoRevendedora resultado =
                    movimentacaoRevendedoraService.buscarVendaAtualPorRevendedora(1L);

            assertNotNull(resultado);
            assertEquals(movimentacao, resultado);
        }

        @Test
        @DisplayName("Deve lançar NotFoundException quando todas as vendas estiverem finalizadas ou com erro")
        void deveLancarNotFoundExceptionQuandoNaoHouverVendaAtual() {
            when(mrRepository.findFirstByRevendedoraIdAndStatusNomeNotInOrderByIdMovimentacaoRevendedoraDesc(
                    1L, List.of(StatusMaletaEnum.FINALIZADA, StatusMaletaEnum.ERRO)))
                    .thenReturn(Optional.empty());

            assertThrows(NotFoundException.class,
                    () -> movimentacaoRevendedoraService.buscarVendaAtualPorRevendedora(1L));
        }
    }

    @Nested
    @DisplayName("listarPorIdRevendedora")
    class listarPorIdRevendedora {

        @Test
        @DisplayName("Deve retornar lista de movimentações da revendedora quando existirem dados")
        void deveRetornarListaQuandoExistiremMovimentacoes() {
            when(mrRepository.findByRevendedoraId(1L))
                    .thenReturn(List.of(new MovimentacaoRevendedora(), new MovimentacaoRevendedora()));

            List<MovimentacaoRevendedora> resultado =
                    movimentacaoRevendedoraService.listarPorIdRevendedora(1L);

            assertNotNull(resultado);
            assertEquals(2, resultado.size());
            verify(mrRepository, times(1)).findByRevendedoraId(1L);
        }

        @Test
        @DisplayName("Deve retornar lista vazia quando não existirem movimentações para a revendedora")
        void deveRetornarListaVaziaQuandoNaoExistiremMovimentacoes() {
            when(mrRepository.findByRevendedoraId(1L)).thenReturn(List.of());

            List<MovimentacaoRevendedora> resultado =
                    movimentacaoRevendedoraService.listarPorIdRevendedora(1L);

            assertNotNull(resultado);
            assertTrue(resultado.isEmpty());
        }
    }

    @Nested
    @DisplayName("listarJoiasComSaidaPorRevendedora")
    class listarJoiasComSaidaPorRevendedora {

        @Test
        @DisplayName("Deve retornar joias com saída quando existir venda atual para a revendedora")
        void deveRetornarJoiasComSaidaQuandoExistirVendaAtual() {
            MovimentacaoRevendedora maleta = new MovimentacaoRevendedora();
            maleta.setIdMovimentacaoRevendedora(7L);

            List<JoiaSaidaMaletaDto> joias = List.of(
                    new JoiaSaidaMaletaDto(1L, "Colar prata", 3L),
                    new JoiaSaidaMaletaDto(2L, "Bracelete ouro", 1L));

            when(mrRepository.findFirstByRevendedoraIdAndStatusNomeNotInOrderByIdMovimentacaoRevendedoraDesc(
                    1L, List.of(StatusMaletaEnum.FINALIZADA, StatusMaletaEnum.ERRO)))
                    .thenReturn(Optional.of(maleta));
            when(movimentacaoRepository.buscarJoiasComSaidaNaMaleta(7L)).thenReturn(joias);

            List<JoiaSaidaMaletaDto> resultado =
                    movimentacaoRevendedoraService.listarJoiasComSaidaPorRevendedora(1L);

            assertNotNull(resultado);
            assertEquals(2, resultado.size());
            assertEquals("Colar prata", resultado.get(0).getNome());
            assertEquals(3L, resultado.get(0).getQuantidade());
            verify(movimentacaoRepository, times(1)).buscarJoiasComSaidaNaMaleta(7L);
        }

        @Test
        @DisplayName("Deve retornar lista vazia quando a maleta atual não tiver saídas")
        void deveRetornarListaVaziaQuandoMaletaNaoTiverSaidas() {
            MovimentacaoRevendedora maleta = new MovimentacaoRevendedora();
            maleta.setIdMovimentacaoRevendedora(7L);

            when(mrRepository.findFirstByRevendedoraIdAndStatusNomeNotInOrderByIdMovimentacaoRevendedoraDesc(
                    1L, List.of(StatusMaletaEnum.FINALIZADA, StatusMaletaEnum.ERRO)))
                    .thenReturn(Optional.of(maleta));
            when(movimentacaoRepository.buscarJoiasComSaidaNaMaleta(7L)).thenReturn(List.of());

            List<JoiaSaidaMaletaDto> resultado =
                    movimentacaoRevendedoraService.listarJoiasComSaidaPorRevendedora(1L);

            assertNotNull(resultado);
            assertTrue(resultado.isEmpty());
        }

        @Test
        @DisplayName("Deve lançar NotFoundException quando não houver venda atual para a revendedora")
        void deveLancarNotFoundExceptionQuandoNaoHouverVendaAtual() {
            when(mrRepository.findFirstByRevendedoraIdAndStatusNomeNotInOrderByIdMovimentacaoRevendedoraDesc(
                    1L, List.of(StatusMaletaEnum.FINALIZADA, StatusMaletaEnum.ERRO)))
                    .thenReturn(Optional.empty());

            assertThrows(NotFoundException.class,
                    () -> movimentacaoRevendedoraService.listarJoiasComSaidaPorRevendedora(1L));

            verify(movimentacaoRepository, never()).buscarJoiasComSaidaNaMaleta(anyLong());
        }
    }

    @Nested
    @DisplayName("atualizarStatus")
    class atualizarStatus {

        @Test
        @DisplayName("Deve atualizar o status da maleta com sucesso quando encontrar pelo id")
        void deveAtualizarStatusComSucesso() {
            MovimentacaoRevendedora movimentacao = new MovimentacaoRevendedora();
            movimentacao.setStatus(new StatusMaleta(1L, StatusMaletaEnum.EM_PREPARACAO));

            MovimentacaoRevendedoraStatusPatchDto patch =
                    new MovimentacaoRevendedoraStatusPatchDto(2L);

            StatusMaleta finalizada = new StatusMaleta(2L, StatusMaletaEnum.FINALIZADA);

            when(mrRepository.findById(1L)).thenReturn(Optional.of(movimentacao));
            when(statusMaletaService.buscarPorId(2L)).thenReturn(finalizada);
            when(mrRepository.save(any(MovimentacaoRevendedora.class))).thenReturn(movimentacao);

            MovimentacaoRevendedora resultado =
                    movimentacaoRevendedoraService.atualizarStatus(1L, patch);

            assertNotNull(resultado);
            assertEquals(finalizada, resultado.getStatus());
            verify(statusMaletaService, times(1)).buscarPorId(2L);
            verify(mrRepository, times(1)).save(movimentacao);
        }

        @Test
        @DisplayName("Deve lançar NotFoundException quando não encontrar movimentação pelo id")
        void deveLancarNotFoundExceptionQuandoNaoEncontrarMovimentacao() {
            when(mrRepository.findById(1L)).thenReturn(Optional.empty());

            assertThrows(NotFoundException.class,
                    () -> movimentacaoRevendedoraService.atualizarStatus(1L,
                            new MovimentacaoRevendedoraStatusPatchDto(2L)));

            verify(mrRepository, never()).save(any(MovimentacaoRevendedora.class));
        }

        @Test
        @DisplayName("Deve notificar saida para consignacao quando o status novo for Em Consignacao")
        void deveNotificarLoteEmConsignacao() {
            MovimentacaoRevendedora movimentacao = new MovimentacaoRevendedora();
            movimentacao.setIdMovimentacaoRevendedora(7L);
            movimentacao.setStatus(new StatusMaleta(1L, StatusMaletaEnum.EM_PREPARACAO));

            StatusMaleta emConsignacao = new StatusMaleta(2L, StatusMaletaEnum.EM_CONSIGNACAO);

            when(mrRepository.findById(1L)).thenReturn(Optional.of(movimentacao));
            when(statusMaletaService.buscarPorId(2L)).thenReturn(emConsignacao);
            when(mrRepository.save(any(MovimentacaoRevendedora.class))).thenReturn(movimentacao);

            movimentacaoRevendedoraService.atualizarStatus(1L,
                    new MovimentacaoRevendedoraStatusPatchDto(2L));

            verify(maletaNotificacaoService, times(1)).notificarLoteEmConsignacao(movimentacao);
            verify(maletaNotificacaoService, never()).notificarFechamentoLote(any(), anyMap(), anyBoolean());
        }

        @Test
        @DisplayName("Deve notificar fechamento do lote quando o status novo for Finalizada")
        void deveNotificarFechamentoLote() {
            MovimentacaoRevendedora movimentacao = new MovimentacaoRevendedora();
            movimentacao.setIdMovimentacaoRevendedora(8L);
            movimentacao.setStatus(new StatusMaleta(2L, StatusMaletaEnum.EM_CONSIGNACAO));

            StatusMaleta finalizada = new StatusMaleta(3L, StatusMaletaEnum.FINALIZADA);

            when(mrRepository.findById(1L)).thenReturn(Optional.of(movimentacao));
            when(statusMaletaService.buscarPorId(3L)).thenReturn(finalizada);
            when(mrRepository.save(any(MovimentacaoRevendedora.class))).thenReturn(movimentacao);

            movimentacaoRevendedoraService.atualizarStatus(1L,
                    new MovimentacaoRevendedoraStatusPatchDto(3L));

            verify(maletaNotificacaoService, times(1)).notificarFechamentoLote(eq(movimentacao), any(), anyBoolean());
            verify(maletaNotificacaoService, never()).notificarLoteEmConsignacao(any());
        }

        @Test
        @DisplayName("Deve nao notificar quando o status novo nao tiver regra de evento")
        void deveNaoNotificarQuandoStatusNaoTiverRegra() {
            MovimentacaoRevendedora movimentacao = new MovimentacaoRevendedora();
            movimentacao.setIdMovimentacaoRevendedora(9L);
            movimentacao.setStatus(new StatusMaleta(1L, StatusMaletaEnum.EM_PREPARACAO));

            StatusMaleta emAcerto = new StatusMaleta(4L, StatusMaletaEnum.EM_ACERTO);

            when(mrRepository.findById(1L)).thenReturn(Optional.of(movimentacao));
            when(statusMaletaService.buscarPorId(4L)).thenReturn(emAcerto);
            when(mrRepository.save(any(MovimentacaoRevendedora.class))).thenReturn(movimentacao);

            movimentacaoRevendedoraService.atualizarStatus(1L,
                    new MovimentacaoRevendedoraStatusPatchDto(4L));

            verify(maletaNotificacaoService, never()).notificarLoteEmConsignacao(any());
            verify(maletaNotificacaoService, never()).notificarFechamentoLote(any(), anyMap(), anyBoolean());
        }
    }

    @Nested
    @DisplayName("criar")
    class criar {

        @Test
        @DisplayName("Deve criar movimentação com sucesso e retornar entidade salva")
        void deveCriarMovimentacaoComSucesso() {
            MovimentacaoRevendedoraRequestDto dto = new MovimentacaoRevendedoraRequestDto(1L);
            dto.setIdRevendedora(1L);

            Revendedora revendedora = new Revendedora();
            MovimentacaoRevendedora movimentacaoSalva = new MovimentacaoRevendedora();

            when(revendedoraService.buscarPorId(1L)).thenReturn(revendedora);
            when(statusMaletaService.buscarPorNome(StatusMaletaEnum.EM_PREPARACAO))
                    .thenReturn(new StatusMaleta(1L, StatusMaletaEnum.EM_PREPARACAO));
            when(mrRepository.save(any(MovimentacaoRevendedora.class))).thenReturn(movimentacaoSalva);

            MovimentacaoRevendedora resultado = movimentacaoRevendedoraService.criar(dto);

            assertNotNull(resultado);
            verify(revendedoraService, times(1)).buscarPorId(1L);
            verify(mrRepository, times(1)).save(any(MovimentacaoRevendedora.class));
        }

        @Test
        @DisplayName("Deve lançar NotFoundException quando a revendedora não existir")
        void deveLancarNotFoundExceptionQuandoRevendedoraNaoExistir() {
            MovimentacaoRevendedoraRequestDto dto = new MovimentacaoRevendedoraRequestDto(1L);
            dto.setIdRevendedora(99L);

            when(revendedoraService.buscarPorId(99L))
                    .thenThrow(new NotFoundException("Revendedora não encontrada"));

            assertThrows(NotFoundException.class,
                    () -> movimentacaoRevendedoraService.criar(dto));

            verify(mrRepository, never()).save(any(MovimentacaoRevendedora.class));
        }
    }
}