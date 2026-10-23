package com.caeca.estoque.service.usuario;

import java.util.List;

import com.caeca.estoque.config.GerenciadorTokenJwt;
import com.caeca.estoque.database.repository.usuario.UsuarioRepository;
import com.caeca.estoque.database.model.usuario.Usuario;
import com.caeca.estoque.dto.usuario.UsuarioListarDto;
import com.caeca.estoque.dto.usuario.UsuarioMapper;
import com.caeca.estoque.dto.usuario.UsuarioTokenDto;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class UsuarioService {

    private final PasswordEncoder passwordEncoder;
    private final UsuarioRepository usuarioRepository;
    private final GerenciadorTokenJwt gerenciadorTokenJwt;
    private final AuthenticationManager authenticationManager;

    public UsuarioService(PasswordEncoder passwordEncoder,
                          UsuarioRepository usuarioRepository,
                          GerenciadorTokenJwt gerenciadorTokenJwt,
                          AuthenticationManager authenticationManager) {
        this.passwordEncoder = passwordEncoder;
        this.usuarioRepository = usuarioRepository;
        this.gerenciadorTokenJwt = gerenciadorTokenJwt;
        this.authenticationManager = authenticationManager;
    }

    public void criar(Usuario novoUsuario) {


        String senhaCriptografada = passwordEncoder.encode(novoUsuario.getSenha());
        novoUsuario.setSenha(senhaCriptografada);

        this.usuarioRepository.save(novoUsuario);
    }

    public UsuarioTokenDto autenticar(Usuario usuario) {

        final UsernamePasswordAuthenticationToken credentials = new UsernamePasswordAuthenticationToken(
                usuario.getEmail(), usuario.getSenha());

        final Authentication authentication = this.authenticationManager.authenticate(credentials);

        Usuario usuarioAutenticado =
                usuarioRepository.findByEmail(usuario.getEmail())
                        .orElseThrow(
                                () -> new ResponseStatusException(404, "Email do usuário não cadastrado", null)
                        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        final String token = gerenciadorTokenJwt.generateToken(authentication);

        return UsuarioMapper.of(usuarioAutenticado, token);
    }

    public List<UsuarioListarDto> listarTodos() {

        List<Usuario> usuariosEncontrados = usuarioRepository.findAll();
        return usuariosEncontrados.stream().map(UsuarioMapper::of).toList();

    }

    public Usuario getUsuarioLogado() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();

        return usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(404, "Usuário logado não encontrado", null));
    }

    public Usuario buscarPorId(Long id) {
        return this.usuarioRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(404, "Usuário não encontrado", null));
    }

}
