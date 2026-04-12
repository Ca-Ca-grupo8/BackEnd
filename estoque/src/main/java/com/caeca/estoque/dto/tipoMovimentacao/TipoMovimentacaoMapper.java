package com.caeca.estoque.dto.tipoMovimentacao;

import com.caeca.estoque.database.model.tipoMovimentacao.TipoMovimentacao;

import java.util.List;

public class TipoMovimentacaoMapper {

    public static TipoMovimentacaoResponseDto toResponse(TipoMovimentacao entity) {
        return new TipoMovimentacaoResponseDto(
                entity.getIdTipoMovimentacao(),
                entity.getDescricao()
        );
    }

    public static List<TipoMovimentacaoResponseDto> toResponseList(List<TipoMovimentacao> entities) {
        return entities.stream().map(TipoMovimentacaoMapper::toResponse).toList();
    }

    public static TipoMovimentacao toEntity(TipoMovimentacaoRequestDto request) {
        TipoMovimentacao tipo = new TipoMovimentacao();
        tipo.setDescricao(request.getDescricao());
        return tipo;
    }
}
