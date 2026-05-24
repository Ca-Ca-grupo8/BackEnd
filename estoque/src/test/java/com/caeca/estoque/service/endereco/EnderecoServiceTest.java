package com.caeca.estoque.service.endereco;

import com.caeca.estoque.client.ViaCepClient;
import com.caeca.estoque.database.model.endereco.Endereco;
import com.caeca.estoque.database.repository.endereco.EnderecoRepository;
import com.caeca.estoque.dto.endereco.EnderecoRequestDTO;
import com.caeca.estoque.exception.EnderecoNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EnderecoServiceTest {

    @Mock
    private EnderecoRepository enderecoRepository;

    @Mock
    private ViaCepClient viaCepClient;

    @InjectMocks
    private EnderecoService enderecoService;

    private Endereco endereco;
    private EnderecoRequestDTO requestDTO;

    @BeforeEach
    void setUp() {
        endereco = new Endereco();
        endereco.setId(1L);
        endereco.setCep("01310100");
        endereco.setLogradouro("Avenida Paulista");
        endereco.setNumero(1000);
        endereco.setComplemento("Apto 42");
        endereco.setBairro("Bela Vista");
        endereco.setCidade("São Paulo");
        endereco.setUf("SP");

        requestDTO = new EnderecoRequestDTO();
        requestDTO.setCep("01310100");
        requestDTO.setLogradouro("Avenida Paulista");
        requestDTO.setNumero(1000);
        requestDTO.setComplemento("Apto 42");
        requestDTO.setBairro("Bela Vista");
        requestDTO.setCidade("São Paulo");
        requestDTO.setUf("SP");
    }

    @Nested
    @DisplayName("listarTodos")
    class ListarTodos {

        @Test
        @DisplayName("Deve retornar lista de enderecos")
        void deveRetornarListaDeEnderecos() {
            when(enderecoRepository.findAll()).thenReturn(List.of(endereco));

            List<Endereco> resultado = enderecoService.listarTodos();

            assertEquals(1, resultado.size());
            assertEquals("Avenida Paulista", resultado.get(0).getLogradouro());
            verify(enderecoRepository).findAll();
        }

        @Test
        @DisplayName("Deve retornar lista vazia quando nao ha enderecos")
        void deveRetornarListaVazia() {
            when(enderecoRepository.findAll()).thenReturn(List.of());

            List<Endereco> resultado = enderecoService.listarTodos();

            assertTrue(resultado.isEmpty());
            verify(enderecoRepository).findAll();
        }
    }

    @Nested
    @DisplayName("buscarPorId")
    class BuscarPorId {

        @Test
        @DisplayName("Deve retornar endereco quando id existente")
        void deveRetornarEnderecoQuandoIdExistente() {
            when(enderecoRepository.findById(1L)).thenReturn(Optional.of(endereco));

            Endereco resultado = enderecoService.buscarPorId(1L);

            assertEquals(1L, resultado.getId());
            assertEquals("01310100", resultado.getCep());
            assertEquals("Avenida Paulista", resultado.getLogradouro());
            verify(enderecoRepository).findById(1L);
        }

        @Test
        @DisplayName("Deve lancar EnderecoNotFoundException quando id inexistente")
        void deveLancarExceptionQuandoIdInexistente() {
            when(enderecoRepository.findById(99L)).thenReturn(Optional.empty());

            assertThrows(EnderecoNotFoundException.class,
                    () -> enderecoService.buscarPorId(99L));
        }
    }

    @Nested
    @DisplayName("criar")
    class Criar {

        @Test
        @DisplayName("Deve salvar e retornar o endereco criado")
        void deveSalvarERetornarEnderecoCriado() {
            when(enderecoRepository.save(any(Endereco.class))).thenReturn(endereco);

            Endereco resultado = enderecoService.criar(requestDTO);

            assertEquals("01310100", resultado.getCep());
            assertEquals("Avenida Paulista", resultado.getLogradouro());
            verify(enderecoRepository).save(any(Endereco.class));
        }
    }

    @Nested
    @DisplayName("atualizar")
    class Atualizar {

        @Test
        @DisplayName("Deve atualizar e retornar o endereco")
        void deveAtualizarERetornarEndereco() {
            requestDTO.setLogradouro("Rua Nova");
            requestDTO.setNumero(500);

            when(enderecoRepository.findById(1L)).thenReturn(Optional.of(endereco));
            when(enderecoRepository.save(any(Endereco.class))).thenReturn(endereco);

            Endereco resultado = enderecoService.atualizar(1L, requestDTO);

            assertEquals("Rua Nova", resultado.getLogradouro());
            assertEquals(500, resultado.getNumero());
            verify(enderecoRepository).save(endereco);
        }

        @Test
        @DisplayName("Deve lancar EnderecoNotFoundException quando id inexistente")
        void deveLancarExceptionQuandoIdInexistente() {
            when(enderecoRepository.findById(99L)).thenReturn(Optional.empty());

            assertThrows(EnderecoNotFoundException.class,
                    () -> enderecoService.atualizar(99L, requestDTO));
        }
    }

    @Nested
    @DisplayName("deletar")
    class Deletar {

        @Test
        @DisplayName("Deve deletar endereco existente")
        void deveDeletarEnderecoExistente() {
            when(enderecoRepository.existsById(1L)).thenReturn(true);

            enderecoService.deletar(1L);

            verify(enderecoRepository).deleteById(1L);
        }

        @Test
        @DisplayName("Deve lancar EnderecoNotFoundException quando id inexistente")
        void deveLancarExceptionQuandoIdInexistente() {
            when(enderecoRepository.existsById(99L)).thenReturn(false);

            assertThrows(EnderecoNotFoundException.class,
                    () -> enderecoService.deletar(99L));

            verify(enderecoRepository, never()).deleteById(any());
        }
    }
}