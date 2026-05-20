package com.caeca.estoque.dto.movimentacao;

import com.caeca.estoque.database.model.tipoMovimentacao.TipoMovimentacao;
import com.caeca.estoque.database.model.usuario.Usuario;

public record MovimentacaoContextDto(
        TipoMovimentacao tipo,
        Usuario usuario,
        MovimentacaoRequestDto dadosOriginais,
        org.springframework.web.multipart.MultipartFile arquivo) {}
