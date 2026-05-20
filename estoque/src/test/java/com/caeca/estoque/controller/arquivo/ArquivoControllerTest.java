package com.caeca.estoque.controller.arquivo;

import com.caeca.estoque.database.model.arquivo.Arquivo;
import com.caeca.estoque.strategy.arquivo.ArquivoStrategy;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMultipartHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.multipart.MultipartFile;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ArquivoController.class)
@ExtendWith(MockitoExtension.class)
class ArquivoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ArquivoStrategy arquivoService;

    @Test
    @DisplayName("Deve deletar arquivo pelo ID da Joia e retornar 204 No Content")
    void deleteByJoiaIdComSucesso() throws Exception {
        mockMvc.perform(delete("/arquivos/joia/1/deletar"))
                .andExpect(status().isNoContent());

        verify(arquivoService, times(1)).deletarPorJoiaId(1L);
    }

    @Test
    @DisplayName("Deve deletar arquivo pelo ID e retornar 204 No Content")
    void deleteByIdComSucesso() throws Exception {
        mockMvc.perform(delete("/arquivos/1"))
                .andExpect(status().isNoContent());

        verify(arquivoService, times(1)).deletar(1L);
    }

    @Test
    @DisplayName("Deve atualizar o arquivo da joia e retornar 200 OK com o DTO")
    void atualizarComSucesso() throws Exception {
        MockMultipartFile arquivo = new MockMultipartFile(
                "arquivo", "nova-foto.jpg", "image/jpeg", "dados".getBytes()
        );

        Arquivo arquivoMock = new Arquivo();
        arquivoMock.setId(1L);
        arquivoMock.setNomeOriginal("nova-foto.jpg");
        arquivoMock.setUrl("https://s3.amazonaws.com/bucket/nova-foto.jpg");

        when(arquivoService.atualizarPorJoiaId(eq(1L), any(MultipartFile.class)))
                .thenReturn(arquivoMock);

        mockMvc.perform(multipart(HttpMethod.PUT, "/arquivos/joia/{id}", 1L)
                        .file(arquivo)
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.nomeOriginal").value("nova-foto.jpg"))
                .andExpect(jsonPath("$.url").value("https://s3.amazonaws.com/bucket/nova-foto.jpg"));

        verify(arquivoService, times(1)).atualizarPorJoiaId(eq(1L), any(MultipartFile.class));
    }
}