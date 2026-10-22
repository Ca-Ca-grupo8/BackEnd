package com.caeca.estoque.service.statusMaleta;

import com.caeca.estoque.database.model.statusMaleta.StatusMaleta;
import com.caeca.estoque.database.model.statusMaleta.StatusMaletaEnum;
import com.caeca.estoque.database.repository.statusMaleta.StatusMaletaRepository;
import com.caeca.estoque.exception.NotFoundException;
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
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StatusMaletaServiceTest {

    @Mock
    private StatusMaletaRepository statusMaletaRepository;

    @InjectMocks
    private StatusMaletaService statusMaletaService;

    @Nested
    @DisplayName("listarTodos")
    class listarTodos {

        @Test
        @DisplayName("Deve retornar lista de status com sucesso quando existirem dados")
        void deveRetornarListaQuandoExistiremDados() {
            when(statusMaletaRepository.findAll()).thenReturn(List.of(
                    new StatusMaleta(1L, StatusMaletaEnum.EM_PREPARACAO),
                    new StatusMaleta(2L, StatusMaletaEnum.FINALIZADA)
            ));

            List<StatusMaleta> resultado = statusMaletaService.listarTodos();

            assertNotNull(resultado);
            assertEquals(2, resultado.size());
            assertEquals(StatusMaletaEnum.EM_PREPARACAO, resultado.get(0).getNome());
            verify(statusMaletaRepository, times(1)).findAll();
        }

        @Test
        @DisplayName("Deve retornar lista vazia quando não existirem dados")
        void deveRetornarListaVaziaQuandoNaoExistiremDados() {
            when(statusMaletaRepository.findAll()).thenReturn(List.of());

            List<StatusMaleta> resultado = statusMaletaService.listarTodos();

            assertNotNull(resultado);
            assertTrue(resultado.isEmpty());
            verify(statusMaletaRepository, times(1)).findAll();
        }
    }

    @Nested
    @DisplayName("buscarPorNome")
    class buscarPorNome {

        @Test
        @DisplayName("Deve retornar o status quando ele existir na base")
        void deveRetornarStatusQuandoExistirNaBase() {
            StatusMaleta aberto = new StatusMaleta(1L, StatusMaletaEnum.EM_PREPARACAO);
            when(statusMaletaRepository.findByNome(StatusMaletaEnum.EM_PREPARACAO))
                    .thenReturn(Optional.of(aberto));

            StatusMaleta resultado = statusMaletaService.buscarPorNome(StatusMaletaEnum.EM_PREPARACAO);

            assertEquals(aberto, resultado);
            verify(statusMaletaRepository, times(1)).findByNome(StatusMaletaEnum.EM_PREPARACAO);
        }

        @Test
        @DisplayName("Deve lançar NotFoundException quando o status não existir na base")
        void deveLancarNotFoundExceptionQuandoNaoExistirNaBase() {
            when(statusMaletaRepository.findByNome(StatusMaletaEnum.ERRO))
                    .thenReturn(Optional.empty());

            assertThrows(NotFoundException.class,
                    () -> statusMaletaService.buscarPorNome(StatusMaletaEnum.ERRO));
        }
    }

    @Nested
    @DisplayName("buscarPorId")
    class buscarPorId {

        @Test
        @DisplayName("Deve retornar o status quando ele existir na base")
        void deveRetornarStatusQuandoExistirNaBase() {
            StatusMaleta aberto = new StatusMaleta(1L, StatusMaletaEnum.EM_PREPARACAO);
            when(statusMaletaRepository.findById(1L)).thenReturn(Optional.of(aberto));

            StatusMaleta resultado = statusMaletaService.buscarPorId(1L);

            assertEquals(aberto, resultado);
            verify(statusMaletaRepository, times(1)).findById(1L);
        }

        @Test
        @DisplayName("Deve lançar NotFoundException quando o status não existir na base")
        void deveLancarNotFoundExceptionQuandoNaoExistirNaBase() {
            when(statusMaletaRepository.findById(99L)).thenReturn(Optional.empty());

            assertThrows(NotFoundException.class,
                    () -> statusMaletaService.buscarPorId(99L));
        }
    }
}
