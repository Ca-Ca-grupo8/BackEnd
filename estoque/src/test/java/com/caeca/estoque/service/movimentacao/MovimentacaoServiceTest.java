package com.caeca.estoque.service.movimentacao;

import com.caeca.estoque.database.model.movimentacao.Movimentacao;
import com.caeca.estoque.database.model.movimentacaoRevendedora.MovimentacaoRevendedora;
import com.caeca.estoque.database.model.motivoMovimentacao.MotivoMovimentacao;
import com.caeca.estoque.database.repository.movimentacao.MovimentacaoRepository;
import com.caeca.estoque.dto.movimentacao.MovimentacaoContextDto;
import com.caeca.estoque.dto.movimentacao.MovimentacaoRequestDto;
import com.caeca.estoque.exception.BadRequestException;
import com.caeca.estoque.exception.NotFoundException;
import com.caeca.estoque.service.joia.JoiaService;
import com.caeca.estoque.service.motivoMovimentacao.MotivoMovimentacaoService;
import com.caeca.estoque.service.usuario.UsuarioService;
import com.caeca.estoque.strategy.movimentacao.MovimentacaoStrategy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MovimentacaoServiceTest {

    @Mock private MovimentacaoRepository movimentacaoRepository;
    @Mock private MotivoMovimentacaoService tipoService;
    @Mock private JoiaService joiaService;
    @Mock private UsuarioService usuarioService;
    @Mock private MovimentacaoStrategy movimentacaoStrategy;

    private MovimentacaoService movimentacaoService;

    @BeforeEach
    void setUp() {
        movimentacaoService = new MovimentacaoService(
                List.of(movimentacaoStrategy),
                movimentacaoRepository,
                tipoService,
                joiaService,
                usuarioService
        );
    }

    @Nested
    @DisplayName("listar")
    class listar {

        @Test
        @DisplayName("Deve retornar lista de movimentações com sucesso quando existirem dados")
        void deveRetornarListaDeMovimentacoesComSucesso() {
            when(movimentacaoRepository.buscarComFiltros(null, null, null, null)).thenReturn(List.of(new Movimentacao()));

            List<Movimentacao> resultado = movimentacaoService.listar(null, null, null, null);

            assertNotNull(resultado);
            assertFalse(resultado.isEmpty());
            assertEquals(1, resultado.size());
            verify(movimentacaoRepository, times(1)).buscarComFiltros(null, null, null, null);
        }

        @Test
        @DisplayName("Deve retornar lista vazia quando não existirem movimentações")
        void deveRetornarListaVaziaQuandoNaoExistiremMovimentacoes() {
            when(movimentacaoRepository.buscarComFiltros(null, null, null, null)).thenReturn(List.of());

            List<Movimentacao> resultado = movimentacaoService.listar(null, null, null, null);

            assertNotNull(resultado);
            assertTrue(resultado.isEmpty());
            verify(movimentacaoRepository, times(1)).buscarComFiltros(null, null, null, null);
        }
    }

    @Nested
    @DisplayName("buscarPorId")
    class buscarPorId {

        @Test
        @DisplayName("Deve retornar movimentação quando encontrar pelo id")
        void deveRetornarMovimentacaoQuandoEncontrarPeloId() {
            Movimentacao movimentacao = new Movimentacao();
            when(movimentacaoRepository.findById(anyInt())).thenReturn(Optional.of(movimentacao));

            Movimentacao resultado = movimentacaoService.buscarPorId(1L);

            assertNotNull(resultado);
            assertEquals(movimentacao, resultado);
            verify(movimentacaoRepository, times(1)).findById(1);
        }

        @Test
        @DisplayName("Deve lançar NotFoundException quando não encontrar a movimentação pelo id")
        void deveLancarNotFoundExceptionQuandoNaoEncontrarAMovimentacaoPeloId() {
            when(movimentacaoRepository.findById(anyInt())).thenReturn(Optional.empty());

            assertThrows(NotFoundException.class, () -> {
                movimentacaoService.buscarPorId(1L);
            });

            verify(movimentacaoRepository, times(1)).findById(1);
        }
    }

    @Nested
    @DisplayName("cadastrar")
    class cadastrar {

        @Test
        @DisplayName("Deve cadastrar movimentação com sucesso quando strategy suportar o tipo")
        void deveCadastrarMovimentacaoComSucesso() {
            MotivoMovimentacao motivoMovimentacao = new MotivoMovimentacao();
            Movimentacao movimentacao = new Movimentacao();
            MovimentacaoRequestDto request = mock(MovimentacaoRequestDto.class);
            MultipartFile arquivo = mock(MultipartFile.class);

            when(request.getIdMotivoMovimentacao()).thenReturn(1);
            when(tipoService.buscarPorId(1)).thenReturn(motivoMovimentacao);
            when(usuarioService.getUsuarioLogado()).thenReturn(null);
            when(movimentacaoStrategy.suporta(motivoMovimentacao)).thenReturn(true);
            when(movimentacaoStrategy.realizarMovimentacao(any(MovimentacaoContextDto.class))).thenReturn(movimentacao);
            doReturn(movimentacao).when(movimentacaoRepository).save(any());

            Movimentacao resultado = movimentacaoService.cadastrar(request, arquivo);

            assertNotNull(resultado);
            assertEquals(movimentacao, resultado);
            verify(movimentacaoRepository, times(1)).save(movimentacao);
            verify(movimentacaoRepository, times(1)).flush();
            verify(movimentacaoStrategy, times(1)).atualizarEstoque(movimentacao);
        }

        @Test
        @DisplayName("Deve lançar BadRequestException quando nenhuma strategy suportar o tipo de movimentação")
        void deveLancarBadRequestExceptionQuandoNenhumaStrategySuportarOTipo() {
            MotivoMovimentacao motivoMovimentacao = new MotivoMovimentacao();
            MovimentacaoRequestDto request = mock(MovimentacaoRequestDto.class);
            MultipartFile arquivo = mock(MultipartFile.class);

            when(request.getIdMotivoMovimentacao()).thenReturn(1);
            when(tipoService.buscarPorId(1)).thenReturn(motivoMovimentacao);
            when(usuarioService.getUsuarioLogado()).thenReturn(null);
            when(movimentacaoStrategy.suporta(motivoMovimentacao)).thenReturn(false);

            assertThrows(BadRequestException.class, () -> {
                movimentacaoService.cadastrar(request, arquivo);
            });

            verify(movimentacaoRepository, never()).save(any());
            verify(movimentacaoStrategy, never()).realizarMovimentacao(any());
            verify(movimentacaoStrategy, never()).atualizarEstoque(any());
        }

        @Test
        @DisplayName("Deve associar movimentação à sessão revendedora quando sessão não for nula")
        void deveAssociarMovimentacaoASessaoRevendedoraQuandoSessaoNaoForNula() {
            MotivoMovimentacao motivoMovimentacao = new MotivoMovimentacao();
            MovimentacaoRevendedora sessao = new MovimentacaoRevendedora();
            sessao.setMovimentacoes(new ArrayList<>());
            Movimentacao movimentacao = new Movimentacao();
            movimentacao.setMovimentacaoRevendedora(sessao);
            MovimentacaoRequestDto request = mock(MovimentacaoRequestDto.class);
            MultipartFile arquivo = mock(MultipartFile.class);

            when(request.getIdMotivoMovimentacao()).thenReturn(1);
            when(tipoService.buscarPorId(1)).thenReturn(motivoMovimentacao);
            when(usuarioService.getUsuarioLogado()).thenReturn(null);
            when(movimentacaoStrategy.suporta(motivoMovimentacao)).thenReturn(true);
            when(movimentacaoStrategy.realizarMovimentacao(any(MovimentacaoContextDto.class))).thenReturn(movimentacao);
            doReturn(movimentacao).when(movimentacaoRepository).save(any());

            Movimentacao resultado = movimentacaoService.cadastrar(request, arquivo);

            assertNotNull(resultado);
            assertTrue(sessao.getMovimentacoes().contains(movimentacao));
            verify(movimentacaoRepository, times(1)).flush();
        }

        @Test
        @DisplayName("Deve inicializar lista de movimentações da sessão quando a lista for nula")
        void deveInicializarListaDeMovimentacoesDaSessaoQuandoAListaForNula() {
            MotivoMovimentacao motivoMovimentacao = new MotivoMovimentacao();
            MovimentacaoRevendedora sessao = new MovimentacaoRevendedora();
            sessao.setMovimentacoes(null);
            Movimentacao movimentacao = new Movimentacao();
            movimentacao.setMovimentacaoRevendedora(sessao);
            MovimentacaoRequestDto request = mock(MovimentacaoRequestDto.class);
            MultipartFile arquivo = mock(MultipartFile.class);

            when(request.getIdMotivoMovimentacao()).thenReturn(1);
            when(tipoService.buscarPorId(1)).thenReturn(motivoMovimentacao);
            when(usuarioService.getUsuarioLogado()).thenReturn(null);
            when(movimentacaoStrategy.suporta(motivoMovimentacao)).thenReturn(true);
            when(movimentacaoStrategy.realizarMovimentacao(any(MovimentacaoContextDto.class))).thenReturn(movimentacao);
            doReturn(movimentacao).when(movimentacaoRepository).save(any());

            Movimentacao resultado = movimentacaoService.cadastrar(request, arquivo);

            assertNotNull(resultado);
            assertNotNull(sessao.getMovimentacoes());
            assertTrue(sessao.getMovimentacoes().contains(movimentacao));
        }

        @Test
        @DisplayName("Deve cadastrar movimentação sem associar sessão quando sessão for nula")
        void deveCadastrarMovimentacaoSemAssociarSessaoQuandoSessaoForNula() {
            MotivoMovimentacao motivoMovimentacao = new MotivoMovimentacao();
            Movimentacao movimentacao = mock(Movimentacao.class);
            MovimentacaoRequestDto request = mock(MovimentacaoRequestDto.class);
            MultipartFile arquivo = mock(MultipartFile.class);

            when(request.getIdMotivoMovimentacao()).thenReturn(1);
            when(tipoService.buscarPorId(1)).thenReturn(motivoMovimentacao);
            when(usuarioService.getUsuarioLogado()).thenReturn(null);
            when(movimentacaoStrategy.suporta(motivoMovimentacao)).thenReturn(true);
            when(movimentacaoStrategy.realizarMovimentacao(any(MovimentacaoContextDto.class))).thenReturn(movimentacao);
            when(movimentacao.getMovimentacaoRevendedora()).thenReturn(null);
            doReturn(movimentacao).when(movimentacaoRepository).save(any());

            Movimentacao resultado = movimentacaoService.cadastrar(request, arquivo);

            assertNotNull(resultado);
            assertNull(resultado.getMovimentacaoRevendedora());
            verify(movimentacaoRepository, times(1)).flush();
        }
    }
}