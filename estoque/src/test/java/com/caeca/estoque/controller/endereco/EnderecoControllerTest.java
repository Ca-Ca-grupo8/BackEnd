package com.caeca.estoque.controller.endereco;

import com.caeca.estoque.database.model.endereco.Endereco;
import com.caeca.estoque.dto.endereco.EnderecoRequestDTO;
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
    private EnderecoRequestDTO requestDTO;

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
    @DisplayName("listar")
    class Listar {

        @Test
        @DisplayName("Deve retornar status 200 e lista de enderecos")
        void deveRetornar200EListaDeEnderecos() throws Exception {
            when(enderecoService.listarTodos()).thenReturn(List.of(endereco));

            mockMvc.perform(get("/enderecos")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$").isArray())
                    .andExpect(jsonPath("$.length()").value(1));

            verify(enderecoService, times(1)).listarTodos();
        }

        @Test
        @DisplayName("Deve retornar status 200 e lista vazia")
        void deveRetornar200EListaVazia() throws Exception {
            when(enderecoService.listarTodos()).thenReturn(List.of());

            mockMvc.perform(get("/enderecos")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.length()").value(0));

            verify(enderecoService, times(1)).listarTodos();
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
            when(enderecoService.criar(any(EnderecoRequestDTO.class))).thenReturn(endereco);

            mockMvc.perform(post("/enderecos")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(requestDTO)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.cep").value("01310100"))
                    .andExpect(jsonPath("$.logradouro").value("Avenida Paulista"));

            verify(enderecoService, times(1)).criar(any(EnderecoRequestDTO.class));
        }
    }

    @Nested
    @DisplayName("atualizar")
    class Atualizar {

        @Test
        @DisplayName("Deve retornar status 200 e dados atualizados do endereco")
        void deveRetornar200EEnderecoAtualizado() throws Exception {
            when(enderecoService.atualizar(eq(1L), any(EnderecoRequestDTO.class)))
                    .thenReturn(endereco);

            mockMvc.perform(put("/enderecos/{id}", 1L)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(requestDTO)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.logradouro").value("Avenida Paulista"));

            verify(enderecoService, times(1)).atualizar(eq(1L), any(EnderecoRequestDTO.class));
        }

        @Test
        @DisplayName("Deve retornar status 404 quando endereco nao encontrado")
        void deveRetornar404QuandoNaoEncontrado() throws Exception {
            when(enderecoService.atualizar(eq(99L), any(EnderecoRequestDTO.class)))
                    .thenThrow(new EnderecoNotFoundException(99L));

            mockMvc.perform(put("/enderecos/{id}", 99L)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(requestDTO)))
                    .andExpect(status().isNotFound());

            verify(enderecoService, times(1)).atualizar(eq(99L), any(EnderecoRequestDTO.class));
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