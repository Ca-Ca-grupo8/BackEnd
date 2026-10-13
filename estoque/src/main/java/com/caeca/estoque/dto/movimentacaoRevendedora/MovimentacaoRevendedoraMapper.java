package com.caeca.estoque.dto.movimentacaoRevendedora;

import com.caeca.estoque.database.model.movimentacaoRevendedora.MovimentacaoRevendedora;
import com.caeca.estoque.dto.joia.JoiaMapper;
import com.caeca.estoque.dto.statusMaleta.StatusMaletaMapper;
import com.caeca.estoque.service.movimentacaoRevendedora.MaletaCalculos;
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
        dto.setMovimentacoes(MaletaCalculos.lista(entity.getMovimentacoes())
                .stream()
                .map(movimentacao -> new MovimentacaoRevendedoraResponseDto.MovimentacaoItemDto(
                        movimentacao.getIdMovimentacao(),
                        JoiaMapper.toResponse(movimentacao.getJoia()),
                        movimentacao.getQuantidade(),
                        movimentacao.getPrecoSaida(),
                        MaletaCalculos.tipoDe(movimentacao)
                ))
                .toList());

        dto.setTotalVenda(MaletaCalculos.valorTotalVendas(entity.getMovimentacoes()));
        dto.setDataLimite(entity.getDataLimite());
        dto.setData(entity.getData());
        dto.setStatus(StatusMaletaMapper.toResponse(entity.getStatus()));

        return dto;
    }

    public static List<MovimentacaoRevendedoraResponseDto> toResponseList(List<MovimentacaoRevendedora> entites){
        return entites.stream().map(MovimentacaoRevendedoraMapper::toResponse).toList();
    }

    public static MovimentacaoRevendedora toEntity(MovimentacaoRevendedoraRequestDto request) {
        MovimentacaoRevendedora entity = new MovimentacaoRevendedora();
        entity.setDataLimite(request.getDataLimite());
        return entity;
    }
}
