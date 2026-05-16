package com.caeca.estoque.service.joia;

import com.caeca.estoque.database.model.joia.Joia;
import com.caeca.estoque.database.repository.joia.JoiaRepository;
import com.caeca.estoque.dto.joia.JoiaAtualizacaoDTO;
import com.caeca.estoque.dto.joia.JoiaEntradaDTO;
import com.caeca.estoque.dto.joia.JoiaMapper;
import com.caeca.estoque.dto.joia.JoiaSaidaDTO;
import com.caeca.estoque.service.Material.MaterialService;
import com.caeca.estoque.service.banho.BanhoService;
import com.caeca.estoque.service.categoria.CategoriaService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JoiaServiceTest {

    @Mock private JoiaRepository joiaRepository;
    @Mock private BanhoService banhoService;
    @Mock private CategoriaService categoriaService;
    @Mock private MaterialService materialService;

    @InjectMocks private JoiaService joiaService;

    @Nested
    @DisplayName("listarTodasAsJoias")
    class listarTodasAsJoias {
        @Test
        @DisplayName("Deve retornar lista de joias com sucesso quando existirem dados")
        void deveLancarExcecaoQuandoListaCheia() {
            when(joiaRepository.findAll()).thenReturn(List.of(new Joia()));

            List<JoiaSaidaDTO> resultado = joiaService.listarTodasAsJoias();

            assertNotNull(resultado);
            assertFalse(resultado.isEmpty());
            assertEquals(1, resultado.size());
            verify(joiaRepository, times(1)).findAll();
        }

        @Test
        @DisplayName("Deve lançar ResponseStatusException 404 quando a lista estiver vazia")
        void deveLancarExcecaoQuandoListaVazia() {
            when(joiaRepository.findAll()).thenReturn(List.of());

            assertThrows(ResponseStatusException.class, () -> {
                joiaService.listarTodasAsJoias();
            });
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

            JoiaSaidaDTO resultado = joiaService.listarJoiaPorId(anyLong());

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
            JoiaSaidaDTO joiaDto = new JoiaSaidaDTO();

            when(joiaRepository.save(any(Joia.class))).thenReturn(joia);

            JoiaSaidaDTO resultado = joiaService.criarJoia(new JoiaEntradaDTO());

            assertEquals(joiaDto.getClass(), resultado.getClass());
        }
    }

    @Nested
    @DisplayName("atualizarJoiaPorId")
    class atualizarJoiaPorId{
        @Test
        @DisplayName("Deve atualizar os dados da joia com sucesso")
        void atualizarJoiaPorId() {
            Joia joia = new Joia();
            when(joiaRepository.findById(anyLong())).thenReturn(Optional.of(joia));
            when(joiaRepository.save(any(Joia.class))).thenReturn(joia);

            JoiaSaidaDTO resultado = joiaService.atualizarJoiaPorId(1L,
                    new JoiaAtualizacaoDTO("a", 2, 10, 1L, 1, 1, 10.0, true));

            assertEquals(10, resultado.getQuantidade());
        }

        @Test
        @DisplayName("Deve lançar exceção quando não encontrar a joia para atualização")
        void deveLancarExcecaoQuandoNaoEncontrarAJoia(){
            when(joiaRepository.findById(anyLong())).thenReturn(Optional.empty());

            assertThrows(RuntimeException.class,
                    () -> joiaService.atualizarJoiaPorId(1L, new JoiaAtualizacaoDTO()));
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