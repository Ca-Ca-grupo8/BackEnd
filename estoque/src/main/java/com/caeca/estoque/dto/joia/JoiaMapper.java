package com.caeca.estoque.dto.joia;

import com.caeca.estoque.database.model.joia.Joia;

import java.util.List;

public class JoiaMapper {

    public static Joia toEntity(JoiaEntradaDTO dto) {

        if (dto == null) {
            return null;
        }

        Joia joia = new Joia();

        joia.setNome(dto.getNome());
        joia.setQuantidade(dto.getQuantidade());
        joia.setPeso(dto.getPeso());
        joia.setPrecoOriginal(dto.getPrecoOriginal());
        joia.setMultiplicador(dto.getMultiplicador());
        joia.setQuantidadeMinima(dto.getQuantidadeMinima());
        joia.setPrecoBanho(dto.getPrecoBanho());

        return joia;
    }

    public static JoiaSaidaDTO toResponse(Joia joia) {

        if (joia == null) {
            return null;
        }

        JoiaSaidaDTO joiaSaidaDTO = new JoiaSaidaDTO();

        joiaSaidaDTO.setId(joia.getId());
        joiaSaidaDTO.setNome(joia.getNome());
        joiaSaidaDTO.setPreco(joia.getPreco());
        joiaSaidaDTO.setQuantidadeMinima(joia.getQuantidadeMinima());
        joiaSaidaDTO.setAtivo(joia.getAtivo());
        joiaSaidaDTO.setDataCadastro(joia.getDataCadastro());

        return joiaSaidaDTO;
    }

    public static List<JoiaSaidaDTO> toResponseList(List<Joia> joias) {
        return joias
                .stream()
                .map(JoiaMapper::toResponse)
                .toList();
    }

}
