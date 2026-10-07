package com.caeca.estoque.config.rabbitMq;

import org.springframework.amqp.support.converter.JacksonJsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.amqp.core.*;

@Configuration
public class RabbitMQConfig {
    public static final String QUEUE_NOTIFICACAO_BREVO = "caecaFila";
    public static final String EXCHANGE_NOTIFICACAO = "caecaExchange";
    public static final String ROUTING_KEY_BREVO = "caecaKey";

    @Bean
    public Queue queue() {
        return new Queue(QUEUE_NOTIFICACAO_BREVO, true);
    }

    @Bean
    public DirectExchange exchange() {
        return new DirectExchange(EXCHANGE_NOTIFICACAO);
    }

    @Bean
    public Binding binding(Queue queue, DirectExchange exchange) {
        return BindingBuilder.bind(queue).to(exchange).with(ROUTING_KEY_BREVO);
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new JacksonJsonMessageConverter();
    }
}
