package com.caeca.estoque.service.usuario;

import com.caeca.estoque.producer.interfaceProducer.NotificacaoProducer;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
@DisplayName("UsuarioNotificacaoService")
class UsuarioNotificacaoServiceTest {

    @Mock
    private NotificacaoProducer notificacaoProducer;

    @InjectMocks
    private UsuarioNotificacaoService usuarioNotificacaoService;

    @Test
    @DisplayName("deve publicar o e-mail de convite com o template e os parâmetros corretos")
    void devePublicarConviteComTemplateEParametrosCorretos() {
        usuarioNotificacaoService.notificacaoConviteUsuario(
                "joao@email.com", "João Silva", "http://localhost:5173/primeiro-acesso?token=abc");

        verify(notificacaoProducer).enviarInterno(
                "joao@email.com",
                "João Silva",
                UsuarioNotificacaoService.TEMPLATE_CONVITE_USUARIO,
                Map.of("link", "http://localhost:5173/primeiro-acesso?token=abc", "nome", "João Silva")
        );
    }
}
