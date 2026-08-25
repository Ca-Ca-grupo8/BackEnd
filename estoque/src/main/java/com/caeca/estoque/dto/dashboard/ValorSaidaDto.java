package com.caeca.estoque.dto.dashboard;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ValorSaidaDto {
    private Double valorAtual;
    private Double valorAntigo;
    private Double varianca;
}
