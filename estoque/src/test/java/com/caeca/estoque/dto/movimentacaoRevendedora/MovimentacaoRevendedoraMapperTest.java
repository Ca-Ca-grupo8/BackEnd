package com.caeca.estoque.dto.movimentacaoRevendedora;

import com.caeca.estoque.database.model.joia.Joia;
import com.caeca.estoque.database.model.movimentacao.Movimentacao;
import com.caeca.estoque.database.model.movimentacaoRevendedora.MovimentacaoRevendedora;
import com.caeca.estoque.database.model.motivoMovimentacao.MotivoMovimentacao;
import com.caeca.estoque.database.model.revendedora.Revendedora;
import com.caeca.estoque.database.model.tipoMovimentacao.TipoMovimentacao;
import com.caeca.estoque.database.model.tipoMovimentacao.TipoMovimentacaoEnum;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class MovimentacaoRevendedoraMapperTest {

    @Nested
    @DisplayName("toResponse")
    class ToResponse {

        @Test
        @DisplayName("Deve descontar as reentradas do totalVenda")
        void deveDescontarReentradasDoTotalVenda() {
            MovimentacaoRevendedora maleta = maleta(List.of(
                    movimentacao(TipoMovimentacaoEnum.SAIDA, 1, 100.0),
                    movimentacao(TipoMovimentacaoEnum.REENTRADA, 1, 40.0)
            ));

            MovimentacaoRevendedoraResponseDto dto = MovimentacaoRevendedoraMapper.toResponse(maleta);

            assertEquals(60.0, dto.getTotalVenda(), 0.001);
        }

        @Test
        @DisplayName("Deve manter o total apenas com as saídas quando não houver reentradas")
        void deveManterTotalApenasComSaidas() {
            MovimentacaoRevendedora maleta = maleta(List.of(
                    movimentacao(TipoMovimentacaoEnum.SAIDA, 2, 50.0)
            ));

            assertEquals(100.0, MovimentacaoRevendedoraMapper.toResponse(maleta).getTotalVenda(), 0.001);
        }

        @Test
        @DisplayName("Deve expor o tipo de cada movimentação para o front")
        void deveExporTipoDeCadaMovimentacao() {
            MovimentacaoRevendedora maleta = maleta(List.of(
                    movimentacao(TipoMovimentacaoEnum.SAIDA, 1, 100.0),
                    movimentacao(TipoMovimentacaoEnum.REENTRADA, 1, 40.0)
            ));

            MovimentacaoRevendedoraResponseDto dto = MovimentacaoRevendedoraMapper.toResponse(maleta);

            assertEquals(TipoMovimentacaoEnum.SAIDA, dto.getMovimentacoes().get(0).getTipo());
            assertEquals(TipoMovimentacaoEnum.REENTRADA, dto.getMovimentacoes().get(1).getTipo());
        }
    }

    private MovimentacaoRevendedora maleta(List<Movimentacao> movimentacoes) {
        MovimentacaoRevendedora maleta = new MovimentacaoRevendedora();
        maleta.setIdMovimentacaoRevendedora(1L);
        maleta.setNome("Maleta de teste");

        Revendedora revendedora = new Revendedora();
        revendedora.setId(1L);
        revendedora.setNome("Maria");
        maleta.setRevendedora(revendedora);

        maleta.setMovimentacoes(movimentacoes);
        return maleta;
    }

    private Movimentacao movimentacao(TipoMovimentacaoEnum tipo, Integer quantidade, Double precoSaida) {
        MotivoMovimentacao motivo = new MotivoMovimentacao();
        motivo.setTipoMovimentacao(new TipoMovimentacao(1L, tipo));

        Joia joia = new Joia();
        joia.setId(1L);
        joia.setNome("Joia de teste");

        Movimentacao movimentacao = new Movimentacao();
        movimentacao.setQuantidade(quantidade);
        movimentacao.setPrecoSaida(precoSaida);
        movimentacao.setMotivoMovimentacao(motivo);
        movimentacao.setJoia(joia);
        return movimentacao;
    }
}
