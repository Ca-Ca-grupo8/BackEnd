package com.caeca.estoque.dto.movimentacao;

public record JoiaMaisSaidaDto(
        Long idJoia,
        String nomeJoia,
        Double quantidadeMedia,
        Long idMotivo,
        String motivoMaisComum
) {
}
