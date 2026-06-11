package com.caeca.estoque.service.Material;

import com.caeca.estoque.database.model.material.Material;
import com.caeca.estoque.database.repository.material.MaterialRepository;
import com.caeca.estoque.dto.material.MaterialRequestDto;
import com.caeca.estoque.exception.ConflictException;
import com.caeca.estoque.exception.NotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MaterialServiceTest {

    @Mock
    private MaterialRepository materialRepository;

    @InjectMocks
    private MaterialService materialService;

    private Material material;
    private MaterialRequestDto requestDto;

    @BeforeEach
    void setUp() {
        // Inicializa objetos comuns para os testes
        material = new Material();
        material.setIdMaterial(1);
        material.setNome("Cimento");

        requestDto = new MaterialRequestDto();
        requestDto.setNome("Cimento");
    }

    @Nested
    @DisplayName("Testes do método listar")
    class ListarTests {

        @Test
        @DisplayName("Deve retornar uma lista de materiais quando existirem registros")
        void deveRetornarListaDeMateriais() {
            // Arrange
            when(materialRepository.findAll()).thenReturn(List.of(material));

            // Act
            List<Material> resultado = materialService.listar();

            // Assert
            assertNotNull(resultado);
            assertEquals(1, resultado.size());
            assertEquals("Cimento", resultado.get(0).getNome());
            verify(materialRepository, times(1)).findAll();
        }

        @Test
        @DisplayName("Deve retornar lista vazia quando não houver materiais")
        void deveRetornarListaVazia() {
            when(materialRepository.findAll()).thenReturn(Collections.emptyList());

            List<Material> resultado = materialService.listar();

            assertTrue(resultado.isEmpty());
            verify(materialRepository, times(1)).findAll();
        }
    }

    @Nested
    @DisplayName("Testes do método listarPorId")
    class ListarPorIdTests {

        @Test
        @DisplayName("Deve retornar o material quando o ID existir")
        void deveRetornarMaterialQuandoIdExistir() throws NotFoundException {
            // Arrange
            when(materialRepository.findById(1)).thenReturn(Optional.of(material));

            // Act
            Material resultado = materialService.listarPorId(1);

            // Assert
            assertNotNull(resultado);
            assertEquals(1, resultado.getIdMaterial());
            assertEquals("Cimento", resultado.getNome());
            verify(materialRepository, times(1)).findById(1);
        }

        @Test
        @DisplayName("Deve lançar NotFoundException quando o ID não existir")
        void deveLancarExceptionQuandoIdNaoExistir() {
            // Arrange
            when(materialRepository.findById(99)).thenReturn(Optional.empty());

            // Act & Assert
            NotFoundException exception = assertThrows(NotFoundException.class, () -> {
                materialService.listarPorId(99);
            });

            assertEquals("Material não encontrado com id: 99", exception.getMessage());
            verify(materialRepository, times(1)).findById(99);
        }
    }

    @Nested
    @DisplayName("Testes do método cadastrar")
    class CadastrarTests {

        @Test
        @DisplayName("Deve cadastrar material com sucesso quando o nome for único")
        void deveCadastrarMaterialComSucesso() {
            // Arrange
            when(materialRepository.existsByNomeIgnoreCase(requestDto.getNome())).thenReturn(false);
            when(materialRepository.save(any(Material.class))).thenReturn(material);

            // Act
            Material resultado = materialService.cadastrar(requestDto);

            // Assert
            assertNotNull(resultado);
            assertEquals("Cimento", resultado.getNome());
            verify(materialRepository, times(1)).existsByNomeIgnoreCase(requestDto.getNome());
            verify(materialRepository, times(1)).save(any(Material.class));
        }

        @Test
        @DisplayName("Deve lançar ConflictException quando o nome já existir")
        void deveLancarExceptionQuandoNomeJaExistir() {
            // Arrange
            when(materialRepository.existsByNomeIgnoreCase(requestDto.getNome())).thenReturn(true);

            // Act & Assert
            ConflictException exception = assertThrows(ConflictException.class, () -> {
                materialService.cadastrar(requestDto);
            });

            assertEquals("Já existe um material com o nome: Cimento", exception.getMessage());
            // Garante que o save NUNCA foi chamado após detectar o conflito
            verify(materialRepository, never()).save(any(Material.class));
            verify(materialRepository, times(1)).existsByNomeIgnoreCase(requestDto.getNome());
        }
    }
}