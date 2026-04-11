package com.caeca.estoque.dto;

import com.caeca.estoque.database.model.Banho;

import java.util.List;

public class BanhoMapper {

    public static BanhoResponseDto toResponse(Banho entity) {
        return new BanhoResponseDto(
                entity.getIdBanho(),
                entity.getNome()
        );
    }

    public static List<BanhoResponseDto> toResponseList(List<Banho> entities) {
        return entities.stream().map(BanhoMapper::toResponse).toList();
    }

    public static Banho toEntity(BanhoRequestDto request) {
        Banho banho = new Banho();
        banho.setNome(request.getNome());
        return banho;
    }
}
