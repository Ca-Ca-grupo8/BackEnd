package com.caeca.estoque.strategy.movimentacao;

import com.caeca.estoque.database.model.joia.Joia;
import com.caeca.estoque.database.model.movimentacao.Movimentacao;
import com.caeca.estoque.database.model.movimentacaoRevendedora.MovimentacaoRevendedora;
import com.caeca.estoque.database.model.motivoMovimentacao.MotivoMovimentacao;
import com.caeca.estoque.database.model.tipoMovimentacao.TipoMovimentacao;
import com.caeca.estoque.database.model.tipoMovimentacao.TipoMovimentacaoEnum;
import com.caeca.estoque.database.model.usuario.Usuario;
import com.caeca.estoque.dto.movimentacao.MovimentacaoContextDto;
import com.caeca.estoque.dto.movimentacao.MovimentacaoRequestDto;
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
class ReentradaStrategyTest {

    @Mock private JoiaService joiaService;
    @Mock private MovimentacaoRevendedoraService mrService;

    @InjectMocks private ReentradaStrategy reentradaStrategy;

    @Nested
    @DisplayName("suporta")
    class suporta {

        @Test
        @DisplayName("Deve retornar true quando descrição do tipo contiver 'reentrada'")
        void deveRetornarTrueQuandoDescricaoContiverReentrada() {
            MotivoMovimentacao tipo = new MotivoMovimentacao();
            tipo.setTipoMovimentacao(new TipoMovimentacao(1L, TipoMovimentacaoEnum.REENTRADA));

            assertTrue(reentradaStrategy.suporta(tipo));
        }

        @Test
        @DisplayName("Deve retornar false quando descrição não contiver 'reentrada'")
        void deveRetornarFalseQuandoDescricaoNaoContiverReentrada() {
            MotivoMovimentacao tipo = new MotivoMovimentacao();
            tipo.setTipoMovimentacao(new TipoMovimentacao(1L, TipoMovimentacaoEnum.ENTRADA));

            assertFalse(reentradaStrategy.suporta(tipo));
        }
    }

    @Nested
    @DisplayName("realizarMovimentacao")
    class realizarMovimentacao {

        @Test
        @DisplayName("Deve realizar movimentação sem revendedora quando idRevendedora for nulo")
        void deveRealizarMovimentacaoSemRevendedoraQuandoIdRevendedoraForNulo() {
            Joia joia = new Joia();
            joia.setId(1L);

            MotivoMovimentacao tipo = new MotivoMovimentacao();
            Usuario usuario = new Usuario();
            MultipartFile arquivo = mock(MultipartFile.class);

            MovimentacaoRequestDto dados = new MovimentacaoRequestDto(
                    3, 1L, null, 1, null, null, null, null, null);

            MovimentacaoContextDto dto = new MovimentacaoContextDto(tipo, usuario, dados, arquivo);

            when(joiaService.obterEntityJoiaPorId(1L)).thenReturn(joia);

            Movimentacao resultado = reentradaStrategy.realizarMovimentacao(dto);

            assertNotNull(resultado);
            assertEquals(joia, resultado.getJoia());
            assertEquals(tipo, resultado.getMotivoMovimentacao());
            assertEquals(usuario, resultado.getUsuario());
            assertNull(resultado.getMovimentacaoRevendedora());
            verify(mrService, never()).buscarVendaNaoFechadaPorRevendedora(any());
        }

        @Test
        @DisplayName("Deve associar revendedora à movimentação quando idRevendedora for informado")
        void deveAssociarRevendedoraAMovimentacaoQuandoIdRevendedoraForInformado() {
            Joia joia = new Joia();
            joia.setId(1L);

            MovimentacaoRevendedora movimentacaoRevendedora = new MovimentacaoRevendedora();
            MotivoMovimentacao tipo = new MotivoMovimentacao();
            Usuario usuario = new Usuario();
            MultipartFile arquivo = mock(MultipartFile.class);

            MovimentacaoRequestDto dados = new MovimentacaoRequestDto(
                    3, 1L, null, 1, null, 5L, null, null, null);

            MovimentacaoContextDto dto = new MovimentacaoContextDto(tipo, usuario, dados, arquivo);

            when(joiaService.obterEntityJoiaPorId(1L)).thenReturn(joia);
            when(mrService.buscarVendaNaoFechadaPorRevendedora(5L)).thenReturn(movimentacaoRevendedora);

            Movimentacao resultado = reentradaStrategy.realizarMovimentacao(dto);

            assertNotNull(resultado);
            assertEquals(movimentacaoRevendedora, resultado.getMovimentacaoRevendedora());
            verify(mrService, times(1)).buscarVendaNaoFechadaPorRevendedora(5L);
        }
    }

    @Nested
    @DisplayName("atualizarEstoque")
    class atualizarEstoque {

        @Test
        @DisplayName("Deve incrementar estoque da joia com a quantidade da movimentação")
        void deveIncrementarEstoqueDaJoiaComAQuantidadeDaMovimentacao() {
            Joia joia = new Joia();
            joia.setId(1L);

            Movimentacao movimentacao = new Movimentacao();
            movimentacao.setJoia(joia);
            movimentacao.setQuantidade(3);

            reentradaStrategy.atualizarEstoque(movimentacao);

            verify(joiaService, times(1)).adicionarEstoque(1L, 3);
        }
    }
}