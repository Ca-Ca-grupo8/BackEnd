package com.caeca.estoque.strategy.movimentacao;

import com.caeca.estoque.database.model.joia.Joia;
import com.caeca.estoque.database.model.movimentacao.Movimentacao;
import com.caeca.estoque.database.model.movimentacaoRevendedora.MovimentacaoRevendedora;
import com.caeca.estoque.database.model.tipoMovimentacao.TipoMovimentacao;
import com.caeca.estoque.database.model.usuario.Usuario;
import com.caeca.estoque.dto.movimentacao.MovimentacaoContextDto;
import com.caeca.estoque.dto.movimentacao.MovimentacaoRequestDto;
import com.caeca.estoque.dto.movimentacaoRevendedora.MovimentacaoRevendedoraRequestDto;
import com.caeca.estoque.exception.QtdInsuficienteException;
import com.caeca.estoque.service.joia.JoiaService;
import com.caeca.estoque.service.movimentacaoRevendedora.MovimentacaoRevendedoraService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SaidaStrategyTest {

    @Mock private JoiaService joiaService;
    @Mock private MovimentacaoRevendedoraService mrService;

    @InjectMocks private SaidaStrategy saidaStrategy;

    @Nested
    @DisplayName("suporta")
    class suporta {

        @Test
        @DisplayName("Deve retornar true quando descrição contiver 'saída' com acento")
        void deveRetornarTrueQuandoDescricaoContiverSaidaComAcento() {
            TipoMovimentacao tipo = new TipoMovimentacao();
            tipo.setDescricao("Saída de estoque");

            assertTrue(saidaStrategy.suporta(tipo));
        }

        @Test
        @DisplayName("Deve retornar true quando descrição contiver 'saida' sem acento")
        void deveRetornarTrueQuandoDescricaoContiverSaidaSemAcento() {
            TipoMovimentacao tipo = new TipoMovimentacao();
            tipo.setDescricao("saida");

            assertTrue(saidaStrategy.suporta(tipo));
        }

        @Test
        @DisplayName("Deve retornar true quando descrição contiver 'venda'")
        void deveRetornarTrueQuandoDescricaoContiverVenda() {
            TipoMovimentacao tipo = new TipoMovimentacao();
            tipo.setDescricao("Venda para revendedora");

            assertTrue(saidaStrategy.suporta(tipo));
        }

        @Test
        @DisplayName("Deve retornar true quando descrição estiver em caixa alta")
        void deveRetornarTrueQuandoDescricaoEstiverEmCaixaAlta() {
            TipoMovimentacao tipo = new TipoMovimentacao();
            tipo.setDescricao("VENDA");

            assertTrue(saidaStrategy.suporta(tipo));
        }

        @Test
        @DisplayName("Deve retornar false quando descrição não contiver nenhum dos termos")
        void deveRetornarFalseQuandoDescricaoNaoContiverNenhumDosTeremos() {
            TipoMovimentacao tipo = new TipoMovimentacao();
            tipo.setDescricao("Entrada de estoque");

            assertFalse(saidaStrategy.suporta(tipo));
        }
    }

    @Nested
    @DisplayName("realizarMovimentacao")
    class realizarMovimentacao {

        @Test
        @DisplayName("Deve realizar movimentação de saída com sucesso quando houver estoque suficiente")
        void deveRealizarMovimentacaoDeSaidaComSucessoQuandoHouverEstoqueSuficiente() {
            Joia joia = new Joia();
            joia.setId(1L);
            joia.setQuantidade(10);
            joia.setPreco(150.0);

            TipoMovimentacao tipo = new TipoMovimentacao();
            tipo.setDescricao("Saída");

            Usuario usuario = new Usuario();
            MultipartFile arquivo = mock(MultipartFile.class);

            MovimentacaoRequestDto dados = new MovimentacaoRequestDto(
                    5, 1L, null, 1, null, null, null, null, null);

            MovimentacaoContextDto dto = new MovimentacaoContextDto(tipo, usuario, dados, arquivo);

            when(joiaService.obterEntityJoiaPorId(1L)).thenReturn(joia);

            Movimentacao resultado = saidaStrategy.realizarMovimentacao(dto);

            assertNotNull(resultado);
            assertEquals(joia, resultado.getJoia());
            assertEquals(tipo, resultado.getTipoMovimentacao());
            assertEquals(usuario, resultado.getUsuario());
            assertEquals(150.0, resultado.getPrecoSaida());
            assertNull(resultado.getMovimentacaoRevendedora());
            verify(mrService, never()).buscarOuCriarVendaPorRevendedora(any());
        }

        @Test
        @DisplayName("Deve lançar QtdInsuficienteException quando estoque for insuficiente")
        void deveLancarQtdInsuficienteExceptionQuandoEstoqueForInsuficiente() {
            Joia joia = new Joia();
            joia.setId(1L);
            joia.setQuantidade(3);
            joia.setNome("Anel de Ouro");

            TipoMovimentacao tipo = new TipoMovimentacao();
            tipo.setDescricao("Saída");

            Usuario usuario = new Usuario();
            MultipartFile arquivo = mock(MultipartFile.class);

            MovimentacaoRequestDto dados = new MovimentacaoRequestDto(
                    5, 1L, null, 1, null, null, null, null, null);

            MovimentacaoContextDto dto = new MovimentacaoContextDto(tipo, usuario, dados, arquivo);

            when(joiaService.obterEntityJoiaPorId(1L)).thenReturn(joia);

            assertThrows(QtdInsuficienteException.class, () -> {
                saidaStrategy.realizarMovimentacao(dto);
            });

            verify(mrService, never()).buscarOuCriarVendaPorRevendedora(any());
        }

        @Test
        @DisplayName("Deve associar revendedora quando tipo for venda e idRevendedora for informado")
        void deveAssociarRevendedoraQuandoTipoForVendaEIdRevendedoraForInformado() {
            Joia joia = new Joia();
            joia.setId(1L);
            joia.setQuantidade(10);
            joia.setPreco(150.0);

            MovimentacaoRevendedora movimentacaoRevendedora = new MovimentacaoRevendedora();
            TipoMovimentacao tipo = new TipoMovimentacao();
            tipo.setDescricao("Venda para revendedora");

            Usuario usuario = new Usuario();
            MultipartFile arquivo = mock(MultipartFile.class);

            MovimentacaoRequestDto dados = new MovimentacaoRequestDto(
                    5, 1L, null, 1, null, 7L, null, null, null);

            MovimentacaoContextDto dto = new MovimentacaoContextDto(tipo, usuario, dados, arquivo);

            when(joiaService.obterEntityJoiaPorId(1L)).thenReturn(joia);
            when(mrService.buscarOuCriarVendaPorRevendedora(any(MovimentacaoRevendedoraRequestDto.class)))
                    .thenReturn(movimentacaoRevendedora);

            Movimentacao resultado = saidaStrategy.realizarMovimentacao(dto);

            assertNotNull(resultado);
            assertEquals(movimentacaoRevendedora, resultado.getMovimentacaoRevendedora());
            verify(mrService, times(1)).buscarOuCriarVendaPorRevendedora(
                    argThat(r -> r.getIdRevendedora().equals(7L)));
        }

        @Test
        @DisplayName("Deve não associar revendedora quando tipo for venda mas idRevendedora for nulo")
        void deveNaoAssociarRevendedoraQuandoTipoForVendaMasIdRevendedoraForNulo() {
            Joia joia = new Joia();
            joia.setId(1L);
            joia.setQuantidade(10);
            joia.setPreco(150.0);

            TipoMovimentacao tipo = new TipoMovimentacao();
            tipo.setDescricao("Venda para revendedora");

            Usuario usuario = new Usuario();
            MultipartFile arquivo = mock(MultipartFile.class);

            MovimentacaoRequestDto dados = new MovimentacaoRequestDto(
                    5, 1L, null, 1, null, null, null, null, null);

            MovimentacaoContextDto dto = new MovimentacaoContextDto(tipo, usuario, dados, arquivo);

            when(joiaService.obterEntityJoiaPorId(1L)).thenReturn(joia);

            Movimentacao resultado = saidaStrategy.realizarMovimentacao(dto);

            assertNotNull(resultado);
            assertNull(resultado.getMovimentacaoRevendedora());
            verify(mrService, never()).buscarOuCriarVendaPorRevendedora(any());
        }

        @Test
        @DisplayName("Deve não associar revendedora quando idRevendedora for informado mas tipo não for venda")
        void deveNaoAssociarRevendedoraQuandoIdRevendedoraForInformadoMasTipoNaoForVenda() {
            Joia joia = new Joia();
            joia.setId(1L);
            joia.setQuantidade(10);
            joia.setPreco(150.0);

            TipoMovimentacao tipo = new TipoMovimentacao();
            tipo.setDescricao("Saída de estoque");

            Usuario usuario = new Usuario();
            MultipartFile arquivo = mock(MultipartFile.class);

            MovimentacaoRequestDto dados = new MovimentacaoRequestDto(
                    5, 1L, null, 1, null, 7L, null, null, null);

            MovimentacaoContextDto dto = new MovimentacaoContextDto(tipo, usuario, dados, arquivo);

            when(joiaService.obterEntityJoiaPorId(1L)).thenReturn(joia);

            Movimentacao resultado = saidaStrategy.realizarMovimentacao(dto);

            assertNotNull(resultado);
            assertNull(resultado.getMovimentacaoRevendedora());
            verify(mrService, never()).buscarOuCriarVendaPorRevendedora(any());
        }
    }

    @Nested
    @DisplayName("atualizarEstoque")
    class atualizarEstoque {

        @Test
        @DisplayName("Deve decrementar estoque da joia com a quantidade da movimentação")
        void deveDecrementarEstoqueDaJoiaComAQuantidadeDaMovimentacao() {
            Joia joia = new Joia();
            joia.setId(1L);

            Movimentacao movimentacao = new Movimentacao();
            movimentacao.setJoia(joia);
            movimentacao.setQuantidade(5);

            saidaStrategy.atualizarEstoque(movimentacao);

            verify(joiaService, times(1)).diminuirEstoque(1L, 5);
        }
    }
}