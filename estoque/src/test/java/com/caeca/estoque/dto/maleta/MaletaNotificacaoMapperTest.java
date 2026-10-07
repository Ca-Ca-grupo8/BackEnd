package com.caeca.estoque.dto.maleta;

import com.caeca.estoque.database.model.movimentacaoRevendedora.MovimentacaoRevendedora;
import com.caeca.estoque.database.model.revendedora.Revendedora;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

class MaletaNotificacaoMapperTest {

    private MovimentacaoRevendedora maleta(String nome, String email,
                                            LocalDateTime vencimento, LocalDateTime criacao) {
        Revendedora revendedora = new Revendedora();
        revendedora.setNome(nome);
        revendedora.setEmail(email);

        MovimentacaoRevendedora maleta = new MovimentacaoRevendedora();
        maleta.setRevendedora(revendedora);
        maleta.setDataLimite(vencimento);
        maleta.setData(criacao);

        return maleta;
    }

    @Nested
    @DisplayName("nomeRevendedora")
    class nomeRevendedora {

        @Test
        @DisplayName("Deve devolver o nome da revendedora da maleta")
        void deveDevolverNome() {
            MovimentacaoRevendedora maleta = maleta("Joias Silva", "joias@exemplo.com",
                    null, null);

            assertEquals("Joias Silva", MaletaNotificacaoMapper.nomeRevendedora(maleta));
        }

        @Test
        @DisplayName("Deve devolver string vazia quando a revendedora nao esta carregada")
        void deveDevolverVazioSemRevendedora() {
            MovimentacaoRevendedora maleta = new MovimentacaoRevendedora();

            assertEquals("", MaletaNotificacaoMapper.nomeRevendedora(maleta));
        }
    }

    @Nested
    @DisplayName("emailRevendedora")
    class emailRevendedora {

        @Test
        @DisplayName("Deve devolver o e-mail da revendedora da maleta")
        void deveDevolverEmail() {
            MovimentacaoRevendedora maleta = maleta("Joias Silva", "joias@exemplo.com",
                    null, null);

            assertEquals("joias@exemplo.com", MaletaNotificacaoMapper.emailRevendedora(maleta));
        }

        @Test
        @DisplayName("Deve devolver string vazia quando a maleta e nula")
        void deveDevolverVazioQuandoMaletaNula() {
            assertEquals("", MaletaNotificacaoMapper.emailRevendedora(null));
        }
    }

    @Nested
    @DisplayName("dataVencimento")
    class dataVencimento {

        @Test
        @DisplayName("Deve formatar a data de vencimento em dd/MM/yyyy")
        void deveFormatarData() {
            MovimentacaoRevendedora maleta = maleta("Joias Silva", "joias@exemplo.com",
                    LocalDateTime.of(2026, 1, 15, 18, 30), null);

            assertEquals("15/01/2026", MaletaNotificacaoMapper.dataVencimento(maleta));
        }

        @Test
        @DisplayName("Deve devolver string vazia quando a maleta nao tem vencimento")
        void deveDevolverVazioSemVencimento() {
            MovimentacaoRevendedora maleta = maleta("Joias Silva", "joias@exemplo.com",
                    null, null);

            assertEquals("", MaletaNotificacaoMapper.dataVencimento(maleta));
        }
    }

    @Nested
    @DisplayName("mesReferencia")
    class mesReferencia {

        @Test
        @DisplayName("Deve formatar o mes e ano de criacao por extenso")
        void deveFormatarMesEAno() {
            MovimentacaoRevendedora maleta = maleta("Joias Silva", "joias@exemplo.com",
                    null, LocalDateTime.of(2025, 1, 15, 10, 0));

            assertEquals("janeiro de 2025", MaletaNotificacaoMapper.mesReferencia(maleta));
        }

        @Test
        @DisplayName("Deve formatar meses com acento corretamente")
        void deveFormatarMesesComAcento() {
            MovimentacaoRevendedora maleta = maleta("Joias Silva", "joias@exemplo.com",
                    null, LocalDateTime.of(2026, 8, 3, 10, 0));

            assertEquals("agosto de 2026", MaletaNotificacaoMapper.mesReferencia(maleta));
        }

        @Test
        @DisplayName("Deve formatar dezembro corretamente")
        void deveFormatarDezembro() {
            MovimentacaoRevendedora maleta = maleta("Joias Silva", "joias@exemplo.com",
                    null, LocalDateTime.of(2026, 12, 31, 23, 59));

            assertEquals("dezembro de 2026", MaletaNotificacaoMapper.mesReferencia(maleta));
        }

        @Test
        @DisplayName("Deve devolver string vazia quando a maleta nao tem data de criacao")
        void deveDevolverVazioSemData() {
            MovimentacaoRevendedora maleta = maleta("Joias Silva", "joias@exemplo.com",
                    null, null);

            assertEquals("", MaletaNotificacaoMapper.mesReferencia(maleta));
        }
    }
}