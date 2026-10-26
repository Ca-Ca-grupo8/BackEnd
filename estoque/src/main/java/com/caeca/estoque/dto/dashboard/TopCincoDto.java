package com.caeca.estoque.dto.dashboard;

/**
 * Linha do ranking de mais vendidos. O id é o da categoria quando a listagem
 * é por categoria e o da jóia quando há filtro de categoria (aí o ranking
 * passa a listar produtos) — é o que permite ao frontend navegar para o
 * destino certo ao clicar em "Acessar".
 */
public record TopCincoDto(
        Long id,
        String nome,
        Long quantidadeSaidas
) {
}
