package com.caeca.estoque.database.repository.material;

import com.caeca.estoque.database.model.material.Material;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MaterialRepository extends JpaRepository<Material, Integer> {
    boolean existsByNomeIgnoreCase(String nome);
}
