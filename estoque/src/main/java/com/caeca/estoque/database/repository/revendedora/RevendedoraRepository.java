package com.caeca.estoque.database.repository.revendedora;

import com.caeca.estoque.database.model.revendedora.Revendedora;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RevendedoraRepository extends JpaRepository<Revendedora, Integer> {
    boolean existsByEmail(String email);

    boolean existsByTelefone(String telefone);

    boolean existsByEnderecoId(Integer enderecoId);
}
