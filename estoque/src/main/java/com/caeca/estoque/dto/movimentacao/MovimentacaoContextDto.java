package com.caeca.estoque.dto.movimentacao;

import com.caeca.estoque.database.model.joia.Joia;
import com.caeca.estoque.database.model.tipoMovimentacao.TipoMovimentacao;
import com.caeca.estoque.database.model.usuario.Usuario;

public record MovimentacaoContextDto(
        Joia joia,
        TipoMovimentacao tipo,
        Usuario usuario,
        MovimentacaoRequestDto dadosOriginais
) {}
