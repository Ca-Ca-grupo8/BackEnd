package com.caeca.estoque.controller.usuario;

import com.caeca.estoque.database.model.usuario.ConviteUsuario;
import com.caeca.estoque.database.model.usuario.PerfilUsuario;
import com.caeca.estoque.database.model.usuario.StatusUsuario;
import com.caeca.estoque.database.model.usuario.Usuario;
import com.caeca.estoque.dto.usuario.ConviteReenvioRequestDto;
import com.caeca.estoque.dto.usuario.ConviteSenhaRequestDto;
import com.caeca.estoque.dto.usuario.ConviteUsuarioRequestDto;
import com.caeca.estoque.dto.usuario.ConviteUsuarioResponseDto;
import com.caeca.estoque.exception.BadRequestException;
import com.caeca.estoque.exception.ConflictException;
import com.caeca.estoque.exception.NotFoundException;
import com.caeca.estoque.handler.GlobalExceptionHandler;
import com.caeca.estoque.service.usuario.ConviteUsuarioService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest(ConviteUsuarioController.class)
@org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc(addFilters = false)
@Import(GlobalExceptionHandler.class)
class ConviteUsuarioControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ConviteUsuarioService conviteUsuarioService;

    private ObjectMapper objectMapper;
    private Usuario usuario;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();

        usuario = new Usuario();
        usuario.setId(1L);
        usuario.setNome("João Silva");
        usuario.setEmail("joao@email.com");
        usuario.setPerfil(PerfilUsuario.ROLE_USER);
        usuario.setStatus(StatusUsuario.PENDENTE);
    }

    @Nested
    @DisplayName("criar")
    class Criar {

        @Test
        @DisplayName("Deve retornar 201 e os dados do usuário convidado")
        void deveRetornar201EUsuarioConvidado() throws Exception {
            ConviteUsuarioRequestDto request = new ConviteUsuarioRequestDto();
            request.setNome("João Silva");
            request.setEmail("joao@email.com");
            request.setPerfilUsuario(PerfilUsuario.ROLE_USER);

            ConviteUsuarioResponseDto response = new ConviteUsuarioResponseDto();
            response.setId(1L);
            response.setNome("João Silva");
            response.setEmail("joao@email.com");
            response.setPerfil(PerfilUsuario.ROLE_USER);
            response.setStatus(StatusUsuario.PENDENTE);

            when(conviteUsuarioService.convidar(any(Usuario.class))).thenReturn(response);

            mockMvc.perform(post("/usuarios/convites")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.id").value(1))
                    .andExpect(jsonPath("$.email").value("joao@email.com"))
                    .andExpect(jsonPath("$.status").value("PENDENTE"));

            verify(conviteUsuarioService, times(1)).convidar(any(Usuario.class));
        }

        @Test
        @DisplayName("Deve retornar 400 quando o corpo for inválido")
        void deveRetornar400QuandoCorpoInvalido() throws Exception {
            ConviteUsuarioRequestDto request = new ConviteUsuarioRequestDto();

            mockMvc.perform(post("/usuarios/convites")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest());

            verify(conviteUsuarioService, never()).convidar(any(Usuario.class));
        }

        @Test
        @DisplayName("Deve retornar 409 quando o e-mail já estiver cadastrado")
        void deveRetornar409QuandoEmailJaCadastrado() throws Exception {
            ConviteUsuarioRequestDto request = new ConviteUsuarioRequestDto();
            request.setNome("João Silva");
            request.setEmail("joao@email.com");
            request.setPerfilUsuario(PerfilUsuario.ROLE_USER);

            when(conviteUsuarioService.convidar(any(Usuario.class)))
                    .thenThrow(new ConflictException("E-mail já cadastrado"));

            mockMvc.perform(post("/usuarios/convites")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isConflict());
        }
    }

    @Nested
    @DisplayName("reenviar")
    class Reenviar {

        @Test
        @DisplayName("Deve retornar 204 ao reenviar o convite")
        void deveRetornar204AoReenviar() throws Exception {
            doNothing().when(conviteUsuarioService).reenviarConvite(1L);

            mockMvc.perform(post("/usuarios/convites/{usuarioId}/reenviar", 1L))
                    .andExpect(status().isNoContent());

            verify(conviteUsuarioService, times(1)).reenviarConvite(1L);
        }

        @Test
        @DisplayName("Deve retornar 404 quando o usuário não existir")
        void deveRetornar404QuandoUsuarioNaoExistir() throws Exception {
            doThrow(new NotFoundException("Usuário não encontrado"))
                    .when(conviteUsuarioService).reenviarConvite(99L);

            mockMvc.perform(post("/usuarios/convites/{usuarioId}/reenviar", 99L))
                    .andExpect(status().isNotFound());
        }
    }

    @Nested
    @DisplayName("reenviarExpirado")
    class ReenviarExpirado {

        @Test
        @DisplayName("Deve retornar 204 ao reenviar um convite expirado")
        void deveRetornar204() throws Exception {
            ConviteReenvioRequestDto request = new ConviteReenvioRequestDto();
            request.setToken("token-expirado");

            doNothing().when(conviteUsuarioService).reenviarConviteExpirado("token-expirado");

            mockMvc.perform(post("/usuarios/convites/reenviar")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isNoContent());

            verify(conviteUsuarioService, times(1)).reenviarConviteExpirado("token-expirado");
        }

        @Test
        @DisplayName("Deve retornar 400 quando o token estiver em branco")
        void deveRetornar400QuandoTokenEmBranco() throws Exception {
            ConviteReenvioRequestDto request = new ConviteReenvioRequestDto();

            mockMvc.perform(post("/usuarios/convites/reenviar")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest());

            verify(conviteUsuarioService, never()).reenviarConviteExpirado(any());
        }

        @Test
        @DisplayName("Deve retornar 404 quando o convite não existir")
        void deveRetornar404() throws Exception {
            ConviteReenvioRequestDto request = new ConviteReenvioRequestDto();
            request.setToken("token-inexistente");

            doThrow(new NotFoundException("Convite não encontrado ou inválido"))
                    .when(conviteUsuarioService).reenviarConviteExpirado("token-inexistente");

            mockMvc.perform(post("/usuarios/convites/reenviar")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isNotFound());
        }

        @Test
        @DisplayName("Deve retornar 400 quando o convite ainda for válido")
        void deveRetornar400QuandoAindaValido() throws Exception {
            ConviteReenvioRequestDto request = new ConviteReenvioRequestDto();
            request.setToken("token-valido");

            doThrow(new BadRequestException("Convite ainda é válido"))
                    .when(conviteUsuarioService).reenviarConviteExpirado("token-valido");

            mockMvc.perform(post("/usuarios/convites/reenviar")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest());
        }
    }

    @Nested
    @DisplayName("validar")
    class Validar {

        @Test
        @DisplayName("Deve retornar 200 com nome e e-mail do convite válido")
        void deveRetornar200ComDadosDoConvite() throws Exception {
            String token = UUID.randomUUID().toString();
            ConviteUsuario convite = new ConviteUsuario(
                    usuario, "hash", LocalDateTime.now().plusHours(1), false);

            when(conviteUsuarioService.validarToken(token)).thenReturn(convite);

            mockMvc.perform(get("/usuarios/convites/{token}", token)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.nome").value("João Silva"))
                    .andExpect(jsonPath("$.email").value("joao@email.com"));

            verify(conviteUsuarioService, times(1)).validarToken(token);
        }

        @Test
        @DisplayName("Deve retornar 404 quando o token não existir")
        void deveRetornar404QuandoTokenNaoExistir() throws Exception {
            String token = UUID.randomUUID().toString();
            when(conviteUsuarioService.validarToken(token))
                    .thenThrow(new NotFoundException("Convite não encontrado ou inválido"));

            mockMvc.perform(get("/usuarios/convites/{token}", token)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isNotFound());
        }

        @Test
        @DisplayName("Deve retornar 400 quando o convite estiver expirado")
        void deveRetornar400QuandoConviteExpirado() throws Exception {
            String token = UUID.randomUUID().toString();
            when(conviteUsuarioService.validarToken(token))
                    .thenThrow(new BadRequestException("Convite expirado"));

            mockMvc.perform(get("/usuarios/convites/{token}", token)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest());
        }
    }

    @Nested
    @DisplayName("definirSenha")
    class DefinirSenha {

        @Test
        @DisplayName("Deve retornar 204 ao definir a senha")
        void deveRetornar204AoDefinirSenha() throws Exception {
            String token = UUID.randomUUID().toString();
            ConviteSenhaRequestDto request = new ConviteSenhaRequestDto();
            request.setNovaSenha("minhaSenha123");
            request.setConfirmarSenha("minhaSenha123");

            doNothing().when(conviteUsuarioService).definirSenha(eq(token), eq("minhaSenha123"), eq("minhaSenha123"));

            mockMvc.perform(post("/usuarios/convites/{token}/senha", token)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isNoContent());

            verify(conviteUsuarioService, times(1)).definirSenha(token, "minhaSenha123", "minhaSenha123");
        }

        @Test
        @DisplayName("Deve retornar 400 quando as senhas não conferirem")
        void deveRetornar400QuandoSenhasDiferentes() throws Exception {
            String token = UUID.randomUUID().toString();
            ConviteSenhaRequestDto request = new ConviteSenhaRequestDto();
            request.setNovaSenha("minhaSenha123");
            request.setConfirmarSenha("outraSenha456");

            doThrow(new BadRequestException("As senhas não conferem"))
                    .when(conviteUsuarioService).definirSenha(eq(token), any(), any());

            mockMvc.perform(post("/usuarios/convites/{token}/senha", token)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("Deve retornar 400 quando a nova senha for muito curta")
        void deveRetornar400QuandoSenhaCurta() throws Exception {
            String token = UUID.randomUUID().toString();
            ConviteSenhaRequestDto request = new ConviteSenhaRequestDto();
            request.setNovaSenha("123");
            request.setConfirmarSenha("123");

            mockMvc.perform(post("/usuarios/convites/{token}/senha", token)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest());

            verify(conviteUsuarioService, never()).definirSenha(any(), any(), any());
        }
    }
}
