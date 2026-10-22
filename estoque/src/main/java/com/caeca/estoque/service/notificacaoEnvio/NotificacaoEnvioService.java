package com.caeca.estoque.service.notificacaoEnvio;

import com.caeca.estoque.database.model.notificacaoEnvio.NotificacaoEnvio;
import com.caeca.estoque.database.model.notificacaoEnvio.TipoReferencia;
import com.caeca.estoque.database.repository.notificacaoEnvio.NotificacaoEnvioRepository;
import com.caeca.estoque.dto.notificacaoEnvio.NotificacaoEnvioDto;
import com.caeca.estoque.service.NotificacaoBrevo.NotificacaoBrevoService;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import org.slf4j.Logger;

@Service
public class NotificacaoEnvioService {

    private static final Logger LOG = LoggerFactory.getLogger(NotificacaoEnvioService.class);

    private final NotificacaoEnvioRepository notificacaoEnvioRepository;

    NotificacaoEnvioService(NotificacaoEnvioRepository notificacaoEnvioRepository) {
        this.notificacaoEnvioRepository = notificacaoEnvioRepository;
    }

    public boolean registrarEnvio(TipoReferencia tipoReferencia, long referenciaId, long templateId, boolean permiteReenvio) {

        try {
            if (notificacaoEnvioRepository.existsByTipoReferenciaAndReferenciaIdAndTemplateIdAndPermiteReenvioFalse(
                    tipoReferencia, referenciaId, templateId)) {
                return false;
            }

            notificacaoEnvioRepository.save(new NotificacaoEnvio(tipoReferencia, referenciaId, templateId, permiteReenvio));

            return true;
        } catch (Exception e) {
            LOG.warn("Nao foi possivel registrar o envio.");

            return false;
        }
    }

    public Page<NotificacaoEnvioDto> listarEnvios(TipoReferencia tipoReferencia, Long templateId, Pageable pageable) {
        return notificacaoEnvioRepository.buscarEnvios(tipoReferencia, templateId, pageable)
                .map(this::toDto);
    }

    private NotificacaoEnvioDto toDto(NotificacaoEnvio envio) {
        return new NotificacaoEnvioDto(
                envio.getIdNotificacaoEnvio(),
                envio.getDataEnvio(),
                envio.getTipoReferencia(),
                envio.getReferenciaId(),
                envio.getTemplateId(),
                NotificacaoBrevoService.nomeTemplate(envio.getTemplateId()),
                envio.isPermiteReenvio()
        );
    }
}
