package com.caeca.estoque.dto.movimentacao;

import com.caeca.estoque.database.model.fornecedores.Fornecedor;
import com.caeca.estoque.database.model.joia.Joia;
import com.caeca.estoque.database.model.movimentacao.Movimentacao;
import com.caeca.estoque.database.model.movimentacaoRevendedora.MovimentacaoRevendedora;
import com.caeca.estoque.database.model.revendedora.Revendedora;
import com.caeca.estoque.database.model.tipoMovimentacao.TipoMovimentacao;
import com.caeca.estoque.database.model.usuario.Usuario;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class MovimentacaoMapperTest {

    @Nested
    @DisplayName("toResponse")
    class toResponse {

        @Test
        @DisplayName("Deve mapear todos os campos da entidade para o DTO de resposta")
        void deveMapearTodosOsCamposDaEntidadeParaODtoDeResposta() {
            Movimentacao entity = new Movimentacao();
            entity.setIdMovimentacao(1L);
            entity.setQuantidade(3);
            entity.setPrecoSaida(200.0);
            entity.setJoia(new Joia());
            entity.setTipoMovimentacao(new TipoMovimentacao());
            entity.setUsuario(new Usuario());
            entity.setFornecedor(null);
            entity.setMovimentacaoRevendedora(null);

            MovimentacaoResponseDto resultado = MovimentacaoMapper.toResponse(entity);

            assertNotNull(resultado);
            assertEquals(1L, resultado.getId());
            assertEquals(3, resultado.getQuantidade());
            assertEquals(200.0, resultado.getPrecoSaida());
            assertNotNull(resultado.getJoia());
            assertNotNull(resultado.getTipoMovimentacao());
            assertNotNull(resultado.getUsuario());
        }

        @Test
        @DisplayName("Deve mapear fornecedor quando fornecedor não for nulo")
        void deveMapearFornecedorQuandoFornecedorNaoForNulo() {
            Fornecedor fornecedor = new Fornecedor(
                    1L, "Fornecedor Teste", "11999999999",
                    true, null, null, "@fornecedor");

            Movimentacao entity = new Movimentacao();
            entity.setIdMovimentacao(1L);
            entity.setJoia(new Joia());
            entity.setTipoMovimentacao(new TipoMovimentacao());
            entity.setUsuario(new Usuario());
            entity.setFornecedor(fornecedor);
            entity.setMovimentacaoRevendedora(null);

            MovimentacaoResponseDto resultado = MovimentacaoMapper.toResponse(entity);

            assertNotNull(resultado.getFornecedor());
            assertEquals(1L, resultado.getFornecedor().getIdFornecedor()    );
            assertEquals("Fornecedor Teste", resultado.getFornecedor().getNome());
        }

        @Test
        @DisplayName("Deve retornar fornecedor nulo quando fornecedor for nulo")
        void deveRetornarFornecedorNuloQuandoFornecedorForNulo() {
            Movimentacao entity = new Movimentacao();
            entity.setIdMovimentacao(1L);
            entity.setJoia(new Joia());
            entity.setTipoMovimentacao(new TipoMovimentacao());
            entity.setUsuario(new Usuario());
            entity.setFornecedor(null);
            entity.setMovimentacaoRevendedora(null);

            MovimentacaoResponseDto resultado = MovimentacaoMapper.toResponse(entity);

            assertNull(resultado.getFornecedor());
        }

        @Test
        @DisplayName("Deve mapear movimentacaoRevendedora quando não for nula")
        void deveMapearMovimentacaoRevendedoraQuandoNaoForNula() {
            Revendedora revendedora = new Revendedora();
            revendedora.setId(1L);
            revendedora.setNome("Revendedora Teste");

            MovimentacaoRevendedora movimentacaoRevendedora = new MovimentacaoRevendedora();
            movimentacaoRevendedora.setRevendedora(revendedora);
            movimentacaoRevendedora.setMovimentacoes(List.of());

            Movimentacao entity = new Movimentacao();
            entity.setIdMovimentacao(1L);
            entity.setJoia(new Joia());
            entity.setTipoMovimentacao(new TipoMovimentacao());
            entity.setUsuario(new Usuario());
            entity.setFornecedor(null);
            entity.setMovimentacaoRevendedora(movimentacaoRevendedora);

            MovimentacaoResponseDto resultado = MovimentacaoMapper.toResponse(entity);

            assertNotNull(resultado.getMovimentacaoRevendedora());
        }

        @Test
        @DisplayName("Deve retornar movimentacaoRevendedora nula quando for nula")
        void deveRetornarMovimentacaoRevendedoraNulaQuandoForNula() {
            Movimentacao entity = new Movimentacao();
            entity.setIdMovimentacao(1L);
            entity.setJoia(new Joia());
            entity.setTipoMovimentacao(new TipoMovimentacao());
            entity.setUsuario(new Usuario());
            entity.setFornecedor(null);
            entity.setMovimentacaoRevendedora(null);

            MovimentacaoResponseDto resultado = MovimentacaoMapper.toResponse(entity);

            assertNull(resultado.getMovimentacaoRevendedora());
        }
    }

    @Nested
    @DisplayName("toReponseList")
    class toReponseList {

        @Test
        @DisplayName("Deve mapear lista de entidades para lista de DTOs")
        void deveMapearListaDeEntidadesParaListaDeDtos() {
            Movimentacao entity1 = new Movimentacao();
            entity1.setIdMovimentacao(1L);
            entity1.setJoia(new Joia());
            entity1.setTipoMovimentacao(new TipoMovimentacao());
            entity1.setUsuario(new Usuario());

            Movimentacao entity2 = new Movimentacao();
            entity2.setIdMovimentacao(2L);
            entity2.setJoia(new Joia());
            entity2.setTipoMovimentacao(new TipoMovimentacao());
            entity2.setUsuario(new Usuario());

            List<MovimentacaoResponseDto> resultado = MovimentacaoMapper.toReponseList(List.of(entity1, entity2));

            assertNotNull(resultado);
            assertEquals(2, resultado.size());
            assertEquals(1L, resultado.get(0).getId());
            assertEquals(2L, resultado.get(1).getId());
        }

        @Test
        @DisplayName("Deve retornar lista vazia quando lista de entidades estiver vazia")
        void deveRetornarListaVaziaQuandoListaDeEntidadesEstiverVazia() {
            List<MovimentacaoResponseDto> resultado = MovimentacaoMapper.toReponseList(List.of());

            assertNotNull(resultado);
            assertTrue(resultado.isEmpty());
        }
    }

    @Nested
    @DisplayName("toEntity")
    class toEntity {

        @Test
        @DisplayName("Deve mapear quantidade do DTO para a entidade")
        void deveMapearQuantidadeDoDto() {
            MovimentacaoRequestDto dto = new MovimentacaoRequestDto();
            dto.setQuantidade(7);

            Movimentacao resultado = MovimentacaoMapper.toEntity(dto);

            assertNotNull(resultado);
            assertEquals(7, resultado.getQuantidade());
        }

        @Test
        @DisplayName("Deve retornar entidade com demais campos nulos quando DTO não os informar")
        void deveRetornarEntidadeComDemaisCamposNulos() {
            MovimentacaoRequestDto dto = new MovimentacaoRequestDto();
            dto.setQuantidade(1);

            Movimentacao resultado = MovimentacaoMapper.toEntity(dto);

            assertNull(resultado.getJoia());
            assertNull(resultado.getFornecedor());
            assertNull(resultado.getTipoMovimentacao());
            assertNull(resultado.getUsuario());
            assertNull(resultado.getMovimentacaoRevendedora());
        }
    }
}