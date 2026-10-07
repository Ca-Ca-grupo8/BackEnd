package com.caeca.estoque.dto.movimentacaoRevendedora;

import com.caeca.estoque.database.model.movimentacaoRevendedora.MovimentacaoRevendedora;
import com.caeca.estoque.database.model.revendedora.Revendedora;
import com.caeca.estoque.database.model.statusMaleta.StatusMaleta;
import com.caeca.estoque.database.model.statusMaleta.StatusMaletaEnum;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("MovimentacaoRevendedoraMapper")
class MovimentacaoRevendedoraMapperTest {

    private MovimentacaoRevendedora entidadeComStatus(StatusMaleta status) {
        Revendedora revendedora = new Revendedora();
        revendedora.setId(1L);
        revendedora.setNome("Joana");

        MovimentacaoRevendedora movimentacaoRevendedora = new MovimentacaoRevendedora();
        movimentacaoRevendedora.setIdMovimentacaoRevendedora(1L);
        movimentacaoRevendedora.setNome("joana-9-2026-4231");
        movimentacaoRevendedora.setRevendedora(revendedora);
        movimentacaoRevendedora.setMovimentacoes(new ArrayList<>());
        movimentacaoRevendedora.setStatus(status);
        return movimentacaoRevendedora;
    }

    @Nested
    @DisplayName("toResponse")
    class toResponse {

        @Test
        @DisplayName("Deve mapear o status da maleta com id e nome")
        void deveMapearStatusDaMaleta() {
            StatusMaleta finalizada = new StatusMaleta(2L, StatusMaletaEnum.FINALIZADA);

            MovimentacaoRevendedoraResponseDto dto =
                    MovimentacaoRevendedoraMapper.toResponse(entidadeComStatus(finalizada));

            assertNotNull(dto.getStatus());
            assertEquals(2L, dto.getStatus().getId());
            assertEquals(StatusMaletaEnum.FINALIZADA, dto.getStatus().getNome());
        }

        @Test
        @DisplayName("Deve retornar status nulo quando a maleta não tiver status")
        void deveRetornarStatusNuloQuandoMaletaNaoTiverStatus() {
            MovimentacaoRevendedoraResponseDto dto =
                    MovimentacaoRevendedoraMapper.toResponse(entidadeComStatus(null));

            assertNull(dto.getStatus());
        }
    }
}
