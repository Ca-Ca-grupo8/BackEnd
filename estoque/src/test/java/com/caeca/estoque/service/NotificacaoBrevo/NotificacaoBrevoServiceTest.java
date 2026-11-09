package com.caeca.estoque.service.NotificacaoBrevo;

import com.caeca.estoque.database.model.movimentacaoRevendedora.MovimentacaoRevendedora;
import com.caeca.estoque.database.model.revendedora.Revendedora;
import com.caeca.estoque.database.model.statusMaleta.StatusMaleta;
import com.caeca.estoque.database.model.statusMaleta.StatusMaletaEnum;
import com.caeca.estoque.dto.brevo.NotificacaoEventoDto;
import com.caeca.estoque.producer.interfaceProducer.ContatoProducer;
import com.caeca.estoque.producer.interfaceProducer.NotificacaoProducer;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

@ExtendWith(MockitoExtension.class)
class NotificacaoBrevoServiceTest {

    @Mock
    private NotificacaoProducer notificacaoProducer;

    @Mock
    private ContatoProducer contatoProducer;

    @InjectMocks
    private NotificacaoBrevoService notificacaoBrevoService;

    private MovimentacaoRevendedora maleta() {
        Revendedora revendedora = new Revendedora();
        revendedora.setNome("Joias Silva");
        revendedora.setEmail("joias@exemplo.com");

        MovimentacaoRevendedora maleta = new MovimentacaoRevendedora();
        maleta.setRevendedora(revendedora);
        maleta.setDataLimite(LocalDateTime.of(2026, 1, 15, 18, 30));
        maleta.setData(LocalDateTime.of(2025, 1, 10, 9, 0));

        return maleta;
    }

    @Nested
    @DisplayName("notificacaoLoteEmConsignacao")
    class notificacaoLoteEmConsignacao {

        @Test
        @DisplayName("Deve enviar o template 3 com o comprovante em PDF anexado (Base64)")
        void deveEnviarTemplateTresComAnexo() {
            MovimentacaoRevendedora maleta = maleta();
            maleta.setIdMovimentacaoRevendedora(9L);
            byte[] comprovante = new byte[]{1, 2, 3};

            notificacaoBrevoService.notificacaoLoteEmConsignacao(maleta, comprovante);

            verify(notificacaoProducer, times(1)).enviarExternoComAnexos(
                    "joias@exemplo.com",
                    "Joias Silva",
                    3L,
                    Map.of("nomeRevendedora", "Joias Silva", "dataVencimento", "15/01/2026"),
                    List.of(new NotificacaoEventoDto.Anexo(
                            "comprovante-maleta-9.pdf",
                            java.util.Base64.getEncoder().encodeToString(comprovante))));
            verifyNoInteractions(contatoProducer);
        }

        @Test
        @DisplayName("Deve enviar o template 3 sem anexos quando o comprovante for nulo")
        void deveEnviarSemAnexoQuandoComprovanteNulo() {
            MovimentacaoRevendedora maleta = maleta();
            maleta.setIdMovimentacaoRevendedora(9L);

            notificacaoBrevoService.notificacaoLoteEmConsignacao(maleta, null);

            verify(notificacaoProducer, times(1)).enviarExternoComAnexos(
                    "joias@exemplo.com",
                    "Joias Silva",
                    3L,
                    Map.of("nomeRevendedora", "Joias Silva", "dataVencimento", "15/01/2026"),
                    List.of());
        }
    }

    @Nested
    @DisplayName("sincronizarContatoMaletaEmConsignacao")
    class sincronizarContatoMaletaEmConsignacao {

        @Test
        @DisplayName("Deve publicar os atributos da maleta para sincronizar o contato")
        void devePublicarAtributosDaMaleta() {
            MovimentacaoRevendedora maleta = maleta();
            maleta.getRevendedora().setId(42L);
            maleta.setIdMovimentacaoRevendedora(9L);
            maleta.setStatus(new StatusMaleta(2L, StatusMaletaEnum.EM_CONSIGNACAO));

            notificacaoBrevoService.sincronizarContatoMaletaEmConsignacao(maleta);

            verify(contatoProducer, times(1)).atualizarContatoMaleta(42L, Map.of(
                    "ID_ULTIMA_MALETA", 9L,
                    "STATUS_MALETA", "EM_CONSIGNACAO",
                    "DATA_VENCIMENTO_MALETA", "2026-01-15",
                    "CREATION_DATE", "2025-01-10"));
            verifyNoInteractions(notificacaoProducer);
        }
    }

    @Nested
    @DisplayName("criarContatoRevendedora")
    class criarContatoRevendedora {

