package com.caeca.estoque.dto.usuario;

import com.caeca.estoque.database.model.usuario.StatusUsuario;
import com.caeca.estoque.database.model.usuario.Usuario;

public class UsuarioMapper {

    public static Usuario of(UsuarioCriacaoDto usuarioCriacaoDto) {
        Usuario usuario = new Usuario();

        usuario.setEmail(usuarioCriacaoDto.getEmail());
        usuario.setNome(usuarioCriacaoDto.getNome());
        usuario.setSenha(usuarioCriacaoDto.getSenha());
        usuario.setPerfil(usuarioCriacaoDto.getPerfilUsuario());

        return usuario;
    }

    public static Usuario of(UsuarioLoginDto usuarioLoginDto) {
        Usuario usuario = new Usuario();

        usuario.setEmail(usuarioLoginDto.getEmail());
        usuario.setSenha(usuarioLoginDto.getSenha());

        return usuario;
    }

    public static UsuarioTokenDto of(Usuario usuario, String token) {
        UsuarioTokenDto usuarioTokenDto = new UsuarioTokenDto();

        usuarioTokenDto.setUserId(usuario.getId());
        usuarioTokenDto.setEmail(usuario.getEmail());
        usuarioTokenDto.setNome(usuario.getNome());
        usuarioTokenDto.setToken(token);
        usuarioTokenDto.setRole(usuario.getPerfil());

        return usuarioTokenDto;
    }

    /**
     * Mapeia para o DTO de resposta do login — sem o token.
     *
     * <p>O token não pertence ao body: ele é enviado como cookie HttpOnly
     * via {@code Set-Cookie}. Este DTO carrega apenas os dados necessários
     * para o frontend identificar o usuário na sessão.</p>
     */
    public static UsuarioSessaoDto ofSessao(UsuarioTokenDto tokenDto) {
        UsuarioSessaoDto dto = new UsuarioSessaoDto();

        dto.setUserId(tokenDto.getUserId());
        dto.setEmail(tokenDto.getEmail());
        dto.setNome(tokenDto.getNome());
        dto.setRole(tokenDto.getRole());

        return dto;
    }

    public static UsuarioListarDto of(Usuario usuario) {
        UsuarioListarDto usuarioListarDto = new UsuarioListarDto();

        usuarioListarDto.setId(usuario.getId());
        usuarioListarDto.setEmail(usuario.getEmail());
        usuarioListarDto.setNome(usuario.getNome());
        usuarioListarDto.setRole(usuario.getPerfil());

        return usuarioListarDto;
    }

    public static Usuario toEntity(ConviteUsuarioRequestDto conviteUsuarioRequestDto) {
        Usuario usuario = new Usuario();

        usuario.setNome(conviteUsuarioRequestDto.getNome());
        usuario.setEmail(conviteUsuarioRequestDto.getEmail());
        usuario.setPerfil(conviteUsuarioRequestDto.getPerfilUsuario());
        usuario.setStatus(StatusUsuario.PENDENTE);
        usuario.setSenha(null);

        return usuario;
    }

    public static ConviteUsuarioResponseDto toResponse(Usuario usuario) {
        ConviteUsuarioResponseDto dto = new ConviteUsuarioResponseDto();

        dto.setId(usuario.getId());
        dto.setNome(usuario.getNome());
        dto.setEmail(usuario.getEmail());
        dto.setPerfil(usuario.getPerfil());
        dto.setStatus(usuario.getStatus());

        return dto;
    }

    public static ConviteValidacaoResponseDto toValidacao(Usuario usuario) {
        return new ConviteValidacaoResponseDto(usuario);
    }
}
