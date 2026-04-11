package com.caeca.estoque.dto.movimentacaoRevendedora;

import com.caeca.estoque.database.model.movimentacaoRevendedora.MovimentacaoRevendedora;

import java.math.BigDecimal;
import java.util.List;

public class MovimentacaoRevendedoraMapper {
    public static MovimentacaoRevendedoraResponseDto toResponse(MovimentacaoRevendedora entity){

        MovimentacaoRevendedoraResponseDto dto = new MovimentacaoRevendedoraResponseDto();
        dto.setIdMovimentacaoRevendedora(entity.getIdMovimentacaoRevendedora());
        dto.setRevendedora(new MovimentacaoRevendedoraResponseDto.RevendedoraMovimentacaoDto(
                entity.getRevendedora().getId(),
                entity.getRevendedora().getNome()
                )
        );
        dto.setMovimentacoes(entity.getMovimentacoes()
                .forEach(movimentacao -> {
                    new MovimentacaoRevendedoraResponseDto.MovimentacaoItemDto(
                            movimentacao.getIdMovimentacao(),
                            JoiaMapper.toResumo(movimentacao.getJoia()),
                            movimentacao.getQuantidade(),
                            movimentacao.getPrecoSaida()
                    );
                }));

        BigDecimal total = dto.getMovimentacoes()
                .stream()
                .map(MovimentacaoRevendedoraResponseDto.MovimentacaoItemDto::getPrecoSaida)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        dto.setTotalVenda(total);
        dto.setData(entity.getData());
        dto.setFinalizada(entity.getIsFinalizada());

        return dto;
    }

    public static List<MovimentacaoRevendedoraResponseDto> toResponseList(List<MovimentacaoRevendedora> entites){
        return entites.stream().map(MovimentacaoRevendedoraMapper::toResponse).toList();
    }

    public static MovimentacaoRevendedora toEntity(MovimentacaoRevendedoraRequestDto request) {
        return new MovimentacaoRevendedora();
    }
}
