package com.caeca.estoque.dto.tipoMovimentacao;

import com.caeca.estoque.database.model.tipoMovimentacao.TipoMovimentacao;

import java.util.List;

public class TipoMovimentacaoMapper {

    public static TipoMovimentacaoResponseDto toResponse(TipoMovimentacao entity) {
        TipoMovimentacaoResponseDto response = new TipoMovimentacaoResponseDto();
        response.setId(entity.getId());
        response.setNome(entity.getNome());
        return response;
    }

    public static List<TipoMovimentacaoResponseDto> toResponseList(List<TipoMovimentacao> entities) {
        return entities.stream().map(TipoMovimentacaoMapper::toResponse).toList();
    }

    public static TipoMovimentacao toEntity(TipoMovimentacaoRequestDto request) {
        TipoMovimentacao tipoMovimentacao = new TipoMovimentacao();
        tipoMovimentacao.setNome(request.getNome());

        return tipoMovimentacao;
    }
}