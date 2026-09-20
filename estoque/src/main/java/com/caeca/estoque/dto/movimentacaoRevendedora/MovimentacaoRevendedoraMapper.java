package com.caeca.estoque.dto.movimentacaoRevendedora;

import com.caeca.estoque.database.model.movimentacaoRevendedora.MovimentacaoRevendedora;
import com.caeca.estoque.dto.joia.JoiaMapper;
import java.util.List;

public class MovimentacaoRevendedoraMapper {
    public static MovimentacaoRevendedoraResponseDto toResponse(MovimentacaoRevendedora entity){

        MovimentacaoRevendedoraResponseDto dto = new MovimentacaoRevendedoraResponseDto();
        dto.setIdMovimentacaoRevendedora(entity.getIdMovimentacaoRevendedora());
        dto.setNome(entity.getNome());
        dto.setRevendedora(new MovimentacaoRevendedoraResponseDto.RevendedoraMovimentacaoDto(
                entity.getRevendedora().getId(),
                entity.getRevendedora().getNome()
                )
        );
        dto.setMovimentacoes(entity.getMovimentacoes()
                .stream()
                .map(movimentacao -> new MovimentacaoRevendedoraResponseDto.MovimentacaoItemDto(
                        movimentacao.getIdMovimentacao(),
                        JoiaMapper.toResponse(movimentacao.getJoia()),
                        movimentacao.getQuantidade(),
                        movimentacao.getPrecoSaida()
                ))
                .toList());

        Double total = dto.getMovimentacoes()
                .stream()
                .mapToDouble(item -> {
                    Double preco = item.getPrecoSaida() != null ? item.getPrecoSaida() : 0.0;
                    Integer qtd = item.getQuantidade() != null ? item.getQuantidade() : 0;

                    return preco * qtd;
                })
                .sum();

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
