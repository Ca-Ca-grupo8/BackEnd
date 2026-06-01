package com.caeca.estoque.service.categoria;

import com.caeca.estoque.database.model.categoria.Categoria;
import com.caeca.estoque.database.repository.categoria.CategoriaRepository;
import com.caeca.estoque.dto.categoria.CategoriaSaidaDTO;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class CategoriaServiceTest {

    @Mock
    private CategoriaRepository categoriaRepository;

    @InjectMocks
    private CategoriaService categoriaService;

    @Test
    @DisplayName("Deve listar categorias")
    void deveListarCategorias() {

        Categoria categoria = new Categoria();
        categoria.setId(1L);
        categoria.setNome("Anéis");

        Mockito.when(categoriaRepository.findAll())
                .thenReturn(List.of(categoria));

        List<CategoriaSaidaDTO> resultado =
                categoriaService.listarTodasAsCategorias();

        Assertions.assertEquals(1, resultado.size());
    }

    @Test
    @DisplayName("Deve lançar exceção ao listar categorias vazias")
    void deveLancarExcecaoAoListarCategoriasVazias() {

        Mockito.when(categoriaRepository.findAll())
                .thenReturn(List.of());

        Assertions.assertThrows(
                ResponseStatusException.class,
                () -> categoriaService.listarTodasAsCategorias()
        );
    }

    @Test
    @DisplayName("Deve retornar categoria por id")
    void deveRetornarCategoriaPorId() {

        Categoria categoria = new Categoria();
        categoria.setId(1L);
        categoria.setNome("Pulseiras");

        Mockito.when(categoriaRepository.findById(1L))
                .thenReturn(Optional.of(categoria));

        Categoria resultado =
                categoriaService.listarCategoriaPorId(1L);

        Assertions.assertEquals(1L, resultado.getId());
        Assertions.assertEquals("Pulseiras", resultado.getNome());
    }

    @Test
    @DisplayName("Deve lançar exceção quando categoria não existir")
    void deveLancarExcecaoQuandoCategoriaNaoExistir() {

        Mockito.when(categoriaRepository.findById(1L))
                .thenReturn(Optional.empty());

        Assertions.assertThrows(
                ResponseStatusException.class,
                () -> categoriaService.listarCategoriaPorId(1L)
        );
    }

    @Test
    @DisplayName("Deve criar categoria")
    void deveCriarCategoria() {

        Categoria categoria = new Categoria();
        categoria.setNome("Colares");

        Mockito.when(categoriaRepository.save(categoria))
                .thenReturn(categoria);

        CategoriaSaidaDTO resultado =
                categoriaService.criarCategoria(categoria);

        Assertions.assertEquals("Colares", resultado.getNome());
    }

    @Test
    @DisplayName("Deve lançar exceção quando nome estiver vazio")
    void deveLancarExcecaoQuandoNomeEstiverVazio() {

        Categoria categoria = new Categoria();
        categoria.setNome("");

        Assertions.assertThrows(
                ResponseStatusException.class,
                () -> categoriaService.criarCategoria(categoria)
        );
    }
}
