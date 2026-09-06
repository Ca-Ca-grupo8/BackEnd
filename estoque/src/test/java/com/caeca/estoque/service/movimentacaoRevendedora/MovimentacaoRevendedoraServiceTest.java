package com.caeca.estoque.service.movimentacaoRevendedora;

import com.caeca.estoque.database.model.movimentacaoRevendedora.MovimentacaoRevendedora;
import com.caeca.estoque.database.model.revendedora.Revendedora;
import com.caeca.estoque.database.repository.movimentacaoRevendedora.MovimentacaoRevendedoraRepository;
import com.caeca.estoque.database.repository.revendedora.RevendedoraRepository;
import com.caeca.estoque.dto.movimentacaoRevendedora.MovimentacaoRevendedoraPatchDto;
import com.caeca.estoque.dto.movimentacaoRevendedora.MovimentacaoRevendedoraRequestDto;
import com.caeca.estoque.exception.NotFoundException;
import com.caeca.estoque.service.paginacao.PaginacaoService;
import com.caeca.estoque.service.revendedora.RevendedoraService;
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
    private RevendedoraService revendedoraService;

    @Mock
    private RevendedoraRepository revendedoraRepository;

    @Mock
    private PaginacaoService paginacaoService;

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
            when(mrRepository.findByRevendedoraIdAndIsFinalizadaFalse(1L))
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

            when(mrRepository.findByRevendedoraIdAndIsFinalizadaFalse(1L))
                    .thenReturn(Optional.empty());
            when(revendedoraService.buscarPorId(1L)).thenReturn(revendedora);
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
            when(mrRepository.findByRevendedoraIdAndIsFinalizadaFalse(1L))
                    .thenReturn(Optional.of(movimentacao));

            MovimentacaoRevendedora resultado =
                    movimentacaoRevendedoraService.buscarVendaAbertaPorRevendedora(1L);

            assertNotNull(resultado);
            assertEquals(movimentacao, resultado);
        }

        @Test
        @DisplayName("Deve lançar NotFoundException quando não houver venda aberta para a revendedora")
        void deveLancarNotFoundExceptionQuandoNaoHouverVendaAberta() {
            when(mrRepository.findByRevendedoraIdAndIsFinalizadaFalse(1L))
                    .thenReturn(Optional.empty());

            assertThrows(NotFoundException.class,
                    () -> movimentacaoRevendedoraService.buscarVendaAbertaPorRevendedora(1L));
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
    @DisplayName("atualizarFinalizacao")
    class atualizarFinalizacao {

        @Test
        @DisplayName("Deve finalizar movimentação com sucesso quando encontrar pelo id")
        void deveFinalizarMovimentacaoComSucesso() {
            MovimentacaoRevendedora movimentacao = new MovimentacaoRevendedora();
            movimentacao.setFinalizada(false);

            MovimentacaoRevendedoraPatchDto patch = new MovimentacaoRevendedoraPatchDto(true);
            patch.setFinalizada(true);

            when(mrRepository.findById(1L)).thenReturn(Optional.of(movimentacao));
            when(mrRepository.save(any(MovimentacaoRevendedora.class))).thenReturn(movimentacao);

            MovimentacaoRevendedora resultado =
                    movimentacaoRevendedoraService.atualizarFinalizacao(1L, patch);

            assertNotNull(resultado);
            assertTrue(resultado.getIsFinalizada());
            verify(mrRepository, times(1)).save(movimentacao);
        }

        @Test
        @DisplayName("Deve lançar NotFoundException quando não encontrar movimentação pelo id")
        void deveLancarNotFoundExceptionQuandoNaoEncontrarMovimentacao() {
            when(mrRepository.findById(1L)).thenReturn(Optional.empty());

            assertThrows(NotFoundException.class,
                    () -> movimentacaoRevendedoraService.atualizarFinalizacao(1L,
                            new MovimentacaoRevendedoraPatchDto(false)));

            verify(mrRepository, never()).save(any(MovimentacaoRevendedora.class));
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