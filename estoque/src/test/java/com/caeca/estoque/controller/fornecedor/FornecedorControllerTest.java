package com.caeca.estoque.controller.fornecedor;

import com.caeca.estoque.dto.fornecedor.*;
import com.caeca.estoque.exception.FornecedorNotFoundException;
import com.caeca.estoque.handler.GlobalExceptionHandler;
import com.caeca.estoque.service.fornecedor.FornecedorService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;


import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest(FornecedorController.class)
@org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc(addFilters = false)
@Import(GlobalExceptionHandler.class)
class FornecedorControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private ObjectMapper objectMapper;

    @MockitoBean
    private FornecedorService fornecedorService;

    private FornecedorResponseDto responseDTO;
    private FornecedorRequestDto requestDTO;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        responseDTO = new FornecedorResponseDto(
                1L, "Joias Brasil", "@joiasbrasil", "11999990000", true, LocalDateTime.now()
        );

        requestDTO = new FornecedorRequestDto();
        requestDTO.setNome("Joias Brasil");
        requestDTO.setTelefone("11999990000");
        requestDTO.setInstagram("@joiasbrasil");
    }

    @Nested
    @DisplayName("listar")
    class Listar {

        @Test
        @DisplayName("Deve retornar status 200 e página de fornecedores")
        void deveRetornar200EPaginaDeFornecedores() throws Exception {
            Page<FornecedorResponseDto> page = new PageImpl<>(List.of(responseDTO));
            when(fornecedorService.listar(eq(null), eq(null), any(Pageable.class))).thenReturn(page);

            mockMvc.perform(get("/fornecedores")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.content").isArray())
                    .andExpect(jsonPath("$.content.length()").value(1));

            verify(fornecedorService, times(1)).listar(eq(null), eq(null), any(Pageable.class));
        }

        @Test
        @DisplayName("Deve retornar status 200 filtrando por isAtivo")
        void deveRetornar200ComFiltroIsAtivo() throws Exception {
            Page<FornecedorResponseDto> page = new PageImpl<>(List.of(responseDTO));
            when(fornecedorService.listar(eq(true), eq(null), any(Pageable.class))).thenReturn(page);

            mockMvc.perform(get("/fornecedores")
                            .param("is_ativo", "true")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.content.length()").value(1));

            verify(fornecedorService, times(1)).listar(eq(true), eq(null), any(Pageable.class));
        }

        @Test
        @DisplayName("Deve retornar status 200 filtrando por nome")
        void deveRetornar200ComFiltroNome() throws Exception {
            Page<FornecedorResponseDto> page = new PageImpl<>(List.of(responseDTO));
            when(fornecedorService.listar(eq(null), eq("joias"), any(Pageable.class))).thenReturn(page);

            mockMvc.perform(get("/fornecedores")
                            .param("nome", "joias")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.content.length()").value(1));

            verify(fornecedorService, times(1)).listar(eq(null), eq("joias"), any(Pageable.class));
        }
    }

    @Nested
    @DisplayName("buscarPorId")
    class BuscarPorId {

        @Test
        @DisplayName("Deve retornar status 200 e dados do fornecedor encontrado")
        void deveRetornar200EFornecedorPorId() throws Exception {
            when(fornecedorService.buscarPorId(1L)).thenReturn(responseDTO);

            mockMvc.perform(get("/fornecedores/{id}", 1L)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.idFornecedor").value(1))
                    .andExpect(jsonPath("$.nome").value("Joias Brasil"));

            verify(fornecedorService, times(1)).buscarPorId(1L);
        }

        @Test
        @DisplayName("Deve retornar status 404 quando fornecedor nao encontrado")
        void deveRetornar404QuandoNaoEncontrado() throws Exception {
            when(fornecedorService.buscarPorId(99L))
                    .thenThrow(new FornecedorNotFoundException(99L));

            mockMvc.perform(get("/fornecedores/{id}", 99L)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isNotFound());

            verify(fornecedorService, times(1)).buscarPorId(99L);
        }
    }

    @Nested
    @DisplayName("criar")
    class Criar {

        @Test
        @DisplayName("Deve retornar status 201 e dados do fornecedor criado")
        void deveRetornar201EFornecedorCriado() throws Exception {
            FornecedorCreatedResponseDto createdDTO = new FornecedorCreatedResponseDto(
                    1L, "Joias Brasil", true, LocalDateTime.now()
            );
            when(fornecedorService.criar(any(FornecedorRequestDto.class))).thenReturn(createdDTO);

            mockMvc.perform(post("/fornecedores")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(requestDTO)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.idFornecedor").value(1))
                    .andExpect(jsonPath("$.ativo").value(true));

            verify(fornecedorService, times(1)).criar(any(FornecedorRequestDto.class));
        }

        @Test
        @DisplayName("Deve retornar status 400 quando nome esta em branco")
        void deveRetornar400QuandoNomeEmBranco() throws Exception {
            requestDTO.setNome("");

            mockMvc.perform(post("/fornecedores")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(requestDTO)))
                    .andExpect(status().isBadRequest());

            verify(fornecedorService, never()).criar(any());
        }
    }

    @Nested
    @DisplayName("atualizar")
    class Atualizar {

        @Test
        @DisplayName("Deve retornar status 200 e dados atualizados do fornecedor")
        void deveRetornar200EFornecedorAtualizado() throws Exception {
            when(fornecedorService.atualizar(eq(1L), any(FornecedorRequestDto.class)))
                    .thenReturn(responseDTO);

            mockMvc.perform(put("/fornecedores/{id}", 1L)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(requestDTO)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.nome").value("Joias Brasil"));

            verify(fornecedorService, times(1)).atualizar(eq(1L), any(FornecedorRequestDto.class));
        }

        @Test
        @DisplayName("Deve retornar status 404 quando fornecedor nao encontrado")
        void deveRetornar404QuandoNaoEncontrado() throws Exception {
            when(fornecedorService.atualizar(eq(99L), any(FornecedorRequestDto.class)))
                    .thenThrow(new FornecedorNotFoundException(99L));

            mockMvc.perform(put("/fornecedores/{id}", 99L)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(requestDTO)))
                    .andExpect(status().isNotFound());

            verify(fornecedorService, times(1)).atualizar(eq(99L), any(FornecedorRequestDto.class));
        }
    }

    @Nested
    @DisplayName("atualizarStatus")
    class AtualizarStatus {

        @Test
        @DisplayName("Deve retornar status 204 ao atualizar status do fornecedor")
        void deveRetornar204AoAtualizarStatus() throws Exception {
            FornecedorStatusDto statusDTO = new FornecedorStatusDto(false);
            doNothing().when(fornecedorService).atualizarStatus(eq(1L), any(FornecedorStatusDto.class));

            mockMvc.perform(patch("/fornecedores/{id}/status", 1L)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(statusDTO)))
                    .andExpect(status().isNoContent());

            verify(fornecedorService, times(1)).atualizarStatus(eq(1L), any(FornecedorStatusDto.class));
        }

        @Test
        @DisplayName("Deve retornar status 404 quando fornecedor nao encontrado")
        void deveRetornar404QuandoNaoEncontrado() throws Exception {
            FornecedorStatusDto statusDTO = new FornecedorStatusDto(false);
            doThrow(new FornecedorNotFoundException(99L))
                    .when(fornecedorService).atualizarStatus(eq(99L), any(FornecedorStatusDto.class));

            mockMvc.perform(patch("/fornecedores/{id}/status", 99L)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(statusDTO)))
                    .andExpect(status().isNotFound());

            verify(fornecedorService, times(1)).atualizarStatus(eq(99L), any(FornecedorStatusDto.class));
        }
    }
}