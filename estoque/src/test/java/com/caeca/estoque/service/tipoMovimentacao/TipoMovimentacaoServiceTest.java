package com.caeca.estoque.service.tipoMovimentacao;

import com.caeca.estoque.database.model.tipoMovimentacao.TipoMovimentacao;
import com.caeca.estoque.database.repository.tipoMovimentacao.TipoMovimentacaoRepository;
import com.caeca.estoque.dto.tipoMovimentacao.TipoMovimentacaoRequestDto;
import com.caeca.estoque.exception.NotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TipoMovimentacaoServiceTest {

    @Mock
    private TipoMovimentacaoRepository tipoMovimentacaoRepository;

    @InjectMocks
    private TipoMovimentacaoService tipoMovimentacaoService;

    @Nested
    @DisplayName("listar")
    class listar {

        @Test
        @DisplayName("Deve retornar lista de tipos de movimentação quando existirem dados")
        void deveRetornarListaQuandoExistiremDados() {
            when(tipoMovimentacaoRepository.findAll()).thenReturn(List.of(new TipoMovimentacao()));

            List<TipoMovimentacao> resultado = tipoMovimentacaoService.listar();

            assertNotNull(resultado);
            assertFalse(resultado.isEmpty());
            assertEquals(1, resultado.size());
            verify(tipoMovimentacaoRepository, times(1)).findAll();
        }

        @Test
        @DisplayName("Deve retornar lista vazia quando não existirem dados")
        void deveRetornarListaVaziaQuandoNaoExistiremDados() {
            when(tipoMovimentacaoRepository.findAll()).thenReturn(List.of());

            List<TipoMovimentacao> resultado = tipoMovimentacaoService.listar();

            assertNotNull(resultado);
            assertTrue(resultado.isEmpty());
            verify(tipoMovimentacaoRepository, times(1)).findAll();
        }
    }

    @Nested
    @DisplayName("buscarPorId")
    class buscarPorId {

        @Test
        @DisplayName("Deve retornar tipo de movimentação quando encontrar pelo id")
        void deveRetornarTipoMovimentacaoQuandoEncontrarPeloId() throws NotFoundException {
            TipoMovimentacao tipoMovimentacao = new TipoMovimentacao();
            when(tipoMovimentacaoRepository.findById(anyInt())).thenReturn(Optional.of(tipoMovimentacao));

            TipoMovimentacao resultado = tipoMovimentacaoService.buscarPorId(1);

            assertNotNull(resultado);
            assertEquals(tipoMovimentacao, resultado);
            verify(tipoMovimentacaoRepository, times(1)).findById(1);
        }

        @Test
        @DisplayName("Deve lançar NotFoundException quando não encontrar pelo id")
        void deveLancarNotFoundExceptionQuandoNaoEncontrarPeloId() {
            when(tipoMovimentacaoRepository.findById(anyInt())).thenReturn(Optional.empty());

            assertThrows(NotFoundException.class,
                    () -> tipoMovimentacaoService.buscarPorId(1));

            verify(tipoMovimentacaoRepository, times(1)).findById(1);
        }
    }

    @Nested
    @DisplayName("cadastrar")
    class cadastrar {

        @Test
        @DisplayName("Deve cadastrar tipo de movimentação com sucesso quando descrição não existir")
        void deveCadastrarComSucessoQuandoDescricaoNaoExistir() {
            TipoMovimentacaoRequestDto request = new TipoMovimentacaoRequestDto("Entrada");

            TipoMovimentacao tipoSalvo = new TipoMovimentacao();
            tipoSalvo.setDescricao("Entrada");

            when(tipoMovimentacaoRepository.existsByDescricaoIgnoreCase("Entrada")).thenReturn(false);
            when(tipoMovimentacaoRepository.save(any(TipoMovimentacao.class))).thenReturn(tipoSalvo);

            TipoMovimentacao resultado = tipoMovimentacaoService.cadastrar(request);

            assertNotNull(resultado);
            assertEquals("Entrada", resultado.getDescricao());
            verify(tipoMovimentacaoRepository, times(1)).existsByDescricaoIgnoreCase("Entrada");
            verify(tipoMovimentacaoRepository, times(1)).save(any(TipoMovimentacao.class));
        }

        @Test
        @DisplayName("Deve lançar ResponseStatusException 409 quando descrição já existir")
        void deveLancarResponseStatusExceptionQuandoDescricaoJaExistir() {
            TipoMovimentacaoRequestDto request = new TipoMovimentacaoRequestDto("Entrada");

            when(tipoMovimentacaoRepository.existsByDescricaoIgnoreCase("Entrada")).thenReturn(true);

            ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                    () -> tipoMovimentacaoService.cadastrar(request));

            assertEquals(409, exception.getStatusCode().value());
            verify(tipoMovimentacaoRepository, never()).save(any(TipoMovimentacao.class));
        }
    }
}