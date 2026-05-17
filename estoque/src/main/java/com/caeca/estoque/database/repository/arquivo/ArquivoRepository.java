package com.caeca.estoque.database.repository.arquivo;

import com.caeca.estoque.database.model.arquivo.Arquivo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ArquivoRepository extends JpaRepository<Arquivo, Long> {
}
