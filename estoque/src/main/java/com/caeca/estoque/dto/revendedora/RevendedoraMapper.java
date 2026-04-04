package com.caeca.estoque.dto.revendedora;

import com.caeca.estoque.database.model.revendedora.Revendedora;

import java.time.LocalDate;
import java.util.List;

public class RevendedoraMapper {
    public static RevendedoraResponseDto toResponse(Revendedora entity){
        return new RevendedoraResponseDto(
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

    public static List<RevendedoraResponseDto> toResponseList(List<Revendedora> entities){
        return entities.stream().map(RevendedoraMapper::toResponse).toList();
    }

    public static Revendedora toEntity(RevendedoraRequestDto request, Endereco endereco){

        Revendedora revendedora = new Revendedora();
        revendedora.setNome(request.getNome());
        revendedora.setEmail(request.getEmail());
        revendedora.setAtivo(true);
        revendedora.setTelefone(request.getTelefone());
        revendedora.setEndereco(endereco);

        return revendedora;
    }
}
