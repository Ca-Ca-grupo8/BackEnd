package com.caeca.estoque.dto.statusMaleta;

import com.caeca.estoque.database.model.statusMaleta.StatusMaleta;

import java.util.List;

public class StatusMaletaMapper {

    public static StatusMaletaResponseDto toResponse(StatusMaleta entity) {

        if (entity == null) {
            return null;
        }

        StatusMaletaResponseDto response = new StatusMaletaResponseDto();
        response.setId(entity.getId());
        response.setNome(entity.getNome());
        return response;
    }

    public static List<StatusMaletaResponseDto> toResponseList(List<StatusMaleta> entities) {
        return entities.stream().map(StatusMaletaMapper::toResponse).toList();
    }
}
