package com.caeca.estoque.service.banho;

import com.caeca.estoque.database.model.banho.Banho;
import com.caeca.estoque.database.repository.banho.BanhoRepository;
import com.caeca.estoque.dto.banho.BanhoRequestDto;
import com.caeca.estoque.exception.NotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BanhoServiceTest {

    @Mock
    private BanhoRepository banhoRepository;

    @InjectMocks
    private BanhoService banhoService;

    @Nested
    @DisplayName("listar")
    class listar {

        @Test
        @DisplayName("Deve retornar lista de banhos com sucesso quando existirem dados")
        void deveRetornarListaQuandoExistiremDados() {
            when(banhoRepository.findAll()).thenReturn(List.of(new Banho()));

            List<Banho> resultado = banhoService.listar();

            assertNotNull(resultado);
            assertFalse(resultado.isEmpty());
            assertEquals(1, resultado.size());
            verify(banhoRepository, times(1)).findAll();
        }

        @Test
        @DisplayName("Deve retornar lista vazia quando não existirem dados")
        void deveRetornarListaVaziaQuandoNaoExistiremDados() {
            when(banhoRepository.findAll()).thenReturn(List.of());

            List<Banho> resultado = banhoService.listar();

            assertNotNull(resultado);
            assertTrue(resultado.isEmpty());
            verify(banhoRepository, times(1)).findAll();
        }
    }

    @Nested
    @DisplayName("buscarPorId")
    class buscarPorId {

        @Test
        @DisplayName("Deve retornar objeto quando encontrar banho pelo id")
        void deveRetornarObjetoQuandoEncontrarBanhoPeloId() throws NotFoundException {
            Banho banho = new Banho();
            when(banhoRepository.findById(anyInt())).thenReturn(Optional.of(banho));

            Banho resultado = banhoService.buscarPorId(1);

            assertNotNull(resultado);
            assertEquals(banho, resultado);
            verify(banhoRepository, times(1)).findById(1);
        }

        @Test
        @DisplayName("Deve lançar NotFoundException quando não encontrar banho pelo id")
        void deveLancarNotFoundExceptionQuandoNaoEncontrarBanhoPeloId() {
            when(banhoRepository.findById(anyInt())).thenReturn(Optional.empty());

            assertThrows(NotFoundException.class, () -> banhoService.buscarPorId(1));
            verify(banhoRepository, times(1)).findById(1);
        }
    }

    @Nested
    @DisplayName("cadastrar")
    class cadastrar {

        @Test
        @DisplayName("Deve cadastrar banho com sucesso quando nome não existir")
        void deveCadastrarBanhoComSucessoQuandoNomeNaoExistir() {
            BanhoRequestDto request = new BanhoRequestDto();
            request.setNome("Ouro");

            Banho banhoSalvo = new Banho();
            banhoSalvo.setNome("Ouro");

            when(banhoRepository.existsByNomeIgnoreCase("Ouro")).thenReturn(false);
            when(banhoRepository.save(any(Banho.class))).thenReturn(banhoSalvo);

            Banho resultado = banhoService.cadastrar(request);

            assertNotNull(resultado);
            assertEquals("Ouro", resultado.getNome());
            verify(banhoRepository, times(1)).existsByNomeIgnoreCase("Ouro");
            verify(banhoRepository, times(1)).save(any(Banho.class));
        }

        @Test
        @DisplayName("Deve lançar ResponseStatusException 409 quando nome já existir")
        void deveLancarResponseStatusExceptionQuandoNomeJaExistir() {
            BanhoRequestDto request = new BanhoRequestDto();
            request.setNome("Ouro");

            when(banhoRepository.existsByNomeIgnoreCase("Ouro")).thenReturn(true);

            ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                    () -> banhoService.cadastrar(request));

            assertEquals(409, exception.getStatusCode().value());
            verify(banhoRepository, never()).save(any(Banho.class));
        }
    }
}