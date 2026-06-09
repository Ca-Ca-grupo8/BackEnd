package com.caeca.estoque.dto.movimentacao;

import com.caeca.estoque.database.model.motivoMovimentacao.MotivoMovimentacao;
import com.caeca.estoque.database.model.usuario.Usuario;

public record MovimentacaoContextDto(
        MotivoMovimentacao tipo,
        Usuario usuario,
        MovimentacaoRequestDto dadosOriginais,
        org.springframework.web.multipart.MultipartFile arquivo) {}
