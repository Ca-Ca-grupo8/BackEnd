package com.caeca.estoque.database.model.notificacaoEnvio;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
public class NotificacaoEnvio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idNotificacaoEnvio;

    @Column(name = "tipo_referencia", nullable = false)
    @Enumerated(EnumType.STRING)
    private TipoReferencia tipoReferencia;

    @Column(name = "referencia_id", nullable = false)
    private Long referenciaId;

    @Column(name = "template_id", nullable = false)
    private Long templateId;

    @Column(name = "permite_reenvio", nullable = false)
    private boolean permiteReenvio;

    @CreationTimestamp
    @Column(name = "data_envio", nullable = false, updatable = false)
    private LocalDateTime dataEnvio;

    public NotificacaoEnvio() {
    }

    public NotificacaoEnvio(TipoReferencia tipoReferencia, Long referenciaId, Long templateId, boolean permiteReenvio) {
        this.tipoReferencia = tipoReferencia;
        this.referenciaId = referenciaId;
        this.templateId = templateId;
        this.permiteReenvio = permiteReenvio;
    }

    public Long getIdNotificacaoEnvio() {
        return idNotificacaoEnvio;
    }

    public void setIdNotificacaoEnvio(Long idNotificacaoEnvio) {
        this.idNotificacaoEnvio = idNotificacaoEnvio;
    }

    public Long getreferenciaId() {
        return referenciaId;
    }

    public void setreferenciaId(Long referenciaId) {
        this.referenciaId = referenciaId;
    }

    public Long getTemplateId() {
        return templateId;
    }

    public void setTemplateId(Long templateId) {
        this.templateId = templateId;
    }

    public LocalDateTime getDataEnvio() {
        return dataEnvio;
    }

    public void setDataEnvio(LocalDateTime dataEnvio) {
        this.dataEnvio = dataEnvio;
    }

    public TipoReferencia getTipoReferencia() {
        return tipoReferencia;
    }

    public void setTipoReferencia(TipoReferencia tipoReferencia) {
        this.tipoReferencia = tipoReferencia;
    }

    public Long getReferenciaId() {
        return referenciaId;
    }

    public void setReferenciaId(Long referenciaId) {
        this.referenciaId = referenciaId;
    }

    public boolean isPermiteReenvio() {
        return permiteReenvio;
    }

    public void setPermiteReenvio(boolean permiteReenvio) {
        this.permiteReenvio = permiteReenvio;
    }
}