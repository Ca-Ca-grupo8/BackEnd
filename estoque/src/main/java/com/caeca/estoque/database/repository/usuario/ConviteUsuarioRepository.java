package com.caeca.estoque.database.repository.usuario;

import com.caeca.estoque.database.model.usuario.ConviteUsuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ConviteUsuarioRepository extends JpaRepository<ConviteUsuario, Long> {

    Optional<ConviteUsuario> findByTokenHash(String tokenHash);

    List<ConviteUsuario> findByUsuarioIdAndUtilizadoFalse(Long usuarioId);
}
