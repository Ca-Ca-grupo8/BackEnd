package com.caeca.estoque.dto.movimentacaoRevendedora;

import com.caeca.estoque.database.model.statusMaleta.StatusMaletaEnum;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public record MaletaResumoDto(
        long totalNaoFinalizado,
        Map<StatusMaletaEnum, Long> totalPorStatus,
        List<ProximoVencimentoDto> topProximoVencimentos
) {
    public record ProximoVencimentoDto(
            Long id,
            String nomeMaleta,
            StatusMaletaEnum status,
            LocalDateTime dataVencimento
    ) {}
}
