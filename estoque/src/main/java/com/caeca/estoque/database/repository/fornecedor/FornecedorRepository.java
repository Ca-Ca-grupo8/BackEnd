package com.caeca.estoque.database.repository.fornecedor;

import com.caeca.estoque.database.model.fornecedores.Fornecedor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FornecedorRepository extends JpaRepository<Fornecedor, Long> {
   Page<Fornecedor> findByIsAtivo(Boolean isAtivo, Pageable pageable);

   Page<Fornecedor> findByNomeContainingIgnoreCase(String nome, Pageable pageable);

   Page<Fornecedor> findByIsAtivoAndNomeContainingIgnoreCase(Boolean isAtivo, String nome, Pageable pageable);
}