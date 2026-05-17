package com.caeca.estoque.dto.arquivo;

import com.caeca.estoque.database.model.joia.Joia;

import java.time.LocalDateTime;

public record ArquivoResponseDto (
    Long id,
    String nomeOriginal,
    String s3key,
    String url,
    Joia joia,
    LocalDateTime dataUpload
){}
