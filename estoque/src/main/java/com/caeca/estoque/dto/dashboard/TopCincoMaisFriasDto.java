package com.caeca.estoque.dto.dashboard;

import java.time.LocalDateTime;

public record TopCincoMaisFriasDto(
        String nome,
        Integer quantidade,
        LocalDateTime ultimaMovimentacao
) {
}
