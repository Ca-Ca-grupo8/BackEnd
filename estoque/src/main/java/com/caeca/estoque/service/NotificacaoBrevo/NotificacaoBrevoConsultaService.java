package com.caeca.estoque.service.NotificacaoBrevo;

import com.caeca.estoque.client.BrevoClient;
import com.caeca.estoque.dto.brevo.BrevoEmailListResponse;
import com.caeca.estoque.dto.brevo.NotificacaoBrevoEnvioDto;
import com.caeca.estoque.exception.BadRequestException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Consulta o histórico de e-mails transacionais enviados pela aplicação via Brevo.
 */
@Service
public class NotificacaoBrevoConsultaService {

    private static final long LIMITE_MAXIMO = 100L;
    private static final long LIMITE_PADRAO = 20L;

    private final BrevoClient brevoClient;

    @Value("${brevo.api.key}")
    private String apiKey;

    public NotificacaoBrevoConsultaService(BrevoClient brevoClient) {
        this.brevoClient = brevoClient;
    }

    public List<NotificacaoBrevoEnvioDto> listarEnvios(Long limit, Long offset, String sort,
                                                      String email, Long templateId, String messageId,
                                                      String startDate, String endDate) {
        long limiteEfetivo = limit == null ? LIMITE_PADRAO : limit;
        if (limiteEfetivo < 1 || limiteEfetivo > LIMITE_MAXIMO) {
            throw new BadRequestException("limit deve estar entre 1 e " + LIMITE_MAXIMO);
        }
        if (offset != null && offset < 0) {
            throw new BadRequestException("offset não pode ser negativo");
        }
        if ((startDate == null) != (endDate == null)) {
            throw new BadRequestException("startDate e endDate devem ser informados juntos");
        }

        BrevoEmailListResponse resposta = brevoClient.listarEmailsTransacionais(
                apiKey, limiteEfetivo, offset, sort, email, templateId, messageId, startDate, endDate
        );

        if (resposta == null || resposta.transactionalEmails() == null) {
            return List.of();
        }

        return resposta.transactionalEmails().stream()
                .map(e -> new NotificacaoBrevoEnvioDto(
                        e.date(),
                        e.email(),
                        e.subject(),
                        e.templateId(),
                        NotificacaoBrevoService.nomeTemplate(e.templateId()),
                        e.messageId()
                ))
                .toList();
    }
}
