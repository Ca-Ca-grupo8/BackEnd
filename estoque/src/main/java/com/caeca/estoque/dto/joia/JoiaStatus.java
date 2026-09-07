package com.caeca.estoque.dto.joia;

import com.caeca.estoque.database.model.joia.Joia;

public enum JoiaStatus {
    INATIVO,
    CRITICO,
    ALERTA,
    OK;

    public static JoiaStatus calcularStatus(Joia joia) {
        if (joia == null || joia.getAtivo() == null || !joia.getAtivo()) {
            return INATIVO;
        }

        if (joia.getQuantidade() == null || joia.getQuantidade() == 0) {
            return CRITICO;
        }

        if (joia.getQuantidadeMinima() != null && joia.getQuantidade() <= joia.getQuantidadeMinima()) {
            return ALERTA;
        }

        return OK;
    }
}
