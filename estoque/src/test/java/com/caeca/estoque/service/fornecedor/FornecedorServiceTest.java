package com.caeca.estoque.service.fornecedor;

import com.caeca.estoque.database.model.fornecedores.Fornecedor;
import com.caeca.estoque.database.repository.fornecedor.FornecedorRepository;
import com.caeca.estoque.dto.fornecedor.FornecedorCreatedResponseDTO;
import com.caeca.estoque.dto.fornecedor.FornecedorRequestDTO;
import com.caeca.estoque.dto.fornecedor.FornecedorResponseDTO;
import com.caeca.estoque.dto.fornecedor.FornecedorStatusDTO;
import com.caeca.estoque.exception.FornecedorNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class FornecedorServiceTest {
    @Mock
    private FornecedorRepository fornecedorRepository;

    @InjectMocks
    private FornecedorService fornecedorService;

    private Fornecedor fornecedor;
    private FornecedorRequestDTO requestDTO;

    @BeforeEach
    void setUp() {
        fornecedor = new Fornecedor();
        fornecedor.setId(1L);
        fornecedor.setNome("Joias Brasil");
        fornecedor.setTelefone("11999990000");
        fornecedor.setInstagram("@joiasbrasil");
        fornecedor.setIsAtivo(true);
        fornecedor.setDataCadastro(LocalDateTime.now());

        requestDTO = new FornecedorRequestDTO();
        requestDTO.setNome("Joias Brasil");
        requestDTO.setTelefone("11999990000");
        requestDTO.setInstagram("@joiasbrasil");
    }

    // ── listar ────────────────────────────────────────────────────────────────

    @Test
    @DisplayName("listar: sem filtros retorna todos os fornecedores")
    void listar_semFiltros_retornaTodos() {
        when(fornecedorRepository.findAll()).thenReturn(List.of(fornecedor));

        List<FornecedorResponseDTO> resultado = fornecedorService.listar(null, null);

        assertEquals(1, resultado.size());
        assertEquals("Joias Brasil", resultado.get(0).getNome());
        verify(fornecedorRepository).findAll();
    }

    @Test
    @DisplayName("listar: filtro por isAtivo chama metodo correto")
    void listar_comIsAtivo_chamaFindByIsAtivo() {
        when(fornecedorRepository.findByIsAtivo(true)).thenReturn(List.of(fornecedor));

        List<FornecedorResponseDTO> resultado = fornecedorService.listar(true, null);

        assertEquals(1, resultado.size());
        verify(fornecedorRepository).findByIsAtivo(true);
    }

    @Test
    @DisplayName("listar: filtro por nome chama metodo correto")
    void listar_comNome_chamaFindByNome() {
        when(fornecedorRepository.findByNomeContainingIgnoreCase("joias")).thenReturn(List.of(fornecedor));

        List<FornecedorResponseDTO> resultado = fornecedorService.listar(null, "joias");

        assertEquals(1, resultado.size());
        verify(fornecedorRepository).findByNomeContainingIgnoreCase("joias");
    }

    @Test
    @DisplayName("listar: filtro por isAtivo e nome chama metodo correto")
    void listar_comIsAtivoENome_chamaFindByIsAtivoAndNome() {
        when(fornecedorRepository.findByIsAtivoAndNomeContainingIgnoreCase(true, "joias"))
                .thenReturn(List.of(fornecedor));

        List<FornecedorResponseDTO> resultado = fornecedorService.listar(true, "joias");

        assertEquals(1, resultado.size());
        verify(fornecedorRepository).findByIsAtivoAndNomeContainingIgnoreCase(true, "joias");
    }

    // ── buscarPorId ───────────────────────────────────────────────────────────

    @Test
    @DisplayName("buscarPorId: id existente retorna DTO corretamente")
    void buscarPorId_idExistente_retornaDTO() {
        when(fornecedorRepository.findById(1L)).thenReturn(Optional.of(fornecedor));

        FornecedorResponseDTO resultado = fornecedorService.buscarPorId(1L);

        assertEquals(1L, resultado.getIdFornecedor());
        assertEquals("Joias Brasil", resultado.getNome());
        assertEquals("11999990000", resultado.getTelefone());
        assertEquals("@joiasbrasil", resultado.getInstagram());
    }

    @Test
    @DisplayName("buscarPorId: id inexistente lanca FornecedorNotFoundException")
    void buscarPorId_idInexistente_lancaException() {
        when(fornecedorRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(FornecedorNotFoundException.class, () -> fornecedorService.buscarPorId(99L));
    }

    // ── criar ─────────────────────────────────────────────────────────────────

    @Test
    @DisplayName("criar: salva fornecedor e retorna DTO com dados corretos")
    void criar_dadosValidos_retornaCreatedDTO() {
        when(fornecedorRepository.save(any(Fornecedor.class))).thenReturn(fornecedor);

        FornecedorCreatedResponseDTO resultado = fornecedorService.criar(requestDTO);

        assertEquals(1L, resultado.getIdFornecedor());
        assertEquals("Joias Brasil", resultado.getNome());
        assertTrue(resultado.getAtivo());
        verify(fornecedorRepository).save(any(Fornecedor.class));
    }

    @Test
    @DisplayName("criar: mapeia nome, telefone e instagram corretamente")
    void criar_mapeiaAtributosCorretamente() {
        when(fornecedorRepository.save(any(Fornecedor.class))).thenAnswer(invocation -> {
            Fornecedor f = invocation.getArgument(0);
            f.setId(1L);
            f.setIsAtivo(true);
            f.setDataCadastro(LocalDateTime.now());
            return f;
        });

        FornecedorCreatedResponseDTO resultado = fornecedorService.criar(requestDTO);

        assertEquals("Joias Brasil", resultado.getNome());
        assertNotNull(resultado.getDataCadastro());
    }

    // ── atualizar ─────────────────────────────────────────────────────────────

    @Test
    @DisplayName("atualizar: id existente atualiza e retorna DTO")
    void atualizar_idExistente_atualizaDados() {
        requestDTO.setNome("Novo Nome");
        requestDTO.setTelefone("11888880000");
        requestDTO.setInstagram("@novonome");

        when(fornecedorRepository.findById(1L)).thenReturn(Optional.of(fornecedor));
        when(fornecedorRepository.save(any(Fornecedor.class))).thenReturn(fornecedor);

        FornecedorResponseDTO resultado = fornecedorService.atualizar(1L, requestDTO);

        assertEquals("Novo Nome", resultado.getNome());
        verify(fornecedorRepository).save(fornecedor);
    }

    @Test
    @DisplayName("atualizar: id inexistente lanca FornecedorNotFoundException")
    void atualizar_idInexistente_lancaException() {
        when(fornecedorRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(FornecedorNotFoundException.class,
                () -> fornecedorService.atualizar(99L, requestDTO));
    }

    // ── atualizarStatus ───────────────────────────────────────────────────────

    @Test
    @DisplayName("atualizarStatus: desativa fornecedor existente")
    void atualizarStatus_desativa_comSucesso() {
        FornecedorStatusDTO statusDTO = new FornecedorStatusDTO(false);

        when(fornecedorRepository.findById(1L)).thenReturn(Optional.of(fornecedor));

        fornecedorService.atualizarStatus(1L, statusDTO);

        assertFalse(fornecedor.getIsAtivo());
        verify(fornecedorRepository).save(fornecedor);
    }

    @Test
    @DisplayName("atualizarStatus: id inexistente lanca FornecedorNotFoundException")
    void atualizarStatus_idInexistente_lancaException() {
        FornecedorStatusDTO statusDTO = new FornecedorStatusDTO(false);

        when(fornecedorRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(FornecedorNotFoundException.class,
                () -> fornecedorService.atualizarStatus(99L, statusDTO));
    }
}
