package com.caeca.estoque.service.usuario;

import com.caeca.estoque.producer.interfaceProducer.NotificacaoProducer;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class UsuarioNotificacaoService {

    public static final long TEMPLATE_CONVITE_USUARIO = 12L;

    private final NotificacaoProducer notificacaoProducer;

    public UsuarioNotificacaoService(NotificacaoProducer notificacaoProducer) {
        this.notificacaoProducer = notificacaoProducer;
    }

    public void notificacaoConviteUsuario(String email, String nome, String link) {
        notificacaoProducer.enviarMensagem(
                email,
                nome,
                TEMPLATE_CONVITE_USUARIO,
                Map.of("link", link, "nome", nome)
        );
    }
}
