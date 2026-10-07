package com.caeca.estoque.database.model.statusMaleta;

import jakarta.persistence.*;
import lombok.Getter;

@Entity
@Getter
@Table(uniqueConstraints = @UniqueConstraint(name = "uk_status_maleta_nome", columnNames = "nome"))
public class StatusMaleta {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Enumerated(EnumType.STRING)
    private StatusMaletaEnum nome;

    public StatusMaleta(Long id, StatusMaletaEnum nome) {
        this.id = id;
        this.nome = nome;
    }

    public StatusMaleta() {
    }

    public Long getId() {
        return id;
    }

    public StatusMaletaEnum getNome() {
        return nome;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setNome(StatusMaletaEnum nome) {
        this.nome = nome;
    }
}
