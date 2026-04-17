package com.caeca.estoque.dto.joia;

import com.caeca.estoque.database.model.joia.Joia;

import java.util.List;

public class JoiaMapper {

    public static Joia toEntity(JoiaEntradaDTO joiaEntradaDTO) {

        if (joiaEntradaDTO == null) {
            return null;
        }

        Joia joia = new Joia();

        joia.setNome(joiaEntradaDTO.getNome());
        joia.setQuantidade(joiaEntradaDTO.getQuantidade());
        joia.setPeso(joiaEntradaDTO.getPeso());
        joia.setPrecoOriginal(joiaEntradaDTO.getPrecoOriginal());
        joia.setMultiplicador(joiaEntradaDTO.getMultiplicador());
        joia.setQuantidadeMinima(joiaEntradaDTO.getQuantidadeMinima());
        joia.setCategoria(joiaEntradaDTO.getCategoria());
        joia.setBanho(joia.getBanho());
        joia.setMaterial(joiaEntradaDTO.getMaterial());

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
