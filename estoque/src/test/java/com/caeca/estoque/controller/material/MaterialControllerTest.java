package com.caeca.estoque.controller.material;

import com.caeca.estoque.database.model.material.Material;
import com.caeca.estoque.dto.material.MaterialRequestDto;
import com.caeca.estoque.exception.NotFoundException;
import com.caeca.estoque.handler.GlobalExceptionHandler;
import com.caeca.estoque.service.Material.MaterialService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest(MaterialController.class)
@org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc(addFilters = false)
@Import(GlobalExceptionHandler.class)
class MaterialControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private MaterialService materialService;

    private ObjectMapper objectMapper;
    private Material material;
    private MaterialRequestDto requestDto;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();

        // Como não vi o código da sua Entidade Material, presumi que ela tenha idMaterial e nome com base nos DTOs e Service.
        // Se os setters forem diferentes, basta ajustar aqui.
        material = new Material();
        material.setIdMaterial(1);
        material.setNome("Ouro 18K");

        requestDto = new MaterialRequestDto();
        requestDto.setNome("Ouro 18K");
    }

    @Nested
    @DisplayName("listar")
    class Listar {

        @Test
        @DisplayName("Deve retornar status 200 e lista de materiais quando houver registros")
        void deveRetornar200EListaDeMateriais() throws Exception {
            when(materialService.listar()).thenReturn(List.of(material));

            mockMvc.perform(get("/materiais")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$").isArray())
                    .andExpect(jsonPath("$.length()").value(1))
                    .andExpect(jsonPath("$[0].idMaterial").value(1))
                    .andExpect(jsonPath("$[0].nome").value("Ouro 18K"));

            verify(materialService, times(1)).listar();
        }

        @Test
        @DisplayName("Deve retornar status 204 No Content quando a lista estiver vazia")
        void deveRetornar204QuandoListaVazia() throws Exception {
            when(materialService.listar()).thenReturn(List.of());

            mockMvc.perform(get("/materiais")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isNoContent())
                    .andExpect(jsonPath("$").doesNotExist());

            verify(materialService, times(1)).listar();
        }
    }

    @Nested
    @DisplayName("exibirPorId")
    class ExibirPorId {

        @Test
        @DisplayName("Deve retornar status 200 e dados do material encontrado")
        void deveRetornar200EMaterialPorId() throws Exception {
            when(materialService.listarPorId(1)).thenReturn(material);

            mockMvc.perform(get("/materiais/{id}", 1)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.idMaterial").value(1))
                    .andExpect(jsonPath("$.nome").value("Ouro 18K"));

            verify(materialService, times(1)).listarPorId(1);
        }

        @Test
        @DisplayName("Deve retornar status 404 quando material nao for encontrado")
        void deveRetornar404QuandoNaoEncontrado() throws Exception {
            when(materialService.listarPorId(99))
                    .thenThrow(new NotFoundException("Material não encontrado com id: 99"));

            mockMvc.perform(get("/materiais/{id}", 99)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isNotFound());

            verify(materialService, times(1)).listarPorId(99);
        }
    }

    @Nested
    @DisplayName("cadastrar")
    class Cadastrar {

        @Test
        @DisplayName("Deve retornar status 201 e dados do material cadastrado")
        void deveRetornar201EMaterialCadastrado() throws Exception {
            when(materialService.cadastrar(any(MaterialRequestDto.class))).thenReturn(material);

            mockMvc.perform(post("/materiais")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(requestDto)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.idMaterial").value(1))
                    .andExpect(jsonPath("$.nome").value("Ouro 18K"));

            verify(materialService, times(1)).cadastrar(any(MaterialRequestDto.class));
        }

        @Test
        @DisplayName("Deve retornar status 409 quando já tiver o material com o mesmo nome cadastrado")
        void deveRetornar409QuandoNomeJaExistir() throws Exception {
            when(materialService.cadastrar(any(MaterialRequestDto.class)))
                    .thenThrow(new ResponseStatusException(HttpStatus.CONFLICT, "Já existe um material com o nome: Ouro 18K"));

            mockMvc.perform(post("/materiais")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(requestDto)))
                    .andExpect(status().isConflict());

            verify(materialService, times(1)).cadastrar(any(MaterialRequestDto.class));
        }

        @Test
        @DisplayName("Deve retornar status 400 Bad Request quando a requisição for inválida (validação do @Valid)")
        void deveRetornar400QuandoRequestInvalido() throws Exception {
            MaterialRequestDto invalidRequest = new MaterialRequestDto();
            invalidRequest.setNome("");

            mockMvc.perform(post("/materiais")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(invalidRequest)))
                    .andExpect(status().isBadRequest());

            // Verifica que a service não foi chamada
            verify(materialService, never()).cadastrar(any(MaterialRequestDto.class));
        }
    }
}