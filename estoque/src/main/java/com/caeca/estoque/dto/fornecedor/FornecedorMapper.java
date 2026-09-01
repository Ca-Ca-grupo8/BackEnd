package com.caeca.estoque.dto.fornecedor;

import com.caeca.estoque.database.model.fornecedores.Fornecedor;

public class FornecedorMapper {
    public static FornecedorResponseDTO toResponseDTO(Fornecedor f) {
        return new FornecedorResponseDTO(
                f.getId(),
                f.getNome(),
                f.getInstagram(),
                f.getTelefone(),
                f.getIsAtivo(),
                f.getDataCadastro()
        );
    }
}
