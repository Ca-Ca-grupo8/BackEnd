package com.caeca.estoque.service.rotina;

import com.caeca.estoque.database.model.notificacaoEnvio.TipoReferencia;

import java.time.LocalDate;

public interface RotinaDiaria {
    TipoReferencia nome();
    void executar(LocalDate hoje);
}
