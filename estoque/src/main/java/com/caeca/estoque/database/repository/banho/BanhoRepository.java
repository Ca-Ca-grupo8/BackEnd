package com.caeca.estoque.database.repository.banho;

import com.caeca.estoque.database.model.banho.Banho;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BanhoRepository extends JpaRepository<Banho, Integer> {
    boolean existsByNomeIgnoreCase(String nome);
}
