package com.caeca.estoque.dto.dashboard;

import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

public record FiltroDashboardDto(
        @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSX")
        LocalDateTime dataInicio,
        @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSX")
        LocalDateTime dataTermino,
        Long categoriaId
) {}
