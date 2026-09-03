package com.caeca.estoque.dto.movimentacao;

import com.caeca.estoque.database.model.movimentacao.Movimentacao;
import com.caeca.estoque.dto.fornecedor.FornecedorMapper;
import com.caeca.estoque.dto.joia.JoiaMapper;
import com.caeca.estoque.dto.motivoMovimentacao.MotivoMovimentacaoMapper;
import com.caeca.estoque.dto.movimentacaoRevendedora.MovimentacaoRevendedoraMapper;
import com.caeca.estoque.dto.usuario.UsuarioMapper;

import java.util.List;

public class MovimentacaoMapper {
    public static MovimentacaoResponseDto toResponse(Movimentacao entity){
        MovimentacaoResponseDto dto = new MovimentacaoResponseDto();
        dto.setId(entity.getIdMovimentacao());
        dto.setQuantidade(entity.getQuantidade());
        dto.setPrecoSaida(entity.getPrecoSaida());
        dto.setJoia(JoiaMapper.toResponse(entity.getJoia()));
        dto.setMotivoMovimentacao(MotivoMovimentacaoMapper.toResponse(entity.getMotivoMovimentacao()));
        dto.setUsuario(UsuarioMapper.of(entity.getUsuario()));

        dto.setFornecedor(entity.getFornecedor() != null
                ? FornecedorMapper.toResponseDTO(entity.getFornecedor())
                : null);

        dto.setMovimentacaoRevendedora(entity.getMovimentacaoRevendedora() != null
                ? MovimentacaoRevendedoraMapper.toResponse(entity.getMovimentacaoRevendedora())
                : null);

        dto.setData(entity.getData());

        return dto;
    }

    public static List<MovimentacaoResponseDto> toResponseList(List<Movimentacao> listar) {
        return listar.stream().map(MovimentacaoMapper::toResponse).toList();
    }

    public static Movimentacao toEntity(MovimentacaoRequestDto dto) {
        Movimentacao movimentacao = new Movimentacao();
        movimentacao.setQuantidade(dto.getQuantidade());

        return movimentacao;
    }
}
