package com.caeca.estoque.dto.joia;

import com.caeca.estoque.database.model.banho.Banho;
import com.caeca.estoque.database.model.categoria.Categoria;
import com.caeca.estoque.database.model.joia.Joia;
import com.caeca.estoque.database.model.material.Material;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class JoiaMapperTest {

    @Nested
    @DisplayName("toEntity")
    class ToEntity {

        @Test
        @DisplayName("Deve mapear JoiaEntradaDTO para Joia corretamente")
        void deveMapearDTOParaEntidade() {
            JoiaEntradaDTO dto = new JoiaEntradaDTO();
            dto.setNome("Anel Solitário Prata");
            dto.setQuantidade(15);
            dto.setQuantidadeMinima(5);

            Joia entidade = JoiaMapper.toEntity(dto);

            assertNotNull(entidade);
            assertEquals(dto.getNome(), entidade.getNome());
            assertEquals(dto.getQuantidade(), entidade.getQuantidade());
            assertEquals(dto.getQuantidadeMinima(), entidade.getQuantidadeMinima());
        }

        @Test
        @DisplayName("Deve retornar null quando o DTO de entrada for null")
        void deveRetornarNullQuandoDtoForNull() {
            assertNull(JoiaMapper.toEntity(null));
        }
    }

    @Nested
    @DisplayName("toResponse")
    class ToResponse {

        @Test
        @DisplayName("Deve mapear Joia para JoiaSaidaDTO corretamente com todos os campos")
        void deveMapearEntidadeParaResponseDTO() {
            Categoria categoria = new Categoria(1L, "Anel");
            Banho banho = new Banho(2, "Dourado");
            Material material = new Material(3, "Prata");

            Joia joia = new Joia();
            joia.setId(1L);
            joia.setNome("Colar Ponto de Luz");
            joia.setPreco(89.90);
            joia.setQuantidadeMinima(3);
            joia.setAtivo(true);
            joia.setDataCadastro(LocalDateTime.now());
            joia.setDataAtualizacao(LocalDateTime.now().plusMinutes(5));
            joia.setQuantidade(20);
            joia.setCategoria(categoria);
            joia.setBanho(banho);
            joia.setMaterial(material);

            JoiaSaidaDTO dto = JoiaMapper.toResponse(joia);

            assertNotNull(dto);
            assertEquals(joia.getId(), dto.getId());
            assertEquals(joia.getNome(), dto.getNome());
            assertEquals(joia.getPreco(), dto.getPreco());
            assertEquals(joia.getQuantidadeMinima(), dto.getQuantidadeMinima());
            assertEquals(joia.getAtivo(), dto.getAtivo());
            assertEquals(JoiaStatus.OK, dto.getStatus());
            assertEquals(joia.getDataCadastro(), dto.getDataCadastro());
            assertEquals(joia.getDataAtualizacao(), dto.getDataAtualizacao());
            assertEquals(joia.getQuantidade(), dto.getQuantidade());
            assertNotNull(dto.getCategoria());
            assertEquals("Anel", dto.getCategoria().getNome());
            assertNotNull(dto.getBanho());
            assertEquals("Dourado", dto.getBanho().getNome());
            assertNotNull(dto.getMaterial());
            assertEquals("Prata", dto.getMaterial().getNome());
        }

        @Test
        @DisplayName("Deve marcar como alerta quando a quantidade atingir a faixa mínima")
        void deveMapearStatusCriticoQuandoQuantidadeAtingiuMinimo() {
            Joia joia = new Joia();
            joia.setAtivo(true);
            joia.setQuantidade(3);
            joia.setQuantidadeMinima(3);

            JoiaSaidaDTO dto = JoiaMapper.toResponse(joia);

            assertEquals(JoiaStatus.ALERTA, dto.getStatus());
        }

        @Test
        @DisplayName("Deve marcar como critico quando a quantidade estiver zerada")
        void deveMapearStatusCriticoQuandoQuantidadeZerada() {
            Joia joia = new Joia();
            joia.setAtivo(true);
            joia.setQuantidade(0);
            joia.setQuantidadeMinima(3);

            JoiaSaidaDTO dto = JoiaMapper.toResponse(joia);

            assertEquals(JoiaStatus.CRITICO, dto.getStatus());
        }

        @Test
        @DisplayName("Deve retornar null quando a entidade Joia for null")
        void deveRetornarNullQuandoEntidadeForNull() {
            assertNull(JoiaMapper.toResponse(null));
        }
    }

    @Nested
    @DisplayName("toResponseList")
    class ToResponseList {

        @Test
        @DisplayName("Deve converter uma lista de Joia em uma lista de JoiaSaidaDTO")
        void deveConverterListaDeEntidadesParaListaDeDTOs() {
            Joia joia1 = new Joia();
            joia1.setId(1L);
            joia1.setNome("Joia 1");

            Joia joia2 = new Joia();
            joia2.setId(2L);
            joia2.setNome("Joia 2");

            List<Joia> listaEntidades = List.of(joia1, joia2);

            List<JoiaSaidaDTO> listaDTOS = JoiaMapper.toResponseList(listaEntidades);

            assertNotNull(listaDTOS);
            assertEquals(2, listaDTOS.size());
            assertEquals(1L, listaDTOS.get(0).getId());
            assertEquals("Joia 1", listaDTOS.get(0).getNome());
            assertEquals(2L, listaDTOS.get(1).getId());
            assertEquals("Joia 2", listaDTOS.get(1).getNome());
        }
    }
}