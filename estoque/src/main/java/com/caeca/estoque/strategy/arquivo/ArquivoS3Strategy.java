package com.caeca.estoque.strategy.arquivo;

import com.caeca.estoque.database.model.arquivo.Arquivo;
import com.caeca.estoque.database.model.joia.Joia;
import com.caeca.estoque.database.repository.arquivo.ArquivoRepository;
import com.caeca.estoque.database.repository.joia.JoiaRepository;
import com.caeca.estoque.dto.arquivo.ArquivoMapper;
import com.caeca.estoque.exception.BadRequestException;
import com.caeca.estoque.exception.NotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@Service
@ConditionalOnProperty(name = "app.storage.type", havingValue = "s3")
public class ArquivoS3Strategy implements ArquivoStrategy{

    private final S3Client s3Client;
    private final String bucket;
    private final ArquivoRepository arquivoRepository;
    private final String regiao;
    private static final List<String> TIPOS_PERMITIDOS = List.of("image/jpeg", "image/png", "image/webp");

    public ArquivoS3Strategy(S3Client s3Client, @Value("${app.storage.s3.bucket}") String bucket,
                             ArquivoRepository arquivoRepository,
                             @Value("${app.storage.s3.region}") String regiao) {
        this.s3Client = s3Client;
        this.bucket = bucket;
        this.arquivoRepository = arquivoRepository;
        this.regiao = regiao;
    }

    @Override
    public Arquivo adicionar(MultipartFile upload) {
        validarTipoArquivoValido(upload);
        Arquivo arquivo = ArquivoMapper.toEntity(upload);
        return fazerUploadESalvar(arquivo, upload);
    }

    @Transactional
    @Override
    public Arquivo atualizarPorJoiaId(Long joiaId, MultipartFile novoArquivo) {
        validarTipoArquivoValido(novoArquivo);

        Arquivo arquivo = arquivoRepository.findByJoiaId(joiaId)
                .orElseThrow(() -> new NotFoundException("Não existe um arquivo para a Joia especificada"));

        Joia joia = arquivo.getJoia();

        joia.setArquivo(null);

        Arquivo novoArquivoEntity = ArquivoMapper.toEntity(novoArquivo);
        novoArquivoEntity.setJoia(joia);

        return fazerUploadESalvar(novoArquivoEntity, novoArquivo);
    }

    @Transactional
    @Override
    public void deletar(Long id) {
        Arquivo arquivo = arquivoRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Não existe um arquivo com id especificado"));

        deletarArquivo(arquivo.getS3Key());

        Joia joia = arquivo.getJoia();

        joia.setArquivo(null);
    }

    @Transactional
    @Override
    public void deletarPorJoiaId(Long joiaId) {
        Arquivo arquivo = arquivoRepository.findByJoiaId(joiaId)
                .orElseThrow(() -> new NotFoundException("Não existe um arquivo com id da jóia"));

        deletarArquivo(arquivo.getS3Key());

        Joia joia = arquivo.getJoia();

        joia.setArquivo(null);

    }

    private Arquivo fazerUploadESalvar(Arquivo arquivo, MultipartFile upload) {
        String s3key = "arquivos/" + UUID.randomUUID() + "-" + arquivo.getNomeOriginal();
        arquivo.setS3Key(s3key);

        PutObjectRequest request = PutObjectRequest.builder()
                .bucket(bucket)
                .key(s3key)
                .contentType(arquivo.getMimeType())
                .contentLength(arquivo.getTamanho())
                .build();

        try {
            s3Client.putObject(request, RequestBody.fromInputStream(upload.getInputStream(), arquivo.getTamanho()));
        } catch (IOException | S3Exception e) {
            throw new RuntimeException("Falha ao realizar upload para o S3", e);
        }

        String url = "https://" + bucket + ".s3." + regiao + ".amazonaws.com/" + s3key;
        arquivo.setUrl(url);

        return arquivoRepository.save(arquivo);
    }

    private void deletarArquivo(String s3key){
        DeleteObjectRequest request = DeleteObjectRequest.builder()
                .bucket(bucket)
                .key(s3key)
                .build();

        try {
            s3Client.deleteObject(request);
        } catch ( S3Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void validarTipoArquivoValido(MultipartFile arquivo){
        if (arquivo.isEmpty()) {
            throw new BadRequestException("O arquivo enviado está vazio.");
        }

        if (!TIPOS_PERMITIDOS.contains(arquivo.getContentType())) {
            throw new BadRequestException("Formato não suportado. Envie JPEG, PNG ou WebP.");
        }
    }
}
