package com.caeca.estoque.dto.revendedora;

import com.caeca.estoque.database.model.revendedora.Revendedora;

import java.util.List;

public class RevendedoraMapper {
    public static RevendedoraResponse toResponse(Revendedora entity){
        return new RevendedoraResponse(
                entity.getId(),
                entity.getNome(),
                entity.getEmail(),
                entity.getTelefone(),
                entity.isAtivo(),
                entity.getEndereco(),
                entity.getDataCadastro(),
                entity.getDataAtualizacao()
        );
    }

    public static List<RevendedoraResponse> toResponseList(List<Revendedora> entities){
        return entities.stream().map(RevendedoraMapper::toResponse).toList();
    }
}
