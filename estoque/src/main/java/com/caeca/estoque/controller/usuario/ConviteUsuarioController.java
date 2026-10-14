package com.caeca.estoque.controller.usuario;

import com.caeca.estoque.database.model.usuario.ConviteUsuario;
import com.caeca.estoque.database.model.usuario.Usuario;
import com.caeca.estoque.dto.usuario.ConviteReenvioRequestDto;
import com.caeca.estoque.dto.usuario.ConviteSenhaRequestDto;
import com.caeca.estoque.dto.usuario.ConviteUsuarioRequestDto;
import com.caeca.estoque.dto.usuario.ConviteUsuarioResponseDto;
import com.caeca.estoque.dto.usuario.ConviteValidacaoResponseDto;
import com.caeca.estoque.dto.usuario.UsuarioMapper;
import com.caeca.estoque.service.usuario.ConviteUsuarioService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/usuarios/convites")
@Tag(name = "Convites de Usuário", description = "Fluxo de primeiro acesso / convite de usuário")
public class ConviteUsuarioController {

    private final ConviteUsuarioService conviteUsuarioService;

    public ConviteUsuarioController(ConviteUsuarioService conviteUsuarioService) {
        this.conviteUsuarioService = conviteUsuarioService;
    }

    @PostMapping
    @SecurityRequirement(name = "Bearer")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<ConviteUsuarioResponseDto> criar(@RequestBody @Valid ConviteUsuarioRequestDto dto) {
        Usuario usuario = UsuarioMapper.toEntity(dto);

        ConviteUsuarioResponseDto response = this.conviteUsuarioService.convidar(usuario);

        return ResponseEntity.status(201).body(response);
    }

    @PostMapping("/{usuarioId}/reenviar")
    @SecurityRequirement(name = "Bearer")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<Void> reenviar(@PathVariable Long usuarioId) {
        this.conviteUsuarioService.reenviarConvite(usuarioId);

        return ResponseEntity.noContent().build();
    }

    @PostMapping("/reenviar")
    public ResponseEntity<Void> reenviarExpirado(@RequestBody @Valid ConviteReenvioRequestDto dto) {
        this.conviteUsuarioService.reenviarConviteExpirado(dto.getToken());

        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{token}")
    public ResponseEntity<ConviteValidacaoResponseDto> validar(@PathVariable String token) {
        ConviteUsuario convite = this.conviteUsuarioService.validarToken(token);

        return ResponseEntity.ok(UsuarioMapper.toValidacao(convite.getUsuario()));
    }

    @PostMapping("/{token}/senha")
    public ResponseEntity<Void> definirSenha(@PathVariable String token,
                                             @RequestBody @Valid ConviteSenhaRequestDto dto) {
        this.conviteUsuarioService.definirSenha(token, dto.getNovaSenha(), dto.getConfirmarSenha());

        return ResponseEntity.noContent().build();
    }
}
