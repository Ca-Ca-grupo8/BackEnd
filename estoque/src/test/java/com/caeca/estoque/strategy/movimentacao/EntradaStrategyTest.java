package com.caeca.estoque.strategy.movimentacao;

import com.caeca.estoque.database.model.arquivo.Arquivo;
import com.caeca.estoque.database.model.fornecedores.Fornecedor;
import com.caeca.estoque.database.model.joia.Joia;
import com.caeca.estoque.database.model.movimentacao.Movimentacao;
import com.caeca.estoque.database.model.motivoMovimentacao.MotivoMovimentacao;
import com.caeca.estoque.database.model.tipoMovimentacao.TipoMovimentacao;
import com.caeca.estoque.database.model.tipoMovimentacao.TipoMovimentacaoEnum;
import com.caeca.estoque.database.model.usuario.Usuario;
import com.caeca.estoque.database.repository.fornecedor.FornecedorRepository;
import com.caeca.estoque.dto.joia.JoiaEntradaDTO;
import com.caeca.estoque.dto.joia.JoiaSaidaDTO;
import com.caeca.estoque.dto.movimentacao.MovimentacaoContextDto;
import com.caeca.estoque.dto.movimentacao.MovimentacaoRequestDto;
import com.caeca.estoque.exception.ConflictException;
import com.caeca.estoque.exception.NotFoundException;
import com.caeca.estoque.service.joia.JoiaService;
import com.caeca.estoque.strategy.arquivo.ArquivoStrategy;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EntradaStrategyTest {

    @Mock private JoiaService joiaService;
    @Mock private FornecedorRepository fornecedorRepository;
    @Mock private ArquivoStrategy arquivoStrategy;

    @InjectMocks private EntradaStrategy entradaStrategy;

    @Nested
    @DisplayName("suporta")
    class suporta {

        @Test
        @DisplayName("Deve retornar true quando descrição do tipo contiver 'ENTRADA'")
        void deveRetornarTrueQuandoDescricaoContiverEntrada() {
            MotivoMovimentacao tipo = new MotivoMovimentacao();
            tipo.setTipoMovimentacao(new TipoMovimentacao(1L, TipoMovimentacaoEnum.ENTRADA));

            assertTrue(entradaStrategy.suporta(tipo));
        }

        @Test
        @DisplayName("Deve retornar false quando descrição não contiver 'entrada'")
        void deveRetornarFalseQuandoDescricaoNaoContiverEntrada() {
            MotivoMovimentacao tipo = new MotivoMovimentacao();
            tipo.setTipoMovimentacao(new TipoMovimentacao(1L, TipoMovimentacaoEnum.SAIDA));

            assertFalse(entradaStrategy.suporta(tipo));
        }
    }

    @Nested
    @DisplayName("realizarMovimentacao")
    class realizarMovimentacao {

        @Test
        @DisplayName("Deve realizar movimentação com joia existente quando idJoia for informado")
        void deveRealizarMovimentacaoComJoiaExistenteQuandoIdJoiaForInformado() {
            Joia joia = new Joia();
            joia.setId(1L);
            joia.setArquivo(null);

            Fornecedor fornecedor = new Fornecedor();
            MotivoMovimentacao tipo = new MotivoMovimentacao();
            Usuario usuario = new Usuario();
            MultipartFile arquivo = mock(MultipartFile.class);

            MovimentacaoRequestDto dados = new MovimentacaoRequestDto(
                    2, 1L, null, 1, 1L, null, 50.0, 10.0, 2.0);

            MovimentacaoContextDto dto = new MovimentacaoContextDto(tipo, usuario, dados, arquivo);

            when(joiaService.obterEntityJoiaPorId(1L)).thenReturn(joia);
            when(fornecedorRepository.findById(1L)).thenReturn(Optional.of(fornecedor));
            when(arquivo.isEmpty()).thenReturn(true);

            Movimentacao resultado = entradaStrategy.realizarMovimentacao(dto);

            assertNotNull(resultado);
            assertEquals(joia, resultado.getJoia());
            assertEquals(fornecedor, resultado.getFornecedor());
            assertEquals(tipo, resultado.getMotivoMovimentacao());
            assertEquals(usuario, resultado.getUsuario());
            assertEquals(50.0, resultado.getPrecoOriginal());
            assertEquals(10.0, resultado.getPrecoBanho());
            assertEquals(2.0, resultado.getMultiplicador());
            verify(joiaService, never()).criarJoia(any());
            verify(joiaService, times(1)).obterEntityJoiaPorId(1L);
        }

        @Test
        @DisplayName("Deve criar nova joia quando idJoia não for informado")
        void deveCriarNovaJoiaQuandoIdJoiaNaoForInformado() {
            JoiaEntradaDTO joiaEntradaDTO = new JoiaEntradaDTO();
            JoiaSaidaDTO joiaSaidaDTO = new JoiaSaidaDTO();
            joiaSaidaDTO.setId(2L);

            Joia joia = new Joia();
            joia.setId(2L);
            joia.setArquivo(null);

            Fornecedor fornecedor = new Fornecedor();
            MotivoMovimentacao tipo = new MotivoMovimentacao();
            Usuario usuario = new Usuario();
            MultipartFile arquivo = mock(MultipartFile.class);

            MovimentacaoRequestDto dados = new MovimentacaoRequestDto(
                    2, null, joiaEntradaDTO, 1, 1L, null, 50.0, 10.0, 2.0);

            MovimentacaoContextDto dto = new MovimentacaoContextDto(tipo, usuario, dados, arquivo);

            when(joiaService.criarJoia(joiaEntradaDTO)).thenReturn(joiaSaidaDTO);
            when(joiaService.obterEntityJoiaPorId(2L)).thenReturn(joia);
            when(fornecedorRepository.findById(1L)).thenReturn(Optional.of(fornecedor));
            when(arquivo.isEmpty()).thenReturn(true);

            Movimentacao resultado = entradaStrategy.realizarMovimentacao(dto);

            assertNotNull(resultado);
            assertEquals(joia, resultado.getJoia());
            verify(joiaService, times(1)).criarJoia(joiaEntradaDTO);
            verify(joiaService, times(1)).obterEntityJoiaPorId(2L);
        }

        @Test
        @DisplayName("Deve salvar arquivo quando arquivo não estiver vazio e joia não tiver imagem")
        void deveSalvarArquivoQuandoArquivoNaoEstiverVazioEJoiaNaoTiverImagem() {
            Joia joia = new Joia();
            joia.setId(1L);
            joia.setArquivo(null);

            Arquivo arquivoEntity = new Arquivo();
            Fornecedor fornecedor = new Fornecedor();
            MotivoMovimentacao tipo = new MotivoMovimentacao();
            Usuario usuario = new Usuario();
            MultipartFile arquivo = mock(MultipartFile.class);

            MovimentacaoRequestDto dados = new MovimentacaoRequestDto(
                    2, 1L, null, 1, 1L, null, 50.0, 10.0, 2.0);

            MovimentacaoContextDto dto = new MovimentacaoContextDto(tipo, usuario, dados, arquivo);

            when(joiaService.obterEntityJoiaPorId(1L)).thenReturn(joia);
            when(fornecedorRepository.findById(1L)).thenReturn(Optional.of(fornecedor));
            when(arquivo.isEmpty()).thenReturn(false);
            when(arquivoStrategy.adicionar(arquivo)).thenReturn(arquivoEntity);

            entradaStrategy.realizarMovimentacao(dto);

            verify(arquivoStrategy, times(1)).adicionar(arquivo);
            assertEquals(joia, arquivoEntity.getJoia());
            assertEquals(arquivoEntity, joia.getArquivo());
        }

        @Test
        @DisplayName("Deve lançar ConflictException quando joia já possuir imagem cadastrada")
        void deveLancarConflictExceptionQuandoJoiaJaPossuirImagemCadastrada() {
            Arquivo arquivoExistente = new Arquivo();
            Joia joia = new Joia();
            joia.setId(1L);
            joia.setArquivo(arquivoExistente);

            Fornecedor fornecedor = new Fornecedor();
            MotivoMovimentacao tipo = new MotivoMovimentacao();
            Usuario usuario = new Usuario();
            MultipartFile arquivo = mock(MultipartFile.class);

            MovimentacaoRequestDto dados = new MovimentacaoRequestDto(
                    2, 1L, null, 1, 1L, null, 50.0, 10.0, 2.0);

            MovimentacaoContextDto dto = new MovimentacaoContextDto(tipo, usuario, dados, arquivo);

            when(joiaService.obterEntityJoiaPorId(1L)).thenReturn(joia);
            when(fornecedorRepository.findById(1L)).thenReturn(Optional.of(fornecedor));

            assertThrows(ConflictException.class, () -> {
                entradaStrategy.realizarMovimentacao(dto);
            });

            verify(arquivoStrategy, never()).adicionar(any());
        }

        @Test
        @DisplayName("Deve lançar NotFoundException quando fornecedor não for encontrado")
        void deveLancarNotFoundExceptionQuandoFornecedorNaoForEncontrado() {
            Joia joia = new Joia();
            joia.setId(1L);
            joia.setArquivo(null);

            MotivoMovimentacao tipo = new MotivoMovimentacao();
            Usuario usuario = new Usuario();
            MultipartFile arquivo = mock(MultipartFile.class);

            MovimentacaoRequestDto dados = new MovimentacaoRequestDto(
                    2, 1L, null, 1, 99L, null, 50.0, 10.0, 2.0);

            MovimentacaoContextDto dto = new MovimentacaoContextDto(tipo, usuario, dados, arquivo);

            when(joiaService.obterEntityJoiaPorId(1L)).thenReturn(joia);
            when(fornecedorRepository.findById(99L)).thenReturn(Optional.empty());

            assertThrows(NotFoundException.class, () -> {
                entradaStrategy.realizarMovimentacao(dto);
            });
        }

        @Test
        @DisplayName("Deve calcular corretamente o preço final da joia")
        void deveCalcularCorretamenteOPrecoFinalDaJoia() {
            Joia joia = new Joia();
            joia.setId(1L);
            joia.setArquivo(null);

            Fornecedor fornecedor = new Fornecedor();
            MotivoMovimentacao tipo = new MotivoMovimentacao();
            Usuario usuario = new Usuario();
            MultipartFile arquivo = mock(MultipartFile.class);

            // custoOriginal = precoBanho(10.0) + precoCompraRevendedor(50.0) = 60.0
            // precoFinal = custoOriginal(60.0) * multiplicador(3.0) = 180.0
            MovimentacaoRequestDto dados = new MovimentacaoRequestDto(
                    2, 1L, null, 1, 1L, null, 50.0, 10.0, 3.0);

            MovimentacaoContextDto dto = new MovimentacaoContextDto(tipo, usuario, dados, arquivo);

            when(joiaService.obterEntityJoiaPorId(1L)).thenReturn(joia);
            when(fornecedorRepository.findById(1L)).thenReturn(Optional.of(fornecedor));
            when(arquivo.isEmpty()).thenReturn(true);

            entradaStrategy.realizarMovimentacao(dto);

            verify(joiaService, times(1)).atualizarJoiaPorId(eq(1L), argThat(dto2 ->
                    dto2.getPrecoFinal() == 180.0));
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
            movimentacao.setQuantidade(5);

            entradaStrategy.atualizarEstoque(movimentacao);

            verify(joiaService, times(1)).adicionarEstoque(1L, 5);
        }
    }
}