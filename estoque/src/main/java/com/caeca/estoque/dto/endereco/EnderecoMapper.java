package com.caeca.estoque.dto.endereco;

import com.caeca.estoque.database.model.endereco.Endereco;

public class EnderecoMapper {
    public static EnderecoResponseDTO toResponse(Endereco e) {
        return EnderecoResponseDTO.builder()
                .id(e.getId())
                .cep(e.getCep())
                .logradouro(e.getLogradouro())
                .numero(e.getNumero())
                .complemento(e.getComplemento())
                .bairro(e.getBairro())
                .cidade(e.getCidade())
                .uf(e.getUf())
                .build();
    }

    public static Endereco toEntity(EnderecoRequestDTO dto) {
        return Endereco.builder()
                .cep(dto.getCep())
                .logradouro(dto.getLogradouro())
                .numero(dto.getNumero())
                .complemento(dto.getComplemento())
                .bairro(dto.getBairro())
                .cidade(dto.getCidade())
                .uf(dto.getUf())
                .build();
    }
}
