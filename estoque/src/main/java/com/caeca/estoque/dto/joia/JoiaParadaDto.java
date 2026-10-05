package com.caeca.estoque.dto.joia;

import com.caeca.estoque.database.model.joia.Joia;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record JoiaParadaDto(
        Joia joia,
        Integer quantidade,
        Double valorCustoTotal,
        LocalDateTime dataUltimaSaida
) {}