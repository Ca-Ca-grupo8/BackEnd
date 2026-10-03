package com.caeca.estoque.database.repository.notificacaoEnvio;

import com.caeca.estoque.database.model.notificacaoEnvio.NotificacaoEnvio;
import com.caeca.estoque.database.model.notificacaoEnvio.TipoReferencia;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificacaoEnvioRepository extends JpaRepository<NotificacaoEnvio, Long> {
    boolean existsByTipoReferenciaAndReferenciaIdAndTemplateIdAndPermiteReenvioFalse(TipoReferencia tipoReferencia, long referenciaId, long templateId);
}