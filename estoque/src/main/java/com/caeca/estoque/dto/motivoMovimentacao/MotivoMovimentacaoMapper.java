package com.caeca.estoque.dto.motivoMovimentacao;

import com.caeca.estoque.database.model.motivoMovimentacao.MotivoMovimentacao;

import java.util.List;

public class MotivoMovimentacaoMapper {

    public static MotivoMovimentacaoResponseDto toResponse(MotivoMovimentacao entity) {
        return new MotivoMovimentacaoResponseDto(
                entity.getIdMotivoMovimentacao(),
                entity.getDescricao(),
                entity.getTipoMovimentacao()
        );
    }

    public static List<MotivoMovimentacaoResponseDto> toResponseList(List<MotivoMovimentacao> entities) {
        return entities.stream().map(MotivoMovimentacaoMapper::toResponse).toList();
    }

    public static MotivoMovimentacao toEntity(MotivoMovimentacaoRequestDto request) {
        MotivoMovimentacao tipo = new MotivoMovimentacao();
        tipo.setDescricao(request.getDescricao());
        return tipo;
    }
}
