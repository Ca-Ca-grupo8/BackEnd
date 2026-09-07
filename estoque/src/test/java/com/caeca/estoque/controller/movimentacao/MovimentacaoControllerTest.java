package com.caeca.estoque.controller.movimentacao;

import com.caeca.estoque.database.model.arquivo.Arquivo;
import com.caeca.estoque.database.model.joia.Joia;
import com.caeca.estoque.database.model.movimentacao.Movimentacao;
import com.caeca.estoque.database.model.movimentacaoRevendedora.MovimentacaoRevendedora;
import com.caeca.estoque.database.model.revendedora.Revendedora;
import com.caeca.estoque.database.model.motivoMovimentacao.MotivoMovimentacao;
import com.caeca.estoque.database.model.usuario.Usuario;
import com.caeca.estoque.dto.movimentacao.MovimentacaoRequestDto;
import com.caeca.estoque.dto.movimentacao.MovimentacaoResponseDto;
import com.caeca.estoque.dto.movimentacao.MovimentacaoMapper;
import com.caeca.estoque.service.movimentacao.MovimentacaoService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(MovimentacaoController.class)
@ExtendWith(MockitoExtension.class)
class MovimentacaoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    MovimentacaoService movimentacaoService;

    @Test
    void listar() throws Exception {
        Joia joiaMock = mock(Joia.class);
        MotivoMovimentacao tipoMock = mock(MotivoMovimentacao.class);
        Usuario usuarioMock = mock(Usuario.class);
        Revendedora revendedoraMock = mock(Revendedora.class);
        MovimentacaoRevendedora movRevendedoraMock = mock(MovimentacaoRevendedora.class);
        when(movRevendedoraMock.getRevendedora()).thenReturn(revendedoraMock);

        Movimentacao movimentacao = new Movimentacao(
                1L, LocalDateTime.now(), 10, 150.0, 50.0, 3.0, 15.0,
                joiaMock, tipoMock, usuarioMock, null, movRevendedoraMock
        );

        MovimentacaoResponseDto response = MovimentacaoMapper.toResponse(movimentacao);

        when(movimentacaoService.listar(eq(null), eq(null), eq(null), eq(null), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(response)));

        mockMvc.perform(get("/movimentacoes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(1));
    }

    @Test
    void buscarPorId() throws Exception {
        Joia joiaMock = mock(Joia.class);
        MotivoMovimentacao tipoMock = mock(MotivoMovimentacao.class);
        Usuario usuarioMock = mock(Usuario.class);
        Revendedora revendedoraMock = mock(Revendedora.class);

        Movimentacao movimentacao = new Movimentacao(
                1L, LocalDateTime.now(), 10, 150.0, 50.0, 3.0, 15.0,
                joiaMock, tipoMock, usuarioMock, null, null
        );

        when(movimentacaoService.buscarPorId(1L)).thenReturn(movimentacao);

        mockMvc.perform(get("/movimentacoes/{id}", 1L))
                .andExpect(status().isOk());

        verify(movimentacaoService, times(1)).buscarPorId(1L);
    }

    @Test
    void cadastrar() throws Exception {
        MockMultipartFile arquivo = new MockMultipartFile(
                "arquivo", "nova-foto.jpg", "image/jpeg", "dados".getBytes()
        );

        String dtoJson = """
        {
            "quantidade": 2,
            "idJoia": null,
            "dadosNovaJoia": {
                "nome": "flobers",
                "quantidade": 0,
                "quantidadeMinima": 2,
                "categoria": 1,
                "banho": 1,
                "material": 1
            },
            "idMotivoMovimentacao": 1,
            "idFornecedor": 1,
            "idRevendedora": 0,
            "precoCompraRevendedor": 0.1,
            "precoBanho": 0.1,
            "multiplicador": 0.1
        }
        """;

        MockMultipartFile requestPart = new MockMultipartFile(
                "movimentacaoRequest",
                "",
                MediaType.APPLICATION_JSON_VALUE,
                dtoJson.getBytes()
        );

        Joia joiaMock = mock(Joia.class);
        MotivoMovimentacao tipoMock = mock(MotivoMovimentacao.class);
        Usuario usuarioMock = mock(Usuario.class);
        MovimentacaoRevendedora movRevendedoraMock = mock(MovimentacaoRevendedora.class);
        when(movRevendedoraMock.getRevendedora()).thenReturn(mock(Revendedora.class));

        Arquivo arquivoMock = new Arquivo();
        arquivoMock.setId(1L);
        arquivoMock.setNomeOriginal("nova-foto.jpg");
        arquivoMock.setUrl("https://s3.amazonaws.com/bucket/nova-foto.jpg");

        Movimentacao movimentacaoSalva = new Movimentacao(
                1L, LocalDateTime.now(), 10, 150.0, 50.0, 3.0, 15.0,
                joiaMock, tipoMock, usuarioMock, null, movRevendedoraMock
        );

        when(movimentacaoService.cadastrar(any(MovimentacaoRequestDto.class), any(MultipartFile.class)))
                .thenReturn(movimentacaoSalva);

        mockMvc.perform(multipart(HttpMethod.POST, "/movimentacoes")
                        .file(requestPart)
                        .file(arquivo)
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.quantidade").value(10));

        verify(movimentacaoService, times(1)).cadastrar(any(MovimentacaoRequestDto.class), any(MultipartFile.class));
    }
}