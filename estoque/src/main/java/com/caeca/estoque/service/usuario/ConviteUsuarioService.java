package com.caeca.estoque.service.usuario;

import com.caeca.estoque.database.model.notificacaoEnvio.TipoReferencia;
import com.caeca.estoque.database.model.usuario.ConviteUsuario;
import com.caeca.estoque.database.model.usuario.StatusUsuario;
import com.caeca.estoque.database.model.usuario.Usuario;
import com.caeca.estoque.database.repository.usuario.ConviteUsuarioRepository;
import com.caeca.estoque.database.repository.usuario.UsuarioRepository;
import com.caeca.estoque.dto.usuario.ConviteUsuarioResponseDto;
import com.caeca.estoque.dto.usuario.UsuarioMapper;
import com.caeca.estoque.exception.BadRequestException;
import com.caeca.estoque.exception.ConflictException;
import com.caeca.estoque.exception.NotFoundException;
import com.caeca.estoque.service.notificacaoEnvio.NotificacaoEnvioService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class ConviteUsuarioService {

    private final ConviteUsuarioRepository conviteUsuarioRepository;
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final UsuarioNotificacaoService usuarioNotificacaoService;
    private final NotificacaoEnvioService notificacaoEnvioService;
    private final long expiracaoHoras;
    private final String frontendUrl;

    public ConviteUsuarioService(ConviteUsuarioRepository conviteUsuarioRepository,
                                 UsuarioRepository usuarioRepository,
                                 PasswordEncoder passwordEncoder,
                                 UsuarioNotificacaoService usuarioNotificacaoService,
                                 NotificacaoEnvioService notificacaoEnvioService,
                                 @Value("${app.convite.expiracao-horas:24}") long expiracaoHoras,
                                 @Value("${app.frontend.url:http://localhost:5173}") String frontendUrl) {
        this.conviteUsuarioRepository = conviteUsuarioRepository;
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.usuarioNotificacaoService = usuarioNotificacaoService;
        this.notificacaoEnvioService = notificacaoEnvioService;
        this.expiracaoHoras = expiracaoHoras;
        this.frontendUrl = frontendUrl;
    }

    @Transactional
    public String criarConvite(Usuario usuario) {
        if (usuario.getStatus() == null) {
            usuario.setStatus(StatusUsuario.PENDENTE);
        }

        Usuario usuarioSalvo = this.usuarioRepository.save(usuario);

        return gerarRegistroConvite(usuarioSalvo);
    }

    @Transactional
    public ConviteUsuarioResponseDto convidar(Usuario usuario) {
        if (this.usuarioRepository.findByEmail(usuario.getEmail()).isPresent()) {
            throw new ConflictException("E-mail já cadastrado");
        }

        String token = criarConvite(usuario);
        this.agendarEnvio(usuario, token);

        return UsuarioMapper.toResponse(usuario);
    }

    @Transactional
    public void reenviarConvite(Long usuarioId) {
        Usuario usuario = this.usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new NotFoundException("Usuário com ID " + usuarioId + " não encontrado"));

        List<ConviteUsuario> pendentes = this.conviteUsuarioRepository.findByUsuarioIdAndUtilizadoFalse(usuarioId);
        pendentes.forEach(convite -> convite.setUtilizado(true));
        this.conviteUsuarioRepository.saveAll(pendentes);

        String token = gerarRegistroConvite(usuario);
        this.agendarEnvio(usuario, token);
    }

    @Transactional
    public void reenviarConviteExpirado(String token) {
        ConviteUsuario convite = this.conviteUsuarioRepository.findByTokenHash(HasherToken.gerarHash(token))
                .orElseThrow(() -> new NotFoundException("Convite não encontrado ou inválido"));

        if (convite.isUtilizado()) {
            throw new BadRequestException("Convite já utilizado");
        }

        if (!convite.isExpirado()) {
            throw new BadRequestException("Convite ainda é válido");
        }

        Usuario usuario = convite.getUsuario();

        if (StatusUsuario.ATIVO.equals(usuario.getStatus())) {
            throw new BadRequestException("Usuário já está ativo");
        }

        convite.setUtilizado(true);
        this.conviteUsuarioRepository.save(convite);

        String novoToken = gerarRegistroConvite(usuario);
        this.agendarEnvio(usuario, novoToken);
    }

    @Transactional(readOnly = true)
    public ConviteUsuario validarToken(String token) {
        String tokenHash = HasherToken.gerarHash(token);

        ConviteUsuario convite = this.conviteUsuarioRepository.findByTokenHash(tokenHash)
                .orElseThrow(() -> new NotFoundException("Convite não encontrado ou inválido"));

        if (convite.isUtilizado()) {
            throw new BadRequestException("Convite já utilizado");
        }

        if (convite.isExpirado()) {
            throw new BadRequestException("Convite expirado");
        }

        return convite;
    }

    @Transactional
    public void definirSenha(String token, String novaSenha, String confirmarSenha) {
        if (!novaSenha.equals(confirmarSenha)) {
            throw new BadRequestException("As senhas não conferem");
        }

        definirSenha(token, novaSenha);
    }

    @Transactional
    public void definirSenha(String token, String novaSenha) {
        ConviteUsuario convite = validarToken(token);
        Usuario usuario = convite.getUsuario();

        usuario.setSenha(this.passwordEncoder.encode(novaSenha));
        usuario.setStatus(StatusUsuario.ATIVO);
        this.usuarioRepository.save(usuario);

        convite.setUtilizado(true);
        this.conviteUsuarioRepository.save(convite);
    }

    private String gerarRegistroConvite(Usuario usuario) {
        String token = UUID.randomUUID().toString();

        ConviteUsuario convite = new ConviteUsuario(
                usuario,
                HasherToken.gerarHash(token),
                LocalDateTime.now().plusHours(expiracaoHoras),
                false
        );

        this.conviteUsuarioRepository.save(convite);

        return token;
    }

    private void agendarEnvio(Usuario usuario, String token) {
        if (TransactionSynchronizationManager.isSynchronizationActive()) {
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                @Override
                public void afterCommit() {
                    enviarConvite(usuario, token);
                }
            });
            return;
        }

        enviarConvite(usuario, token);
    }

    private void enviarConvite(Usuario usuario, String token) {
        String link = montarLink(token);

        this.notificacaoEnvioService.registrarEnvio(
                TipoReferencia.USUARIO,
                usuario.getId(),
                UsuarioNotificacaoService.TEMPLATE_CONVITE_USUARIO,
                true
        );

        this.usuarioNotificacaoService.notificacaoConviteUsuario(usuario.getEmail(), usuario.getNome(), link);
    }

    private String montarLink(String token) {
        return this.frontendUrl + "/primeiro-acesso?token=" + token;
    }
}
