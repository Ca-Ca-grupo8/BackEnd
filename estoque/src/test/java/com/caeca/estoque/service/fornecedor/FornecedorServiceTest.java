package com.caeca.estoque.service.fornecedor;

import com.caeca.estoque.database.model.fornecedores.Fornecedor;
import com.caeca.estoque.database.repository.fornecedor.FornecedorRepository;
import com.caeca.estoque.dto.fornecedor.FornecedorCreatedResponseDto;
import com.caeca.estoque.dto.fornecedor.FornecedorRequestDto;
import com.caeca.estoque.dto.fornecedor.FornecedorResponseDto;
import com.caeca.estoque.dto.fornecedor.FornecedorStatusDto;
import com.caeca.estoque.exception.FornecedorNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FornecedorServiceTest {
    @Mock
    private FornecedorRepository fornecedorRepository;

    @InjectMocks
    private FornecedorService fornecedorService;

    private Fornecedor fornecedor;
    private FornecedorRequestDto requestDTO;

    @BeforeEach
    void setUp() {
        fornecedor = new Fornecedor();
        fornecedor.setId(1L);
        fornecedor.setNome("Joias Brasil");
        fornecedor.setTelefone("11999990000");
        fornecedor.setInstagram("@joiasbrasil");
        fornecedor.setIsAtivo(true);
        fornecedor.setDataCadastro(LocalDateTime.now());

        requestDTO = new FornecedorRequestDto();
        requestDTO.setNome("Joias Brasil");
        requestDTO.setTelefone("11999990000");
        requestDTO.setInstagram("@joiasbrasil");
    }

    @Test
    @DisplayName("sem filtros retorna todos os fornecedores")
    void listarSemFiltrosRetornaTodos() {
        when(fornecedorRepository.findAll(any(Pageable.class))).thenReturn(new PageImpl<>(List.of(fornecedor)));

        Page<FornecedorResponseDto> resultado = fornecedorService.listar(null, null, PageRequest.of(0, 10));

        assertEquals(1, resultado.getContent().size());
        assertEquals("Joias Brasil", resultado.getContent().get(0).getNome());
        verify(fornecedorRepository).findAll(any(Pageable.class));
    }

    @Test
    @DisplayName("filtro por isAtivo chama metodo correto")
    void listarComIsAtivoChamaFindByIsAtivo() {
        when(fornecedorRepository.findByIsAtivo(eq(true), any(Pageable.class))).thenReturn(new PageImpl<>(List.of(fornecedor)));

        Page<FornecedorResponseDto> resultado = fornecedorService.listar(true, null, PageRequest.of(0, 10));

        assertEquals(1, resultado.getContent().size());
        verify(fornecedorRepository).findByIsAtivo(eq(true), any(Pageable.class));
    }

    @Test
    @DisplayName("filtro por nome chama metodo correto")
    void listarComNomeChamaFindByNome() {
        when(fornecedorRepository.findByNomeContainingIgnoreCase(eq("joias"), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(fornecedor)));

        Page<FornecedorResponseDto> resultado = fornecedorService.listar(null, "joias", PageRequest.of(0, 10));

        assertEquals(1, resultado.getContent().size());
        verify(fornecedorRepository).findByNomeContainingIgnoreCase(eq("joias"), any(Pageable.class));
    }

    @Test
    @DisplayName("filtro por isAtivo e nome chama metodo correto")
    void listarComIsAtivoENomeChamaFindByIsAtivoAndNome() {
        when(fornecedorRepository.findByIsAtivoAndNomeContainingIgnoreCase(eq(true), eq("joias"), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(fornecedor)));

        Page<FornecedorResponseDto> resultado = fornecedorService.listar(true, "joias", PageRequest.of(0, 10));

        assertEquals(1, resultado.getContent().size());
        verify(fornecedorRepository).findByIsAtivoAndNomeContainingIgnoreCase(eq(true), eq("joias"), any(Pageable.class));
    }

    @Test
    @DisplayName("existente retorna DTO corretamente")
    void buscarPorIdExistenteRetornaDTO() {
        when(fornecedorRepository.findById(1L)).thenReturn(Optional.of(fornecedor));

        FornecedorResponseDto resultado = fornecedorService.buscarPorId(1L);

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

        FornecedorCreatedResponseDto resultado = fornecedorService.criar(requestDTO);

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

        FornecedorCreatedResponseDto resultado = fornecedorService.criar(requestDTO);

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

        FornecedorResponseDto resultado = fornecedorService.atualizar(1L, requestDTO);

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
        FornecedorStatusDto statusDTO = new FornecedorStatusDto(false);

        when(fornecedorRepository.findById(1L)).thenReturn(Optional.of(fornecedor));

        fornecedorService.atualizarStatus(1L, statusDTO);

        assertFalse(fornecedor.getIsAtivo());
        verify(fornecedorRepository).save(fornecedor);
    }

    @Test
    @DisplayName("id inexistente lanca FornecedorNotFoundException")
    void atualizarStatusIdInexistenteLancaException() {
        FornecedorStatusDto statusDTO = new FornecedorStatusDto(false);

        when(fornecedorRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(FornecedorNotFoundException.class,
                () -> fornecedorService.atualizarStatus(99L, statusDTO));
    }
}
