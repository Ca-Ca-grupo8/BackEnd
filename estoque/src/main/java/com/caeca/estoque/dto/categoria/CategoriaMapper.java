package com.caeca.estoque.dto.categoria;

import com.caeca.estoque.database.model.categoria.Categoria;

import java.util.List;

public class CategoriaMapper {

    public static Categoria toEntity(CategoriaEntradaDto categoriaEntradaDTO) {

        if (categoriaEntradaDTO == null) {
            return null;
        }

        Categoria categoria = new Categoria();

        categoria.setNome(categoriaEntradaDTO.getNome());

        return categoria;
    }

    public static CategoriaSaidaDto toResponse(Categoria categoria) {

        if (categoria == null) {
            return null;
        }

        CategoriaSaidaDto categoriaSaidaDTO = new CategoriaSaidaDto();

        categoriaSaidaDTO.setId(categoria.getId());
        categoriaSaidaDTO.setNome(categoria.getNome());

        return categoriaSaidaDTO;
    }

    public static List<CategoriaSaidaDto> toResponseList(List<Categoria> categorias) {

        return categorias
                .stream()
                .map(CategoriaMapper::toResponse)
                .toList();
    }
}
