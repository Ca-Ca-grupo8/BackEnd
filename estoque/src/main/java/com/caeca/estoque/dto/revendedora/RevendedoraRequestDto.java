package com.caeca.estoque.dto.revendedora;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public class RevendedoraRequestDto {
    @Schema(example = "Ana Beatriz", description = "Nome completo da revendedora")
    @NotBlank
    private String nome;

    @Schema(example = "ana@email.com", description = "Email da revendedora")
    @NotBlank
    @Email
    private String email;

    @Schema(example = "11123456789", description = "Telefone da revendedora [DDD+Número]")
    @NotBlank
    @Size(min = 11, max = 11)
    private String telefone;

    @Schema(example = "1", description = "Id do endereço da revendedora")
    @NotNull
    private Long enderecoId;

    public RevendedoraRequestDto() {
    }

    public RevendedoraRequestDto(String nome, String email, String telefone, Long enderecoId) {
        this.nome = nome;
        this.email = email;
        this.telefone = telefone;
        this.enderecoId = enderecoId;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    public Long getEnderecoId() {
        return enderecoId;
    }

    public void setEnderecoId(Long enderecoId) {
        this.enderecoId = enderecoId;
    }
}
