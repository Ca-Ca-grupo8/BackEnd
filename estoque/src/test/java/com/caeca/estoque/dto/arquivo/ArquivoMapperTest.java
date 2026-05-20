package com.caeca.estoque.dto.arquivo;

import com.caeca.estoque.database.model.arquivo.Arquivo;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class ArquivoMapperTest {

    @Nested
    @DisplayName("toResponse")
    class toResponse{
        @Test
        void deveriaRetornarResponseComOsValoresCorretos() {
            Arquivo arquivo =  new Arquivo();
            arquivo.setId(1L);
            arquivo.setNomeOriginal("imagemTeste");
            arquivo.setUrl("http://...");

            ArquivoResponseDto arquivoResponseDto = ArquivoMapper.toResponse(arquivo);

            assertEquals(arquivo.getId(), arquivoResponseDto.id());
            assertEquals(arquivo.getNomeOriginal(), arquivoResponseDto.nomeOriginal());
            assertEquals(arquivo.getUrl(), arquivoResponseDto.url());
        }

        @Test
        void deveriaRetornarNullQuandoArquivoForNull() {
            Arquivo arquivo = null;

            ArquivoResponseDto arquivoResponseDto = ArquivoMapper.toResponse(arquivo);

            assertNull(arquivoResponseDto);
        }
    }

    @Nested
    @DisplayName("toEntity")
    class toEntity{
        @Test
        @DisplayName("Deve mapear um MultipartFile para a entidade Arquivo corretamente")
        void deveMapearMultipartFileParaArquivoComSucesso() throws IOException {
            MockMultipartFile mockFile = new MockMultipartFile(
                    "arquivo",
                    "relatorio_financeiro.pdf",
                    "application/pdf",
                    "conteúdo simulado do arquivo".getBytes()
            );

            Arquivo entity = ArquivoMapper.toEntity(mockFile);

            assertNotNull(entity);
            assertEquals(mockFile.getOriginalFilename(), entity.getNomeOriginal());
            assertEquals(mockFile.getContentType(), entity.getMimeType());
            assertEquals(mockFile.getSize(), entity.getTamanho());
        }
    }
}