package com.caeca.estoque.controller.endereco;

import com.caeca.estoque.database.model.endereco.Endereco;
import com.caeca.estoque.dto.endereco.EnderecoMapper;
import com.caeca.estoque.dto.endereco.EnderecoRequestDto;
import com.caeca.estoque.dto.endereco.EnderecoResponseDto;
import com.caeca.estoque.exception.EnderecoNotFoundException;
import com.caeca.estoque.handler.GlobalExceptionHandler;
import com.caeca.estoque.service.endereco.EnderecoService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest(EnderecoController.class)
@org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc(addFilters = false)
@Import(GlobalExceptionHandler.class)
class EnderecoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private EnderecoService enderecoService;

    private ObjectMapper objectMapper;
    private Endereco endereco;
    private EnderecoRequestDto requestDTO;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();

        endereco = new Endereco();
        endereco.setId(1L);
        endereco.setCep("01310100");
        endereco.setLogradouro("Avenida Paulista");
        endereco.setNumero(1000);
        endereco.setComplemento("Apto 42");
        endereco.setBairro("Bela Vista");
        endereco.setCidade("São Paulo");
        endereco.setUf("SP");

        requestDTO = new EnderecoRequestDto();
        requestDTO.setCep("01310100");
        requestDTO.setLogradouro("Avenida Paulista");
        requestDTO.setNumero(1000);
        requestDTO.setComplemento("Apto 42");
        requestDTO.setBairro("Bela Vista");
        requestDTO.setCidade("São Paulo");
        requestDTO.setUf("SP");
    }

    @Nested
    @DisplayName("listar")
    class Listar {

        @Test
        @DisplayName("Deve retornar status 200 e página de enderecos")
        void deveRetornar200EPaginaDeEnderecos() throws Exception {
            Page<EnderecoResponseDto> page = new PageImpl<>(List.of(EnderecoMapper.toResponse(endereco)));
            when(enderecoService.listarTodas(any(Pageable.class))).thenReturn(page);

            mockMvc.perform(get("/enderecos")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.content").isArray())
                    .andExpect(jsonPath("$.content.length()").value(1));

            verify(enderecoService, times(1)).listarTodas(any(Pageable.class));
        }

        @Test
        @DisplayName("Deve retornar status 200 com página vazia quando não houver registros")
        void deveRetornar200ComPaginaVazia() throws Exception {
            when(enderecoService.listarTodas(any(Pageable.class))).thenReturn(new PageImpl<>(List.of()));

            mockMvc.perform(get("/enderecos")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.content").isArray())
                    .andExpect(jsonPath("$.content.length()").value(0));

            verify(enderecoService, times(1)).listarTodas(any(Pageable.class));
        }
    }

    @Nested
    @DisplayName("buscarPorId")
    class BuscarPorId {

        @Test
        @DisplayName("Deve retornar status 200 e dados do endereco encontrado")
        void deveRetornar200EEnderecoPorId() throws Exception {
            when(enderecoService.buscarPorId(1L)).thenReturn(endereco);

            mockMvc.perform(get("/enderecos/{id}", 1L)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(1))
                    .andExpect(jsonPath("$.cep").value("01310100"))
                    .andExpect(jsonPath("$.logradouro").value("Avenida Paulista"));

            verify(enderecoService, times(1)).buscarPorId(1L);
        }

        @Test
        @DisplayName("Deve retornar status 404 quando endereco nao encontrado")
        void deveRetornar404QuandoNaoEncontrado() throws Exception {
            when(enderecoService.buscarPorId(99L))
                    .thenThrow(new EnderecoNotFoundException(99L));

            mockMvc.perform(get("/enderecos/{id}", 99L)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isNotFound());

            verify(enderecoService, times(1)).buscarPorId(99L);
        }
    }

    @Nested
    @DisplayName("criar")
    class Criar {

        @Test
        @DisplayName("Deve retornar status 201 e dados do endereco criado")
        void deveRetornar201EEnderecoCriado() throws Exception {
            when(enderecoService.criar(any(EnderecoRequestDto.class))).thenReturn(endereco);

            mockMvc.perform(post("/enderecos")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(requestDTO)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.cep").value("01310100"))
                    .andExpect(jsonPath("$.logradouro").value("Avenida Paulista"));

            verify(enderecoService, times(1)).criar(any(EnderecoRequestDto.class));
        }
    }

    @Nested
    @DisplayName("atualizar")
    class Atualizar {

        @Test
        @DisplayName("Deve retornar status 200 e dados atualizados do endereco")
        void deveRetornar200EEnderecoAtualizado() throws Exception {
            when(enderecoService.atualizar(eq(1L), any(EnderecoRequestDto.class)))
                    .thenReturn(endereco);

            mockMvc.perform(put("/enderecos/{id}", 1L)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(requestDTO)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.logradouro").value("Avenida Paulista"));

            verify(enderecoService, times(1)).atualizar(eq(1L), any(EnderecoRequestDto.class));
        }

        @Test
        @DisplayName("Deve retornar status 404 quando endereco nao encontrado")
        void deveRetornar404QuandoNaoEncontrado() throws Exception {
            when(enderecoService.atualizar(eq(99L), any(EnderecoRequestDto.class)))
                    .thenThrow(new EnderecoNotFoundException(99L));

            mockMvc.perform(put("/enderecos/{id}", 99L)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(requestDTO)))
                    .andExpect(status().isNotFound());

            verify(enderecoService, times(1)).atualizar(eq(99L), any(EnderecoRequestDto.class));
        }
    }

    @Nested
    @DisplayName("deletar")
    class Deletar {

        @Test
        @DisplayName("Deve retornar status 204 ao deletar endereco")
        void deveRetornar204AoDeletarEndereco() throws Exception {
            doNothing().when(enderecoService).deletar(1L);

            mockMvc.perform(delete("/enderecos/{id}", 1L))
                    .andExpect(status().isNoContent());

            verify(enderecoService, times(1)).deletar(1L);
        }

        @Test
        @DisplayName("Deve retornar status 404 quando endereco nao encontrado")
        void deveRetornar404QuandoNaoEncontrado() throws Exception {
            doThrow(new EnderecoNotFoundException(99L))
                    .when(enderecoService).deletar(99L);

            mockMvc.perform(delete("/enderecos/{id}", 99L))
                    .andExpect(status().isNotFound());

            verify(enderecoService, times(1)).deletar(99L);
        }
    }
}