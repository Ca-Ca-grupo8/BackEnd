package com.caeca.estoque.service.motivoMovimentacao;

import com.caeca.estoque.database.model.motivoMovimentacao.MotivoMovimentacao;
import com.caeca.estoque.database.model.tipoMovimentacao.TipoMovimentacao;
import com.caeca.estoque.database.model.tipoMovimentacao.TipoMovimentacaoEnum;
import com.caeca.estoque.database.repository.motivoMovimentacao.MotivoMovimentacaoRepository;
import com.caeca.estoque.database.repository.tipoMovimentacao.TipoMovimentacaoRepository;
import com.caeca.estoque.dto.motivoMovimentacao.MotivoMovimentacaoRequestDto;
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
class MotivoMovimentacaoServiceTest {

    @Mock
    private MotivoMovimentacaoRepository motivoMovimentacaoRepository;

    @Mock
    private TipoMovimentacaoRepository tipoMovimentacaoRepository;

    @InjectMocks
    private MotivoMovimentacaoService motivoMovimentacaoService;

    @Nested
    @DisplayName("listar")
    class listar {

        @Test
        @DisplayName("Deve retornar lista de tipos de movimentação quando existirem dados")
        void deveRetornarListaQuandoExistiremDados() {
            when(motivoMovimentacaoRepository.findAll()).thenReturn(List.of(new MotivoMovimentacao()));

            List<MotivoMovimentacao> resultado = motivoMovimentacaoService.listar();

            assertNotNull(resultado);
            assertFalse(resultado.isEmpty());
            assertEquals(1, resultado.size());
            verify(motivoMovimentacaoRepository, times(1)).findAll();
        }

        @Test
        @DisplayName("Deve retornar lista vazia quando não existirem dados")
        void deveRetornarListaVaziaQuandoNaoExistiremDados() {
            when(motivoMovimentacaoRepository.findAll()).thenReturn(List.of());

            List<MotivoMovimentacao> resultado = motivoMovimentacaoService.listar();

            assertNotNull(resultado);
            assertTrue(resultado.isEmpty());
            verify(motivoMovimentacaoRepository, times(1)).findAll();
        }
    }

    @Nested
    @DisplayName("buscarPorId")
    class buscarPorId {

        @Test
        @DisplayName("Deve retornar tipo de movimentação quando encontrar pelo id")
        void deveRetornarTipoMovimentacaoQuandoEncontrarPeloId() throws NotFoundException {
            MotivoMovimentacao motivoMovimentacao = new MotivoMovimentacao();
            when(motivoMovimentacaoRepository.findById(anyInt())).thenReturn(Optional.of(motivoMovimentacao));

            MotivoMovimentacao resultado = motivoMovimentacaoService.buscarPorId(1);

            assertNotNull(resultado);
            assertEquals(motivoMovimentacao, resultado);
            verify(motivoMovimentacaoRepository, times(1)).findById(1);
        }

        @Test
        @DisplayName("Deve lançar NotFoundException quando não encontrar pelo id")
        void deveLancarNotFoundExceptionQuandoNaoEncontrarPeloId() {
            when(motivoMovimentacaoRepository.findById(anyInt())).thenReturn(Optional.empty());

            assertThrows(NotFoundException.class,
                    () -> motivoMovimentacaoService.buscarPorId(1));

            verify(motivoMovimentacaoRepository, times(1)).findById(1);
        }
    }

    @Nested
    @DisplayName("cadastrar")
    class cadastrar {

        @Test
        @DisplayName("Deve cadastrar tipo de movimentação com sucesso quando descrição não existir")
        void deveCadastrarComSucessoQuandoDescricaoNaoExistir() {
            MotivoMovimentacaoRequestDto request = new MotivoMovimentacaoRequestDto("Entrada", 1L);

            MotivoMovimentacao tipoSalvo = new MotivoMovimentacao();
            tipoSalvo.setDescricao("Entrada");

            when(motivoMovimentacaoRepository.existsByDescricaoIgnoreCase("Entrada")).thenReturn(false);

            when(tipoMovimentacaoRepository.findById(anyLong())).thenReturn(Optional.of(new TipoMovimentacao(1L, TipoMovimentacaoEnum.ENTRADA)));

            when(motivoMovimentacaoRepository.save(any(MotivoMovimentacao.class))).thenReturn(tipoSalvo);

            MotivoMovimentacao resultado = motivoMovimentacaoService.cadastrar(request);

            assertNotNull(resultado);
            assertEquals("Entrada", resultado.getDescricao());
            verify(motivoMovimentacaoRepository, times(1)).existsByDescricaoIgnoreCase("Entrada");
            verify(motivoMovimentacaoRepository, times(1)).save(any(MotivoMovimentacao.class));
        }

        @Test
        @DisplayName("Deve lançar excessão quando id não existir")
        void deveLancarExcessaoQuandoIdNaoExistir() {
            MotivoMovimentacaoRequestDto request = new MotivoMovimentacaoRequestDto("Entrada", 1L);

            MotivoMovimentacao tipoSalvo = new MotivoMovimentacao();
            tipoSalvo.setDescricao("Entrada");

            when(motivoMovimentacaoRepository.existsByDescricaoIgnoreCase("Entrada")).thenReturn(false);

            when(tipoMovimentacaoRepository.findById(anyLong())).thenReturn(Optional.empty());

            assertThrows(NotFoundException.class,
                    () -> motivoMovimentacaoService.cadastrar(request));
        }

        @Test
        @DisplayName("Deve lançar ResponseStatusException 409 quando descrição já existir")
        void deveLancarResponseStatusExceptionQuandoDescricaoJaExistir() {
            MotivoMovimentacaoRequestDto request = new MotivoMovimentacaoRequestDto("Entrada", 1L);

            when(motivoMovimentacaoRepository.existsByDescricaoIgnoreCase("Entrada")).thenReturn(true);

            ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                    () -> motivoMovimentacaoService.cadastrar(request));

            assertEquals(409, exception.getStatusCode().value());
            verify(motivoMovimentacaoRepository, never()).save(any(MotivoMovimentacao.class));
        }
    }
}