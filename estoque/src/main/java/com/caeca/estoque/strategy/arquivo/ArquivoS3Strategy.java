package com.caeca.estoque.strategy.arquivo;

import com.caeca.estoque.database.model.arquivo.Arquivo;
import com.caeca.estoque.database.repository.arquivo.ArquivoRepository;
import com.caeca.estoque.dto.arquivo.ArquivoMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;

import java.io.IOException;
import java.util.UUID;

@Service
@ConditionalOnProperty(name = "app.storage.type", havingValue = "s3")
public class ArquivoS3Strategy implements ArquivoStrategy{

    private final S3Client s3Client;
    private final String bucket;
    private final ArquivoRepository arquivoRepository;
    private final String regiao;

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
        System.out.println("AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");
        Arquivo arquivo = ArquivoMapper.toEntity(upload);

        String s3key = "arquivos/" + UUID.randomUUID() + "-" + arquivo.getNomeOriginal();

        arquivo.setS3Key(s3key);

        PutObjectRequest request = PutObjectRequest.builder()
                .bucket(bucket)
                .key(s3key)
                .contentType(arquivo.getMimeType())
                .contentLength(arquivo.getTamanho())
                .build();

        try {
            System.out.println("AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");
            s3Client.putObject(request, RequestBody.fromInputStream(upload.getInputStream(), arquivo.getTamanho()));
        } catch (IOException | S3Exception e) {
            throw new RuntimeException(e);
        }

        String url = "https://" + bucket + ".s3." + regiao + ".amazonaws.com/" + s3key;
        arquivo.setUrl(url);

        return arquivoRepository.save(arquivo);
    }

    @Override
    public Arquivo listar() {
        return null;
    }

    @Override
    public Arquivo atualizarPorId() {
        return null;
    }

    @Override
    public void deletar() {

    }
}
