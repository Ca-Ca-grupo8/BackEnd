package com.caeca.estoque.dto.tipoMovimentacao;

import com.caeca.estoque.database.model.tipoMovimentacao.TipoMovimentacaoEnum;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class TipoMovimentacaoRequestDto {

    @NotBlank(message = "O tipo de movimentação é obrigatório")
    @Size(max = 15, message = "O tipo de movimentação deve ser 'SAIDA', 'ENTRADA' ou 'REENTRADA'")
    @NotNull

    private TipoMovimentacaoEnum nome;

    public TipoMovimentacaoRequestDto() {
    }

    public TipoMovimentacaoRequestDto(TipoMovimentacaoEnum nome) {
        this.nome = nome;
    }

    public TipoMovimentacaoEnum getNome() {
        return nome;
    }

    public void setNome(TipoMovimentacaoEnum nome) {
        this.nome = nome;
    }
}