        @Test
        @DisplayName("Deve publicar a criação do contato com id, nome, email e data de nascimento")
        void devePublicarCriacaoDaRevendedora() {
            Revendedora revendedora = new Revendedora();
            revendedora.setId(42L);
            revendedora.setNome("Joias Silva");
            revendedora.setEmail("joias@exemplo.com");
            revendedora.setTelefone("11987654321");
            revendedora.setAtivo(true);
            revendedora.setDataNascimento(LocalDate.of(1990, 3, 7));
            revendedora.setDataCadastro(LocalDate.of(2025, 2, 1));

            notificacaoBrevoService.criarContatoRevendedora(revendedora);

            // Sem maleta ainda: a data usada pela automação de inatividade é a de cadastro.
            verify(contatoProducer, times(1)).criarContatoRevendedora(
                    42L, "Joias Silva", "joias@exemplo.com", "11987654321", true,
                    LocalDate.of(1990, 3, 7), LocalDate.of(2025, 2, 1));
            verifyNoInteractions(notificacaoProducer);
        }

        @Test
        @DisplayName("Deve usar a data informada como data da última maleta quando ela existir")
        void deveUsarDataDaUltimaMaleta() {
            Revendedora revendedora = new Revendedora();
            revendedora.setId(42L);
            revendedora.setNome("Joias Silva");
            revendedora.setEmail("joias@exemplo.com");

            notificacaoBrevoService.criarContatoRevendedora(revendedora, LocalDate.of(2026, 10, 1));

            verify(contatoProducer, times(1)).criarContatoRevendedora(
                    42L, "Joias Silva", "joias@exemplo.com", null, false, null, LocalDate.of(2026, 10, 1));
        }
    }

    @Nested
    @DisplayName("atualizarContatoRevendedora")
    class atualizarContatoRevendedora {

        @Test
        @DisplayName("Deve enviar os dados da revendedora sem email quando o email não mudou")
        void deveAtualizarSemEmailQuandoNaoMudou() {
            Revendedora revendedora = new Revendedora();
            revendedora.setId(42L);
            revendedora.setNome("Joias Nova");
            revendedora.setEmail("joias@exemplo.com");
            revendedora.setTelefone("11987654321");
            revendedora.setAtivo(false);

            notificacaoBrevoService.atualizarContatoRevendedora(revendedora, "joias@exemplo.com");

            verify(contatoProducer, times(1)).atualizarContatoRevendedora(eq(42L), isNull(), eq(Map.of(
                    "NOME", "Joias Nova",
                    "TELEFONE", "11987654321",
                    "ATIVO", false)));
        }

        @Test
        @DisplayName("Deve enviar o novo email quando ele mudou")
        void deveEnviarNovoEmailQuandoMudou() {
            Revendedora revendedora = new Revendedora();
            revendedora.setId(42L);
            revendedora.setNome("Joias");
            revendedora.setEmail("novo@exemplo.com");
            revendedora.setTelefone("11987654321");
            revendedora.setAtivo(true);

            notificacaoBrevoService.atualizarContatoRevendedora(revendedora, "antigo@exemplo.com");

            verify(contatoProducer, times(1)).atualizarContatoRevendedora(eq(42L), eq("novo@exemplo.com"), eq(Map.of(
                    "NOME", "Joias",
                    "TELEFONE", "11987654321",
                    "ATIVO", true,
                    "EMAIL", "novo@exemplo.com")));
        }
    }

    @Nested
    @DisplayName("atualizarUltimaMaleta")
    class atualizarUltimaMaleta {

        @Test
        @DisplayName("Deve enviar id, status e data da última maleta")
        void deveEnviarIdStatusEDataDaMaleta() {
            MovimentacaoRevendedora maleta = maleta();
            maleta.getRevendedora().setId(42L);
            maleta.setIdMovimentacaoRevendedora(9L);
            maleta.setStatus(new StatusMaleta(2L, StatusMaletaEnum.EM_ACERTO));
            maleta.setData(LocalDateTime.of(2026, 10, 1, 9, 0));
            maleta.setDataLimite(LocalDateTime.of(2026, 11, 30, 18, 0));

            notificacaoBrevoService.atualizarUltimaMaleta(maleta);

            verify(contatoProducer, times(1)).atualizarContatoMaleta(42L, Map.of(
                    "ID_ULTIMA_MALETA", 9L,
                    "STATUS_MALETA", "EM_ACERTO",
                    "DATA_ULTIMA_MALETA", "2026-10-01",
                    "DATA_VENCIMENTO_MALETA", "2026-11-30"));
        }
    }

    @Nested
    @DisplayName("notificacaoMetaAtingida")
    class notificacaoMetaAtingida {

        @Test
        @DisplayName("Deve enviar o template 8 quando a meta foi atingida")
        void deveEnviarTemplateOito() {
            Map<String, String> params = Map.of("mes", "janeiro de 2025");

            notificacaoBrevoService.notificacaoMetaAtingida(maleta(), params, true);

            verify(notificacaoProducer, times(1)).enviarExterno(
                    "joias@exemplo.com",
                    "Joias Silva",
                    8L,
                    params);
        }

        @Test
        @DisplayName("Deve enviar o template 6 de fechamento quando a meta nao foi atingida")
        void deveEnviarTemplateSeis() {
            Map<String, String> params = Map.of("mes", "janeiro de 2025");

            notificacaoBrevoService.notificacaoMetaAtingida(maleta(), params, false);

            verify(notificacaoProducer, times(1)).enviarExterno(
                    "joias@exemplo.com",
                    "Joias Silva",
                    6L,
                    params);
        }
    }
}
