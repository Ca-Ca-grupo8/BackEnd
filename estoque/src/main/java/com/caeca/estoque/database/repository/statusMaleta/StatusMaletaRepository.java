package com.caeca.estoque.database.repository.statusMaleta;

import com.caeca.estoque.database.model.statusMaleta.StatusMaleta;
import com.caeca.estoque.database.model.statusMaleta.StatusMaletaEnum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StatusMaletaRepository extends JpaRepository<StatusMaleta, Long> {

    Optional<StatusMaleta> findByNome(StatusMaletaEnum nome);
}
