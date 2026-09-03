package com.caeca.estoque.dto.dashboard;

public record FiltroGiroEstoqueDto(
        double valorAtual,
        double valorAntigo,
        double varianca
){}
