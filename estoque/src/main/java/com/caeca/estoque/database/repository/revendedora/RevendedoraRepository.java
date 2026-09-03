package com.caeca.estoque.database.repository.revendedora;

import com.caeca.estoque.database.model.revendedora.Revendedora;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RevendedoraRepository extends JpaRepository<Revendedora, Long> {
    boolean existsByEmail(String email);

    boolean existsByTelefone(String telefone);

    boolean existsByEnderecoId(Long enderecoId);

    Optional<Revendedora> findByEmail(String email);

    Optional<Revendedora> findByTelefone(String telefone);

    Optional<Revendedora> findByEnderecoId(Long enderecoId);

    boolean existsByEmailAndIdNot(String email, Long id);

    boolean existsByTelefoneAndIdNot(String telefone, Long id);

    boolean existsByEnderecoIdAndIdNot(Long enderecoId, Long id);
}
