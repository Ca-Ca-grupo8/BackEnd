package com.caeca.estoque.dto.joia;

import com.caeca.estoque.database.model.joia.Joia;
import com.caeca.estoque.dto.arquivo.ArquivoMapper;
import com.caeca.estoque.dto.banho.BanhoMapper;
import com.caeca.estoque.dto.categoria.CategoriaMapper;
import com.caeca.estoque.dto.material.MaterialMapper;

import java.util.List;

public class JoiaMapper {

    public static Joia toEntity(JoiaEntradaDTO dto) {

        if (dto == null) {
            return null;
        }

        Joia joia = new Joia();

        joia.setNome(dto.getNome());
        joia.setQuantidade(dto.getQuantidade());
        joia.setQuantidadeMinima(dto.getQuantidadeMinima());

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
        joiaSaidaDTO.setStatus(calcularStatus(joia));
        joiaSaidaDTO.setDataCadastro(joia.getDataCadastro());
        joiaSaidaDTO.setDataAtualizacao(joia.getDataAtualizacao());
        joiaSaidaDTO.setQuantidade(joia.getQuantidade());

        if (joia.getCategoria() != null) {
            joiaSaidaDTO.setCategoria(CategoriaMapper.toResponse(joia.getCategoria()));
        } else {
            joiaSaidaDTO.setCategoria(null);
        }

        if (joia.getBanho() != null) {
            joiaSaidaDTO.setBanho(BanhoMapper.toResponse(joia.getBanho()));
        } else {
            joiaSaidaDTO.setBanho(null);
        }

        if (joia.getMaterial() != null) {
            joiaSaidaDTO.setMaterial(MaterialMapper.toResponse(joia.getMaterial()));
        } else {
            joiaSaidaDTO.setMaterial(null);
        }

        if (joia.getArquivo() != null) {
            joiaSaidaDTO.setArquivo(ArquivoMapper.toResponse(joia.getArquivo()));
        } else {
            joiaSaidaDTO.setArquivo(null);
        }

        return joiaSaidaDTO;
    }

    public static List<JoiaSaidaDTO> toResponseList(List<Joia> joias) {
        return joias
                .stream()
                .map(JoiaMapper::toResponse)
                .toList();
    }

    private static JoiaStatus calcularStatus(Joia joia) {
        if (joia.getAtivo() == null || !joia.getAtivo()) {
            return JoiaStatus.INATIVO;
        }

        if (joia.getQuantidade() == 0) {
            return JoiaStatus.CRITICO;
        }

        if (joia.getQuantidade() == null || joia.getQuantidadeMinima() == null) {
            return JoiaStatus.OK;
        }

        if (joia.getQuantidade() <= joia.getQuantidadeMinima()) {
            return JoiaStatus.ALERTA;
        }

        return JoiaStatus.OK;
    }

}
