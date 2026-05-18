package com.caeca.estoque.dto.arquivo;

import com.caeca.estoque.database.model.arquivo.Arquivo;
import org.springframework.web.multipart.MultipartFile;

public class ArquivoMapper {
    public static ArquivoResponseDto toResponse(Arquivo arquivo){
        if (arquivo == null) {
            return null;
        }

        return new ArquivoResponseDto(
                arquivo.getId(),
                arquivo.getNomeOriginal(),
                arquivo.getUrl()
        );
    }

    public static Arquivo toEntity(MultipartFile arquivo){
        Arquivo entity = new Arquivo();

        entity.setNomeOriginal(arquivo.getOriginalFilename());
        entity.setMimeType(arquivo.getContentType());
        entity.setTamanho(arquivo.getSize());

        return entity;
    }
}
