package com.caeca.estoque.database.model.usuario;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "convite_usuario")
public class ConviteUsuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @Column(name = "token_hash", nullable = false, unique = true, length = 64)
    private String tokenHash;

    @Column(name = "expira_em", nullable = false)
    private LocalDateTime expiresAt;

    @Column(name = "utilizado", nullable = false)
    private boolean utilizado;

    @CreationTimestamp
    @Column(name = "criado_em", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public ConviteUsuario() {
    }

    public ConviteUsuario(Usuario usuario, String tokenHash, LocalDateTime expiresAt, boolean utilizado) {
        this.usuario = usuario;
        this.tokenHash = tokenHash;
        this.expiresAt = expiresAt;
        this.utilizado = utilizado;
    }

    public boolean isExpirado() {
        return expiresAt != null && expiresAt.isBefore(LocalDateTime.now());
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public String getTokenHash() {
        return tokenHash;
    }

    public void setTokenHash(String tokenHash) {
        this.tokenHash = tokenHash;
    }

    public LocalDateTime getExpiresAt() {
        return expiresAt;
    }

    public void setExpiresAt(LocalDateTime expiresAt) {
        this.expiresAt = expiresAt;
    }

    public boolean isUtilizado() {
        return utilizado;
    }

    public void setUtilizado(boolean utilizado) {
        this.utilizado = utilizado;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
