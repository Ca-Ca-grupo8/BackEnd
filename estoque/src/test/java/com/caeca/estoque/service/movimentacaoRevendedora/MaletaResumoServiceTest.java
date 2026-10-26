package com.caeca.estoque.service.movimentacaoRevendedora;

import com.caeca.estoque.database.model.movimentacaoRevendedora.MovimentacaoRevendedora;
import com.caeca.estoque.database.model.statusMaleta.StatusMaleta;
import com.caeca.estoque.database.model.statusMaleta.StatusMaletaEnum;
import com.caeca.estoque.database.repository.movimentacao.MovimentacaoRepository;
import com.caeca.estoque.database.repository.movimentacaoRevendedora.MovimentacaoRevendedoraRepository;
import com.caeca.estoque.dto.movimentacaoRevendedora.MaletaResumoDto;
import com.caeca.estoque.service.revendedora.RevendedoraService;
import com.caeca.estoque.service.statusMaleta.StatusMaletaService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class MaletaResumoServiceTest {

    @Test
    @DisplayName("Deve contar as maletas em andamento por status e listar as mais próximas do vencimento")
    void deveMontarResumo() {
        MovimentacaoRevendedoraRepository repository = mock(MovimentacaoRevendedoraRepository.class);
        var service = new MovimentacaoRevendedoraService(repository, mock(MovimentacaoRepository.class),
                mock(RevendedoraService.class), mock(StatusMaletaService.class), mock(MaletaNotificacaoService.class),
                mock(MaletaMetaService.class));

        when(repository.findByStatusNomeNotIn(any())).thenReturn(List.of(
                maleta(1L, "maleta-a", StatusMaletaEnum.EM_CONSIGNACAO, LocalDateTime.of(2026, 11, 10, 0, 0)),
                maleta(2L, "maleta-b", StatusMaletaEnum.EM_PREPARACAO, LocalDateTime.of(2026, 10, 25, 0, 0)),
                maleta(3L, "maleta-c", StatusMaletaEnum.EM_CONSIGNACAO, null)
        ));

        MaletaResumoDto resumo = service.resumoNaoFinalizadas();

        assertEquals(3, resumo.totalNaoFinalizado());
        assertEquals(1L, resumo.totalPorStatus().get(StatusMaletaEnum.EM_PREPARACAO));
        assertEquals(2L, resumo.totalPorStatus().get(StatusMaletaEnum.EM_CONSIGNACAO));
        assertEquals(0L, resumo.totalPorStatus().get(StatusMaletaEnum.EM_ACERTO));
        assertEquals(2, resumo.topProximoVencimentos().size());
        assertEquals("maleta-b", resumo.topProximoVencimentos().get(0).nomeMaleta());
        assertEquals("maleta-a", resumo.topProximoVencimentos().get(1).nomeMaleta());
    }

    @Test
    @DisplayName("Sem maletas em andamento, deve retornar zeros e lista vazia")
    void deveRetornarVazioSemMaletas() {
        MovimentacaoRevendedoraRepository repository = mock(MovimentacaoRevendedoraRepository.class);
        var service = new MovimentacaoRevendedoraService(repository, mock(MovimentacaoRepository.class),
                mock(RevendedoraService.class), mock(StatusMaletaService.class), mock(MaletaNotificacaoService.class),
                mock(MaletaMetaService.class));

        when(repository.findByStatusNomeNotIn(any())).thenReturn(List.of());

        MaletaResumoDto resumo = service.resumoNaoFinalizadas();

        assertEquals(0, resumo.totalNaoFinalizado());
        assertEquals(0L, resumo.totalPorStatus().get(StatusMaletaEnum.EM_PREPARACAO));
        assertEquals(List.of(), resumo.topProximoVencimentos());
    }

    private static MovimentacaoRevendedora maleta(Long id, String nome, StatusMaletaEnum status, LocalDateTime dataLimite) {
        var maleta = new MovimentacaoRevendedora();
        maleta.setIdMovimentacaoRevendedora(id);
        maleta.setNome(nome);
        maleta.setStatus(new StatusMaleta(id, status));
        maleta.setDataLimite(dataLimite);
        return maleta;
    }
}
