package com.caeca.estoque.service.movimentacaoRevendedora;

import com.caeca.estoque.database.model.movimentacao.Movimentacao;
import com.caeca.estoque.database.model.movimentacaoRevendedora.MovimentacaoRevendedora;
import com.caeca.estoque.database.model.motivoMovimentacao.MotivoMovimentacao;
import com.caeca.estoque.database.model.revendedora.Revendedora;
import com.caeca.estoque.database.model.tipoMovimentacao.TipoMovimentacao;
import com.caeca.estoque.database.model.tipoMovimentacao.TipoMovimentacaoEnum;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MaletaMetaServiceTest {

    private final MaletaMetaService maletaMetaService = new MaletaMetaService();

    @Nested
    @DisplayName("bateuMeta")
    class bateuMeta {

        @Test
        @DisplayName("Deve retornar false quando a maleta não tiver movimentações")
        void deveRetornarFalseQuandoMaletaNaoTiverMovimentacoes() {
            assertFalse(maletaMetaService.bateuMeta(maleta(List.of())));
        }

        @Test
        @DisplayName("Deve retornar false quando a lista de movimentações for nula")
        void deveRetornarFalseQuandoListaDeMovimentacoesForNula() {
            assertFalse(maletaMetaService.bateuMeta(maleta(null)));
        }

        @Test
        @DisplayName("Deve retornar false quando a maleta for nula")
        void deveRetornarFalseQuandoAMaletaForNula() {
            assertFalse(maletaMetaService.bateuMeta(null));
        }

        @Test
        @DisplayName("Deve retornar false quando houver apenas entradas na maleta")
        void deveRetornarFalseQuandoHouverApenasEntradas() {
            MovimentacaoRevendedora maleta = maleta(List.of(
                    movimentacao(TipoMovimentacaoEnum.ENTRADA, 10, 40.0)
            ));

            assertFalse(maletaMetaService.bateuMeta(maleta));
        }

        @Test
        @DisplayName("Deve retornar true quando todas as peças que saíram da maleta tiverem sido vendidas")
        void deveRetornarTrueQuandoTodasAsPecasQueSairamTiveremSidoVendidas() {
            MovimentacaoRevendedora maleta = maleta(List.of(
                    movimentacao(TipoMovimentacaoEnum.SAIDA, 10, 40.0)
            ));

            assertTrue(maletaMetaService.bateuMeta(maleta));
        }

        @Test
        @DisplayName("Deve retornar true quando a meta for batida exatamente em 90 por cento")
        void deveRetornarTrueQuandoMetaForBatidaExatamenteEm90PorCento() {
            MovimentacaoRevendedora maleta = maleta(List.of(
                    movimentacao(TipoMovimentacaoEnum.SAIDA, 10, 40.0),
                    movimentacao(TipoMovimentacaoEnum.REENTRADA, 1, null)
            ));

            assertTrue(maletaMetaService.bateuMeta(maleta));
        }

        @Test
        @DisplayName("Deve retornar false quando o percentual de venda ficar abaixo de 90 por cento")
        void deveRetornarFalseQuandoPercentualDeVendaFicarAbaixoDe90PorCento() {
            MovimentacaoRevendedora maleta = maleta(List.of(
                    movimentacao(TipoMovimentacaoEnum.SAIDA, 9, 40.0),
                    movimentacao(TipoMovimentacaoEnum.REENTRADA, 1, null)
            ));

            assertFalse(maletaMetaService.bateuMeta(maleta));
        }

        @Test
        @DisplayName("Deve retornar false quando houver reentrada acima do percentual tolerado")
        void deveRetornarFalseQuandoHouverReentradaAcimaDoPercentualTolerado() {
            MovimentacaoRevendedora maleta = maleta(List.of(
                    movimentacao(TipoMovimentacaoEnum.SAIDA, 10, 40.0),
                    movimentacao(TipoMovimentacaoEnum.REENTRADA, 2, null)
            ));

            assertFalse(maletaMetaService.bateuMeta(maleta));
        }

        @Test
        @DisplayName("Deve retornar false sem gerar quantidade negativa quando a reentrada superar a saída")
        void deveRetornarFalseSemGerarQuantidadeNegativaQuandoReentradaSuperarSaida() {
            MovimentacaoRevendedora maleta = maleta(List.of(
                    movimentacao(TipoMovimentacaoEnum.SAIDA, 1, 40.0),
                    movimentacao(TipoMovimentacaoEnum.REENTRADA, 3, null)
            ));

            assertFalse(maletaMetaService.bateuMeta(maleta));
            assertEquals("0", maletaMetaService.montarParametrosMeta(maleta).get("quantidadeVendida"));
        }

        @Test
        @DisplayName("Deve ignorar movimento sem motivo de movimentação")
        void deveIgnorarMovimentoSemMotivoDeMovimentacao() {
            Movimentacao semMotivo = new Movimentacao();
            semMotivo.setQuantidade(10);
            semMotivo.setPrecoSaida(40.0);

            assertFalse(maletaMetaService.bateuMeta(maleta(List.of(semMotivo))));
        }

        @Test
        @DisplayName("Deve ignorar movimento cujo motivo não tenha tipo de movimentação")
        void deveIgnorarMovimentoCujoMotivoNaoTenhaTipoDeMovimentacao() {
            Movimentacao semTipo = new Movimentacao();
            semTipo.setQuantidade(10);
            semTipo.setPrecoSaida(40.0);
            semTipo.setMotivoMovimentacao(new MotivoMovimentacao());

            assertFalse(maletaMetaService.bateuMeta(maleta(List.of(semTipo))));
        }

        @Test
        @DisplayName("Deve ignorar movimento sem quantidade informada sem quebrar o cálculo")
        void deveIgnorarMovimentoSemQuantidadeInformadaSemQuebrarOCalculo() {
            MovimentacaoRevendedora maleta = maleta(List.of(
                    movimentacao(TipoMovimentacaoEnum.SAIDA, 10, 40.0),
                    movimentacao(TipoMovimentacaoEnum.SAIDA, null, 40.0),
                    movimentacao(TipoMovimentacaoEnum.REENTRADA, 2, null)
            ));

            assertFalse(maletaMetaService.bateuMeta(maleta));
            assertEquals("8", maletaMetaService.montarParametrosMeta(maleta).get("quantidadeVendida"));
        }
    }

    @Nested
    @DisplayName("montarParametrosMeta")
    class montarParametrosMeta {

        @Test
        @DisplayName("Deve montar todos os parâmetros com o mês de referência por extenso")
        void deveMontarTodosOsParametrosComOMesDeReferenciaPorExtenso() {
            MovimentacaoRevendedora maleta = maleta(List.of(
                    movimentacao(TipoMovimentacaoEnum.SAIDA, 4, 40.0)
            ));
            maleta.setData(LocalDateTime.of(2026, 9, 15, 10, 0));
            maleta.setRevendedora(revendedora("Maria da Silva"));

            Map<String, String> params = maletaMetaService.montarParametrosMeta(maleta);

            assertEquals("Maria da Silva", params.get("nomeRevendedora"));
            assertEquals("setembro de 2026", params.get("mes"));
            assertEquals("4", params.get("quantidadeVendida"));
            assertEquals("160.00", params.get("valorTotalVendas"));
        }

        @Test
        @DisplayName("Deve devolver nome vazio quando a maleta não tiver revendedora")
        void deveDevolverNomeVazioQuandoMaletaNaoTiverRevendedora() {
            MovimentacaoRevendedora maleta = maleta(List.of(
                    movimentacao(TipoMovimentacaoEnum.SAIDA, 1, 40.0)
            ));

            assertEquals("", maletaMetaService.montarParametrosMeta(maleta).get("nomeRevendedora"));
        }

        @Test
        @DisplayName("Deve descontar da quantidade vendida as peças que voltaram para o estoque")
        void deveDescontarDaQuantidadeVendidaAsPecasQueVoltaramParaOEstoque() {
            MovimentacaoRevendedora maleta = maleta(List.of(
                    movimentacao(TipoMovimentacaoEnum.SAIDA, 10, 40.0),
                    movimentacao(TipoMovimentacaoEnum.REENTRADA, 1, null)
            ));

            assertEquals("9", maletaMetaService.montarParametrosMeta(maleta).get("quantidadeVendida"));
        }

        @Test
        @DisplayName("Deve descontar do valor total as reentradas que tiverem preço de saída")
        void deveDescontarDoValorTotalAsReentradasQueTiveremPrecoDeSaida() {
            MovimentacaoRevendedora maleta = maleta(List.of(
                    movimentacao(TipoMovimentacaoEnum.SAIDA, 1, 100.0),
                    movimentacao(TipoMovimentacaoEnum.REENTRADA, 1, 40.0)
            ));

            assertEquals("60.00", maletaMetaService.montarParametrosMeta(maleta).get("valorTotalVendas"));
        }

        @Test
        @DisplayName("Deve considerar zero o valor da reentrada que não tiver preço de saída gravado")
        void deveConsiderarZeroOValorDaReentradaQueNaoTiverPrecoDeSaidaGravado() {
            MovimentacaoRevendedora maleta = maleta(List.of(
                    movimentacao(TipoMovimentacaoEnum.SAIDA, 2, 50.0),
                    movimentacao(TipoMovimentacaoEnum.REENTRADA, 1, null)
            ));

            assertEquals("100.00", maletaMetaService.montarParametrosMeta(maleta).get("valorTotalVendas"));
        }

        @Test
        @DisplayName("Deve devolver valores zerados quando a maleta não tiver movimentações")
        void deveDevolverValoresZeradosQuandoMaletaNaoTiverMovimentacoes() {
            Map<String, String> params = maletaMetaService.montarParametrosMeta(maleta(List.of()));

            assertEquals("0", params.get("quantidadeVendida"));
            assertEquals("0.00", params.get("valorTotalVendas"));
        }
    }

    private MovimentacaoRevendedora maleta(List<Movimentacao> movimentacoes) {
        MovimentacaoRevendedora maleta = new MovimentacaoRevendedora();
        maleta.setIdMovimentacaoRevendedora(1L);
        maleta.setNome("Maleta de teste");
        maleta.setMovimentacoes(movimentacoes);
        return maleta;
    }

    private Movimentacao movimentacao(TipoMovimentacaoEnum tipo, Integer quantidade, Double precoSaida) {
        MotivoMovimentacao motivo = new MotivoMovimentacao();
        motivo.setTipoMovimentacao(new TipoMovimentacao(1L, tipo));

        Movimentacao movimentacao = new Movimentacao();
        movimentacao.setQuantidade(quantidade);
        movimentacao.setPrecoSaida(precoSaida);
        movimentacao.setMotivoMovimentacao(motivo);
        return movimentacao;
    }

    private Revendedora revendedora(String nome) {
        Revendedora revendedora = new Revendedora();
        revendedora.setNome(nome);
        return revendedora;
    }
}
