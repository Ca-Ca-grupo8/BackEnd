package com.caeca.estoque.dto.movimentacao;

import com.caeca.estoque.database.model.movimentacao.Movimentacao;
import com.caeca.estoque.dto.fornecedor.FornecedorMapper;
import com.caeca.estoque.dto.joia.JoiaMapper;
import com.caeca.estoque.dto.tipoMovimentacao.TipoMovimentacaoMapper;
import com.caeca.estoque.dto.movimentacaoRevendedora.MovimentacaoRevendedoraMapper;
import com.caeca.estoque.dto.usuario.UsuarioMapper;
import org.springframework.http.ResponseEntity;

import java.util.List;

public class MovimentacaoMapper {
    public static MovimentacaoResponseDto toResponse(Movimentacao entity){
        MovimentacaoResponseDto dto = new MovimentacaoResponseDto();
        dto.setId(entity.getIdMovimentacao());
        dto.setQuantidade(entity.getQuantidade());
        dto.setPrecoSaida(entity.getPrecoSaida());
        dto.setJoia(JoiaMapper.toResponse(entity.getJoia()));
        dto.setTipoMovimentacao(TipoMovimentacaoMapper.toResponse(entity.getTipoMovimentacao()));
        dto.setUsuario(UsuarioMapper.of(entity.getUsuario()));
        dto.setFornecedor(FornecedorMapper.toResponseDTO(entity.getFornecedor()));
        dto.setMovimentacaoRevendedora(MovimentacaoRevendedoraMapper.toResponse(entity.getMovimentacaoRevendedora()));
    }

    public static List<MovimentacaoResponseDto> toReponseList(List<Movimentacao> listar) {
        return listar.stream().map(MovimentacaoMapper::toResponse).toList();
    }

    public static Movimentacao toEntity(MovimentacaoRequestDto dto) {
        Movimentacao movimentacao = new Movimentacao();
        movimentacao.setQuantidade(dto.getQuantidade());

        return movimentacao;
    }
}
