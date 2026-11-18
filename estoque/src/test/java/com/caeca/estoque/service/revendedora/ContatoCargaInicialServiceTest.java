package com.caeca.estoque.service.revendedora;

import com.caeca.estoque.database.model.movimentacaoRevendedora.MovimentacaoRevendedora;
import com.caeca.estoque.database.model.revendedora.Revendedora;
import com.caeca.estoque.database.repository.movimentacaoRevendedora.MovimentacaoRevendedoraRepository;
import com.caeca.estoque.database.repository.revendedora.RevendedoraRepository;
import com.caeca.estoque.service.NotificacaoBrevo.NotificacaoBrevoService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ContatoCargaInicialServiceTest {

    @Mock
    private RevendedoraRepository revendedoraRepository;

    @Mock
    private MovimentacaoRevendedoraRepository movimentacaoRepository;

    @Mock
    private NotificacaoBrevoService notificacaoBrevoService;

    private Revendedora revendedora(long id, LocalDate dataCadastro) {
        Revendedora revendedora = new Revendedora();
        revendedora.setId(id);
        revendedora.setNome("Revendedora " + id);
        revendedora.setEmail("r" + id + "@exemplo.com");
        revendedora.setDataCadastro(dataCadastro);
        return revendedora;
    }

    @Test
    @DisplayName("Deve publicar cada revendedora com a data da última maleta, ou a de cadastro quando não há maleta")
    void devePublicarTodasComADataCorreta() {
        Revendedora comMaleta = revendedora(1L, LocalDate.of(2025, 1, 1));
        Revendedora semMaleta = revendedora(2L, LocalDate.of(2025, 2, 1));
        MovimentacaoRevendedora maleta = new MovimentacaoRevendedora();
        maleta.setData(LocalDateTime.of(2026, 9, 15, 10, 0));

        when(revendedoraRepository.findAll()).thenReturn(List.of(comMaleta, semMaleta));
        when(movimentacaoRepository.findFirstByRevendedoraIdOrderByIdMovimentacaoRevendedoraDesc(1L)).thenReturn(maleta);
        when(movimentacaoRepository.findFirstByRevendedoraIdOrderByIdMovimentacaoRevendedoraDesc(2L)).thenReturn(null);

        new ContatoCargaInicialService(revendedoraRepository, movimentacaoRepository, notificacaoBrevoService).executar();

        verify(notificacaoBrevoService).criarContatoRevendedora(comMaleta, LocalDate.of(2026, 9, 15));
        verify(notificacaoBrevoService).criarContatoRevendedora(semMaleta, LocalDate.of(2025, 2, 1));
    }

    @Test
    @DisplayName("Uma falha em uma revendedora não deve impedir as demais")
    void deveContinuarAposFalha() {
        Revendedora primeira = revendedora(1L, LocalDate.of(2025, 1, 1));
        Revendedora segunda = revendedora(2L, LocalDate.of(2025, 1, 1));

        when(revendedoraRepository.findAll()).thenReturn(List.of(primeira, segunda));
        doThrow(new RuntimeException("RabbitMQ indisponivel"))
                .when(notificacaoBrevoService).criarContatoRevendedora(eq(primeira), any());

        new ContatoCargaInicialService(revendedoraRepository, movimentacaoRepository, notificacaoBrevoService).executar();

        verify(notificacaoBrevoService, times(1)).criarContatoRevendedora(eq(segunda), any());
    }
}
