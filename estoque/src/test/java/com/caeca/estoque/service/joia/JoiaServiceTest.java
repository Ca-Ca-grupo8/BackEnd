package com.caeca.estoque.service.joia;

import com.caeca.estoque.database.model.banho.Banho;
import com.caeca.estoque.database.model.categoria.Categoria;
import com.caeca.estoque.database.model.fornecedores.Fornecedor;
import com.caeca.estoque.database.model.joia.Joia;
import com.caeca.estoque.database.model.material.Material;
import com.caeca.estoque.database.repository.joia.JoiaRepository;
import com.caeca.estoque.database.repository.movimentacao.FornecedorJoiaProjecao;
import com.caeca.estoque.database.repository.movimentacao.MovimentacaoRepository;
import com.caeca.estoque.dto.joia.JoiaAtualizacaoDto;
import com.caeca.estoque.dto.joia.JoiaEntradaDto;
import com.caeca.estoque.dto.joia.JoiaSaidaDto;
import com.caeca.estoque.dto.joia.JoiaStatus;
import com.caeca.estoque.service.Material.MaterialService;
import com.caeca.estoque.service.banho.BanhoService;
import com.caeca.estoque.service.categoria.CategoriaService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JoiaServiceTest {

    @Mock private JoiaRepository joiaRepository;
    @Mock private MovimentacaoRepository movimentacaoRepository;
    @Mock private BanhoService banhoService;
    @Mock private CategoriaService categoriaService;
    @Mock private MaterialService materialService;

    @InjectMocks private JoiaService joiaService;

    @Nested
    @DisplayName("listarTodas")
    class listarTodas {
        @Test
        @DisplayName("Deve retornar página de joias com sucesso quando existirem dados")
        void deveRetornarPaginaDeJoiasComSucesso() {
            Joia joia = new Joia();
            joia.setId(1L);
            when(joiaRepository.listarTodosFiltrado(any(), any(), any(), any(), any(), any(), any(), any(Pageable.class)))
                    .thenReturn(new PageImpl<>(List.of(joia)));
            when(movimentacaoRepository.buscarFornecedoresDasJoias(anyList())).thenReturn(List.of());

            Page<JoiaSaidaDto> resultado = joiaService.listarTodas(null, null, null, null, null, null, null, PageRequest.of(0, 10));

            assertNotNull(resultado);
            assertFalse(resultado.getContent().isEmpty());
            assertEquals(1, resultado.getContent().size());
            verify(joiaRepository, times(1)).listarTodosFiltrado(any(), any(), any(), any(), any(), any(), any(), any(Pageable.class));
        }

        @Test
        @DisplayName("Deve buscar fornecedores em lote único e preencher por joia")
        void deveBuscarFornecedoresEmLoteUnico() {
            Joia joia1 = new Joia();
            joia1.setId(1L);
            Joia joia2 = new Joia();
            joia2.setId(2L);

            Fornecedor fornecedor1 = new Fornecedor();
            fornecedor1.setId(10L);
            fornecedor1.setNome("Fornecedor A");
            Fornecedor fornecedor2 = new Fornecedor();
            fornecedor2.setId(20L);
            fornecedor2.setNome("Fornecedor B");

            when(joiaRepository.listarTodosFiltrado(any(), any(), any(), any(), any(), any(), any(), any(Pageable.class)))
                    .thenReturn(new PageImpl<>(List.of(joia1, joia2)));
            when(movimentacaoRepository.buscarFornecedoresDasJoias(anyList()))
                    .thenReturn(List.of(
                            new FornecedorJoiaProjecao(1L, fornecedor1),
                            new FornecedorJoiaProjecao(2L, fornecedor2)));

            Page<JoiaSaidaDto> resultado = joiaService.listarTodas(null, null, null, null, null, null, null, PageRequest.of(0, 10));

            assertNotNull(resultado);
            assertEquals(2, resultado.getContent().size());
            assertEquals("Fornecedor A", resultado.getContent().get(0).getFornecedores().get(0).getNome());
            assertEquals("Fornecedor B", resultado.getContent().get(1).getFornecedores().get(0).getNome());
            verify(movimentacaoRepository, times(1)).buscarFornecedoresDasJoias(List.of(1L, 2L));
        }

        @Test
        @DisplayName("Deve repassar o filtro de status para a consulta paginada")
        void deveRepassarFiltroDeStatusParaConsulta() {
            Joia joiaAtiva = new Joia();
            joiaAtiva.setId(1L);
            joiaAtiva.setQuantidade(10);
            joiaAtiva.setQuantidadeMinima(5);
            joiaAtiva.setAtivo(true);

            when(joiaRepository.listarTodosFiltrado(any(), any(), any(), any(), any(), any(), any(), any(Pageable.class)))
                    .thenReturn(new PageImpl<>(List.of(joiaAtiva)));
            when(movimentacaoRepository.buscarFornecedoresDasJoias(anyList())).thenReturn(List.of());

            Page<JoiaSaidaDto> resultado = joiaService.listarTodas(null, null, null, null, JoiaStatus.OK, null, null, PageRequest.of(0, 10));

            assertNotNull(resultado);
            assertEquals(1, resultado.getContent().size());
            verify(joiaRepository, times(1)).listarTodosFiltrado(any(), any(), any(), any(), eq(JoiaStatus.OK), any(), any(), any(Pageable.class));
        }

        @Test
        @DisplayName("Deve retornar página vazia quando não existirem joias")
        void deveRetornarPaginaVaziaQuandoNaoExistiremJoias() {
            when(joiaRepository.listarTodosFiltrado(any(), any(), any(), any(), any(), any(), any(), any(Pageable.class)))
                    .thenReturn(new PageImpl<>(List.of()));

            Page<JoiaSaidaDto> resultado = joiaService.listarTodas(null, null, null, null, null, null, null, PageRequest.of(0, 10));

            assertNotNull(resultado);
            assertTrue(resultado.getContent().isEmpty());
        }
    }

    @Nested
    @DisplayName("listarJoiaPorId")
    class listarJoiaPorId{
        @Test
        @DisplayName("Deve Retornar Objeto Quando Encontrar Objeto")
        void deveRetornarObjetoQuandoEncontrarObjeto() {
            Joia joia = new Joia();
            when(joiaRepository.findById(anyLong())).thenReturn(Optional.of(joia));

            JoiaSaidaDto resultado = joiaService.listarJoiaPorId(1L);

            assertNotNull(resultado);
        }

        @Test
        @DisplayName("Deve Lancar Exceção (404) Quando Nao Achar Objeto")
        void deveLancarExcecaoQuandoNaoAcharObjeto() {
            when(joiaRepository.findById(anyLong())).thenReturn(Optional.empty());

            assertThrows(ResponseStatusException.class, () -> {
                joiaService.listarJoiaPorId(1L);
            });
        }
    }

    @Nested
    @DisplayName("obterEntityJoiaPorId")
    class obterEntityJoiaPorId{
        @Test
        @DisplayName("Deve obter entity Joia quando encontrar objeto")
        void deveObterEntityJoiaQuandoEncontrarObjeto() {
            Joia joia = new Joia();
            when(joiaRepository.findById(anyLong())).thenReturn(Optional.of(joia));

            Joia resultado = joiaService.obterEntityJoiaPorId(1L);

            assertNotNull(resultado);
            assertEquals(joia, resultado);
        }

        @Test
        @DisplayName("Deve Lancar Exceção (404) Quando Nao Achar Entidade")
        void deveLancarExcecaoQuandoNaoAcharEntidade() {
            when(joiaRepository.findById(anyLong())).thenReturn(Optional.empty());

            assertThrows(ResponseStatusException.class, () -> {
                joiaService.obterEntityJoiaPorId(1L);
            });
        }
    }

    @Nested
    @DisplayName("criarJoia")
    class criarJoia{
        @Test
        @DisplayName("Deve criar joia com sucesso e retornar o DTO de saída")
        void criarJoia() {
            Joia joia = new Joia();
            JoiaEntradaDto dto = new JoiaEntradaDto();
            dto.setCategoria(1L);
            dto.setBanho(1);
            dto.setMaterial(1);

            when(categoriaService.listarCategoriaPorId(anyLong())).thenReturn(new Categoria());
            when(banhoService.buscarPorId(anyInt())).thenReturn(new Banho());
            when(materialService.listarPorId(anyInt())).thenReturn(new Material());
            when(joiaRepository.save(any(Joia.class))).thenReturn(joia);

            JoiaSaidaDto resultado = joiaService.criarJoia(dto);

            assertNotNull(resultado);
        }
    }

    @Nested
    @DisplayName("atualizarJoiaPorId")
    class atualizarJoiaPorId{
        @Test
        @DisplayName("Deve atualizar os dados da joia com sucesso")
        void atualizarJoiaPorId() {
            Joia joia = new Joia();
            joia.setQuantidade(10);
            when(joiaRepository.findById(anyLong())).thenReturn(Optional.of(joia));
            when(joiaRepository.save(any(Joia.class))).thenReturn(joia);

            JoiaSaidaDto resultado = joiaService.atualizarJoiaPorId(1L,
                    new JoiaAtualizacaoDto("a", 2, 10, 1L, 1, 1, 10.0, true));

            assertEquals(10, resultado.getQuantidade());
        }

        @Test
        @DisplayName("Deve lançar exceção quando não encontrar a joia para atualização")
        void deveLancarExcecaoQuandoNaoEncontrarAJoia(){
            when(joiaRepository.findById(anyLong())).thenReturn(Optional.empty());

            assertThrows(RuntimeException.class,
                    () -> joiaService.atualizarJoiaPorId(1L, new JoiaAtualizacaoDto()));
        }

        @Test
        @DisplayName("Deve ignorar campos nulos, vazios ou invalidos na atualizacao")
        void deveIgnorarCamposNulosOuInvalidosNaAtualizacao() {
            Joia joia = new Joia();
            joia.setNome("Original");
            joia.setQuantidadeMinima(5);
            joia.setQuantidade(8);

            when(joiaRepository.findById(anyLong())).thenReturn(Optional.of(joia));
            when(joiaRepository.save(any(Joia.class))).thenReturn(joia);

            JoiaAtualizacaoDto dto = new JoiaAtualizacaoDto(" ", -1, -5, null, null, null, null, null);
            JoiaSaidaDto resultado = joiaService.atualizarJoiaPorId(1L, dto);

            assertEquals("Original", joia.getNome());
            assertEquals(5, joia.getQuantidadeMinima());
            assertEquals(8, joia.getQuantidade());
        }

        @Test
        @DisplayName("Deve atualizar relacionamentos quando IDs forem passados")
        void deveAtualizarRelacionamentosQuandoIdsForemPassados() {
            Joia joia = new Joia();
            when(joiaRepository.findById(anyLong())).thenReturn(Optional.of(joia));
            when(categoriaService.listarCategoriaPorId(anyLong())).thenReturn(new Categoria());
            when(banhoService.buscarPorId(anyInt())).thenReturn(new Banho());
            when(materialService.listarPorId(anyInt())).thenReturn(new Material());
            when(joiaRepository.save(any(Joia.class))).thenReturn(joia);

            JoiaAtualizacaoDto dto = new JoiaAtualizacaoDto();
            dto.setCategoriaId(1L);
            dto.setBanhoId(1);
            dto.setMaterialId(1);

            JoiaSaidaDto resultado = joiaService.atualizarJoiaPorId(1L, dto);

            verify(categoriaService, times(1)).listarCategoriaPorId(1L);
            verify(banhoService, times(1)).buscarPorId(1);
            verify(materialService, times(1)).listarPorId(1);
        }
    }

    @Nested
    @DisplayName("adicionarEstoque")
    class adicionarEstoque{
        @Test
        @DisplayName("Deve incrementar a quantidade do estoque com sucesso")
        void adicionarEstoque() {
            Integer quantidadeRemover = 5;

            Joia joiaMock = new Joia();
            joiaMock.setId(1L);
            joiaMock.setQuantidade(10);

            when(joiaRepository.findById(anyLong())).thenReturn(Optional.of(joiaMock));
            when(joiaRepository.save(any(Joia.class))).thenReturn(joiaMock);

            joiaService.adicionarEstoque(1L, quantidadeRemover);

            assertEquals(15, joiaMock.getQuantidade());
        }
    }

    @Nested
    @DisplayName("dimunirEstoque")
    class dimunirEstoque {
        @Test
        @DisplayName("Deve decrementar a quantidade do estoque com sucesso")
        void disminuirEstoque() {
            Integer quantidadeRemover = 5;

            Joia joiaMock = new Joia();
            joiaMock.setId(1L);
            joiaMock.setQuantidade(10);

            when(joiaRepository.findById(anyLong())).thenReturn(Optional.of(joiaMock));
            when(joiaRepository.save(any(Joia.class))).thenReturn(joiaMock);

            joiaService.diminuirEstoque(1L, quantidadeRemover);

            assertEquals(5, joiaMock.getQuantidade());
        }
    }
}