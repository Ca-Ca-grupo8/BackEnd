package com.caeca.estoque.dto.movimentacao;

import com.caeca.estoque.database.model.joia.Joia;
import com.caeca.estoque.database.model.tipoMovimentacao.TipoMovimentacao;
import com.caeca.estoque.database.model.usuario.Usuario;
import jakarta.persistence.Id;

public record MovimentacaoContextDto(
        TipoMovimentacao tipo,
        Usuario usuario,
        MovimentacaoRequestDto dadosOriginais
) {}
