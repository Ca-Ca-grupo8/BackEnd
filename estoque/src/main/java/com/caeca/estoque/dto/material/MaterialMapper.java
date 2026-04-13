package com.caeca.estoque.dto.material;

import com.caeca.estoque.database.model.material.Material;

import java.util.List;

public class MaterialMapper {

    public static MaterialResponseDto toResponse(Material entity) {
        return new MaterialResponseDto(
                entity.getIdMaterial(),
                entity.getNome()
        );
    }

    public static List<MaterialResponseDto> toResponseList(List<Material> entities) {
        return entities.stream().map(MaterialMapper::toResponse).toList();
    }

    public static Material toEntity(MaterialRequestDto request){
        Material material = new Material();
        material.setNome(request.getNome());
        return material;
    }

}
