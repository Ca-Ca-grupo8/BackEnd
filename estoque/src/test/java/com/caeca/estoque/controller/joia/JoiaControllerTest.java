package com.caeca.estoque.controller.joia;

import com.caeca.estoque.dto.joia.JoiaAtualizacaoDto;
import com.caeca.estoque.dto.joia.JoiaEntradaDto;
import com.caeca.estoque.dto.joia.JoiaSaidaDto;
import com.caeca.estoque.service.Material.MaterialService;
import com.caeca.estoque.service.banho.BanhoService;
import com.caeca.estoque.service.categoria.CategoriaService;
import com.caeca.estoque.service.joia.JoiaService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;

@WebMvcTest(JoiaController.class)
@AutoConfigureMockMvc(addFilters = false)
class JoiaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean private JoiaService joiaService;
    @MockitoBean private CategoriaService categoriaService;
    @MockitoBean private BanhoService banhoService;
    @MockitoBean private MaterialService materialService;

    @Nested
    @DisplayName("listarTodasAsJoias")
    class ListarTodasAsJoias {
        @Test
        @DisplayName("Deve retornar status 200 e lista de joias")
        void deveRetornarStatus200EListaDeJoias() throws Exception {
            JoiaSaidaDto joia = new JoiaSaidaDto();
            when(joiaService.listarTodasAsJoias(null, null, null, null, null, null, null)).thenReturn(List.of(joia));

            mockMvc.perform(get("/joias")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$").isArray())
                    .andExpect(jsonPath("$.length()").value(1));

            verify(joiaService, times(1)).listarTodasAsJoias(null, null, null, null, null, null, null);
        }
    }

    @Nested
    @DisplayName("listarJoiaPorId")
    class ListarJoiaPorId {
        @Test
        @DisplayName("Deve retornar status 200 e os dados da joia encontrada")
        void deveRetornarStatus200EJoiaPorId() throws Exception {
            JoiaSaidaDto joia = new JoiaSaidaDto();
            joia.setId(1L);

            when(joiaService.listarJoiaPorId(1L)).thenReturn(joia);

            mockMvc.perform(get("/joias/{id}", 1L)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk());

            verify(joiaService, times(1)).listarJoiaPorId(1L);
        }
    }

    @Nested
    @DisplayName("criarJoia")
    class CriarJoia {
        @Test
        @DisplayName("Deve retornar status 201 e dados da joia criada")
        void deveRetornarStatus201ECriarJoia() throws Exception {
            JoiaEntradaDto entradaDTO = new JoiaEntradaDto();
            JoiaSaidaDto saidaDTO = new JoiaSaidaDto();

            when(joiaService.criarJoia(any(JoiaEntradaDto.class))).thenReturn(saidaDTO);

            mockMvc.perform(post("/joias")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(entradaDTO)))
                    .andExpect(status().isCreated());

            verify(joiaService, times(1)).criarJoia(any(JoiaEntradaDto.class));
        }
    }

    @Nested
    @DisplayName("atualizarJoiaPorId")
    class AtualizarJoiaPorId {
        @Test
        @DisplayName("Deve retornar status 200 e dados atualizados da joia")
        void deveRetornarStatus200EAtualizarJoia() throws Exception {
            Long id = 1L;
            JoiaAtualizacaoDto atualizacaoDTO = new JoiaAtualizacaoDto();
            JoiaSaidaDto saidaDTO = new JoiaSaidaDto();

            when(joiaService.atualizarJoiaPorId(eq(id), any(JoiaAtualizacaoDto.class))).thenReturn(saidaDTO);

            mockMvc.perform(patch("/joias/{id}", id)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(atualizacaoDTO)))
                    .andExpect(status().isOk());

            verify(joiaService, times(1)).atualizarJoiaPorId(eq(id), any(JoiaAtualizacaoDto.class));
        }
    }
}