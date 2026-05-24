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
class FornecedorServiceTest {
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

    @Test
    @DisplayName("sem filtros retorna todos os fornecedores")
    void listarSemFiltrosRetornaTodos() {
        when(fornecedorRepository.findAll()).thenReturn(List.of(fornecedor));

        List<FornecedorResponseDTO> resultado = fornecedorService.listar(null, null);

        assertEquals(1, resultado.size());
        assertEquals("Joias Brasil", resultado.get(0).getNome());
        verify(fornecedorRepository).findAll();
    }

    @Test
    @DisplayName("filtro por isAtivo chama metodo correto")
    void listarComIsAtivoChamaFindByIsAtivo() {
        when(fornecedorRepository.findByIsAtivo(true)).thenReturn(List.of(fornecedor));

        List<FornecedorResponseDTO> resultado = fornecedorService.listar(true, null);

        assertEquals(1, resultado.size());
        verify(fornecedorRepository).findByIsAtivo(true);
    }

    @Test
    @DisplayName("filtro por nome chama metodo correto")
    void listarComNomeChamaFindByNome() {
        when(fornecedorRepository.findByNomeContainingIgnoreCase("joias")).thenReturn(List.of(fornecedor));

        List<FornecedorResponseDTO> resultado = fornecedorService.listar(null, "joias");

        assertEquals(1, resultado.size());
        verify(fornecedorRepository).findByNomeContainingIgnoreCase("joias");
    }

    @Test
    @DisplayName("filtro por isAtivo e nome chama metodo correto")
    void listarComIsAtivoENomeChamaFindByIsAtivoAndNome() {
        when(fornecedorRepository.findByIsAtivoAndNomeContainingIgnoreCase(true, "joias"))
                .thenReturn(List.of(fornecedor));

        List<FornecedorResponseDTO> resultado = fornecedorService.listar(true, "joias");

        assertEquals(1, resultado.size());
        verify(fornecedorRepository).findByIsAtivoAndNomeContainingIgnoreCase(true, "joias");
    }

    @Test
    @DisplayName("existente retorna DTO corretamente")
    void buscarPorIdExistenteRetornaDTO() {
        when(fornecedorRepository.findById(1L)).thenReturn(Optional.of(fornecedor));

        FornecedorResponseDTO resultado = fornecedorService.buscarPorId(1L);

        assertEquals(1L, resultado.getIdFornecedor());
        assertEquals("Joias Brasil", resultado.getNome());
        assertEquals("@joiasbrasil", resultado.getInstagram());
        assertEquals("11999990000", resultado.getTelefone());



    }

    @Test
    @DisplayName("id inexistente lanca FornecedorNotFoundException")
    void buscarPorIdInexistentelancaException() {
        when(fornecedorRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(FornecedorNotFoundException.class, () -> fornecedorService.buscarPorId(99L));
    }

    @Test
    @DisplayName("salva fornecedor e retorna DTO com dados corretos")
    void criarDadosValidosRetornaCreatedDTO() {
        when(fornecedorRepository.save(any(Fornecedor.class))).thenReturn(fornecedor);

        FornecedorCreatedResponseDTO resultado = fornecedorService.criar(requestDTO);

        assertEquals(1L, resultado.getIdFornecedor());
        assertEquals("Joias Brasil", resultado.getNome());
        assertTrue(resultado.getAtivo());
        verify(fornecedorRepository).save(any(Fornecedor.class));
    }

    @Test
    @DisplayName("mapeia nome, telefone e instagram corretamente")
    void criarMapeiaAtributosCorretamente() {
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

    @Test
    @DisplayName("id existente atualiza e retorna DTO")
    void atualizarIdExistenteAtualizaDados() {
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
    @DisplayName("id inexistente lanca FornecedorNotFoundException")
    void atualizarIdInexistenteLancaException() {
        when(fornecedorRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(FornecedorNotFoundException.class,
                () -> fornecedorService.atualizar(99L, requestDTO));
    }

    @Test
    @DisplayName("desativa fornecedor existente")
    void atualizarStatusDesativaComSucesso() {
        FornecedorStatusDTO statusDTO = new FornecedorStatusDTO(false);

        when(fornecedorRepository.findById(1L)).thenReturn(Optional.of(fornecedor));

        fornecedorService.atualizarStatus(1L, statusDTO);

        assertFalse(fornecedor.getIsAtivo());
        verify(fornecedorRepository).save(fornecedor);
    }

    @Test
    @DisplayName("id inexistente lanca FornecedorNotFoundException")
    void atualizarStatusIdInexistenteLancaException() {
        FornecedorStatusDTO statusDTO = new FornecedorStatusDTO(false);

        when(fornecedorRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(FornecedorNotFoundException.class,
                () -> fornecedorService.atualizarStatus(99L, statusDTO));
    }
}
