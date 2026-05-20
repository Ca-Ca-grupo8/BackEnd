package com.caeca.estoque.strategy.arquivo;

import com.caeca.estoque.database.model.arquivo.Arquivo;
import com.caeca.estoque.database.model.joia.Joia;
import com.caeca.estoque.database.repository.arquivo.ArquivoRepository;
import com.caeca.estoque.exception.BadRequestException;
import com.caeca.estoque.exception.NotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ArquivoS3StrategyTest {

    @Mock
    private S3Client s3Client;

    @Mock
    private ArquivoRepository arquivoRepository;

    @Mock
    private MultipartFile multipartFile;

    private ArquivoS3Strategy strategy;

    private final String BUCKET_NAME = "test-bucket";
    private final String REGION = "us-east-1";

    @BeforeEach
    void setUp() {
        strategy = new ArquivoS3Strategy(s3Client, BUCKET_NAME, arquivoRepository, REGION);
    }

    @Test
    @DisplayName("Deve adicionar um arquivo com sucesso no S3 e salvar no banco")
    void adicionarComSucesso() throws IOException {
        when(multipartFile.isEmpty()).thenReturn(false);
        when(multipartFile.getContentType()).thenReturn("image/jpeg");
        when(multipartFile.getOriginalFilename()).thenReturn("foto-teste.jpg");
        when(multipartFile.getSize()).thenReturn(1024L);
        when(multipartFile.getInputStream()).thenReturn(new ByteArrayInputStream("dados".getBytes()));

        when(s3Client.putObject(any(PutObjectRequest.class), any(RequestBody.class)))
                .thenReturn(PutObjectResponse.builder().build());
        when(arquivoRepository.save(any(Arquivo.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Arquivo resultado = strategy.adicionar(multipartFile);

        assertNotNull(resultado);
        assertNotNull(resultado.getS3Key());

        verify(s3Client, times(1)).putObject(any(PutObjectRequest.class), any(RequestBody.class));
        verify(arquivoRepository, times(1)).save(any(Arquivo.class));
    }

    @Test
    @DisplayName("Deve lançar BadRequestException quando o arquivo estiver vazio")
    void adicionarArquivoVazioLancaExcecao() {
        when(multipartFile.isEmpty()).thenReturn(true);

        assertThrows(BadRequestException.class,
                () -> strategy.adicionar(multipartFile));
    }

    @Test
    @DisplayName("Deve lançar BadRequestException quando o tipo do arquivo for inválido")
    void adicionarTipoInvalidoLancaExcecao() {
        when(multipartFile.isEmpty()).thenReturn(false);
        when(multipartFile.getContentType()).thenReturn("application/pdf");

        assertThrows(BadRequestException.class,
                () -> strategy.adicionar(multipartFile));
    }

    @Test
    @DisplayName("Deve atualizar o arquivo de uma joia existente com sucesso")
    void atualizarPorJoiaIdComSucesso() throws IOException {
        when(multipartFile.isEmpty()).thenReturn(false);
        when(multipartFile.getContentType()).thenReturn("image/jpeg");
        when(multipartFile.getOriginalFilename()).thenReturn("foto-teste.jpg");
        when(multipartFile.getSize()).thenReturn(1024L);
        when(multipartFile.getInputStream()).thenReturn(new ByteArrayInputStream("dados".getBytes()));

        Joia joia = new Joia();
        joia.setId(1L);
        Arquivo arquivoAntigo = new Arquivo();
        arquivoAntigo.setJoia(joia);
        joia.setArquivo(arquivoAntigo);

        when(arquivoRepository.findByJoiaId(1L)).thenReturn(Optional.of(arquivoAntigo));

        when(s3Client.putObject(any(PutObjectRequest.class), any(RequestBody.class)))
                .thenReturn(PutObjectResponse.builder().build());

        when(arquivoRepository.save(any(Arquivo.class))).thenAnswer(
                invocation -> invocation.getArgument(0));

        Arquivo resultado = strategy.atualizarPorJoiaId(1L, multipartFile);

        assertNotNull(resultado);
        assertEquals(joia, resultado.getJoia());
        verify(arquivoRepository, times(1)).findByJoiaId(1L);
        verify(arquivoRepository, times(1)).save(any(Arquivo.class));
    }

    @Test
    @DisplayName("Deve lançar NotFoundException ao tentar atualizar arquivo de joia inexistente")
    void atualizarPorJoiaIdJoiaNaoEncontradaLancaExcecao() {
        Long joiaId = 99L;
        when(multipartFile.isEmpty()).thenReturn(false);
        when(multipartFile.getContentType()).thenReturn("image/jpeg");
        when(arquivoRepository.findByJoiaId(joiaId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class,
                () -> strategy.atualizarPorJoiaId(joiaId, multipartFile));
        verifyNoInteractions(s3Client);
    }


    @Test
    @DisplayName("Deve deletar um arquivo pelo ID e desvincular da joia")
    void deletarComSucesso() {
        Long arquivoId = 1L;
        Joia joia = new Joia();
        Arquivo arquivo = new Arquivo();
        arquivo.setId(arquivoId);
        arquivo.setS3Key("caminho/s3/foto.jpg");
        arquivo.setJoia(joia);
        joia.setArquivo(arquivo);

        when(arquivoRepository.findById(arquivoId)).thenReturn(Optional.of(arquivo));

        strategy.deletar(arquivoId);

        assertNull(joia.getArquivo());

        verify(s3Client, times(1)).deleteObject(any(DeleteObjectRequest.class));
    }

    @Test
    @DisplayName("Deve deletar arquivo pelo ID da Joia com sucesso")
    void deletarPorJoiaIdComSucesso() {
        Long joiaId = 1L;
        Joia joia = new Joia();
        Arquivo arquivo = new Arquivo();
        arquivo.setS3Key("caminho/s3/foto2.png");
        arquivo.setJoia(joia);
        joia.setArquivo(arquivo);

        when(arquivoRepository.findByJoiaId(joiaId)).thenReturn(Optional.of(arquivo));

        strategy.deletarPorJoiaId(joiaId);

        assertNull(joia.getArquivo());
        verify(s3Client, times(1)).deleteObject(any(DeleteObjectRequest.class));
    }

    @Test
    @DisplayName("Deve lançar RuntimeException se a deleção no S3 falhar")
    void deletar_FalhaNoS3_LancaExcecao() {
        Long arquivoId = 1L;
        Arquivo arquivo = new Arquivo();
        arquivo.setS3Key("s3key");
        when(arquivoRepository.findById(arquivoId)).thenReturn(Optional.of(arquivo));

        doThrow(S3Exception.builder().message("Access Denied").build())
                .when(s3Client).deleteObject(any(DeleteObjectRequest.class));

        assertThrows(RuntimeException.class, () -> strategy.deletar(arquivoId));
    }
}