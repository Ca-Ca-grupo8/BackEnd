package com.caeca.estoque.config.rabbitMq;

import org.springframework.amqp.support.converter.JacksonJsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.amqp.core.*;

@Configuration
public class RabbitMQConfig {
    public static final String QUEUE_NOTIFICACAO_EXTERNA = "caecaFilaExterna";
    public static final String QUEUE_NOTIFICACAO_EXTERNA_RETRY = "caecaFilaExterna.retry";
    public static final String QUEUE_NOTIFICACAO_EXTERNA_PARKING = "caecaFilaExterna.parking";
    public static final String QUEUE_NOTIFICACAO_INTERNA = "caecaFilaInterna";
    public static final String QUEUE_NOTIFICACAO_INTERNA_RETRY = "caecaFilaInterna.retry";
    public static final String QUEUE_NOTIFICACAO_INTERNA_PARKING = "caecaFilaInterna.parking";

    public static final String EXCHANGE_NOTIFICACAO = "caecaExchange";
    public static final String EXCHANGE_NOTIFICACAO_DLX = "caecaDlx";

    public static final String ROUTING_KEY_EXTERNA = "caecaKeyExterna";
    public static final String ROUTING_KEY_EXTERNA_RETRY = "caecaKeyExterna.retry";
    public static final String ROUTING_KEY_EXTERNA_PARKING = "caecaKeyExterna.parking";
    public static final String ROUTING_KEY_INTERNA = "caecaKeyInterna";
    public static final String ROUTING_KEY_INTERNA_RETRY = "caecaKeyInterna.retry";
    public static final String ROUTING_KEY_INTERNA_PARKING = "caecaKeyInterna.parking";

    @Bean
    public DirectExchange exchange() {
        return new DirectExchange(EXCHANGE_NOTIFICACAO);
    }

    @Bean
    public DirectExchange exchangeDlx() {
        return new DirectExchange(EXCHANGE_NOTIFICACAO_DLX);
    }

    // Filas principais: falha esgotada no consumer vai para a fila de retry via DLX.
    @Bean
    public Queue queueExterna() {
        return QueueBuilder.durable(QUEUE_NOTIFICACAO_EXTERNA)
                .deadLetterExchange(EXCHANGE_NOTIFICACAO_DLX)
                .deadLetterRoutingKey(ROUTING_KEY_EXTERNA_RETRY)
                .build();
    }

    @Bean
    public Queue queueInterna() {
        return QueueBuilder.durable(QUEUE_NOTIFICACAO_INTERNA)
                .deadLetterExchange(EXCHANGE_NOTIFICACAO_DLX)
                .deadLetterRoutingKey(ROUTING_KEY_INTERNA_RETRY)
                .build();
    }

    // Retry: sem consumer. Ao expirar o TTL, a mensagem volta para a exchange principal.
    @Bean
    public Queue queueExternaRetry(@Value("${app.notificacao.retry.ttl-ms:300000}") int ttlMs) {
        return QueueBuilder.durable(QUEUE_NOTIFICACAO_EXTERNA_RETRY)
                .ttl(ttlMs)
                .deadLetterExchange(EXCHANGE_NOTIFICACAO)
                .deadLetterRoutingKey(ROUTING_KEY_EXTERNA)
                .build();
    }

    @Bean
    public Queue queueInternaRetry(@Value("${app.notificacao.retry.ttl-ms:300000}") int ttlMs) {
        return QueueBuilder.durable(QUEUE_NOTIFICACAO_INTERNA_RETRY)
                .ttl(ttlMs)
                .deadLetterExchange(EXCHANGE_NOTIFICACAO)
                .deadLetterRoutingKey(ROUTING_KEY_INTERNA)
                .build();
    }

    // Parking: fila de descarte para análise manual, após esgotar as voltas.
    @Bean
    public Queue queueExternaParking() {
        return QueueBuilder.durable(QUEUE_NOTIFICACAO_EXTERNA_PARKING).build();
    }

    @Bean
    public Queue queueInternaParking() {
        return QueueBuilder.durable(QUEUE_NOTIFICACAO_INTERNA_PARKING).build();
    }

    @Bean
    public Binding bindingExterna(Queue queueExterna, DirectExchange exchange) {
        return BindingBuilder.bind(queueExterna).to(exchange).with(ROUTING_KEY_EXTERNA);
    }

    @Bean
    public Binding bindingInterna(Queue queueInterna, DirectExchange exchange) {
        return BindingBuilder.bind(queueInterna).to(exchange).with(ROUTING_KEY_INTERNA);
    }

    @Bean
    public Binding bindingExternaRetry(Queue queueExternaRetry, DirectExchange exchangeDlx) {
        return BindingBuilder.bind(queueExternaRetry).to(exchangeDlx).with(ROUTING_KEY_EXTERNA_RETRY);
    }

    @Bean
    public Binding bindingInternaRetry(Queue queueInternaRetry, DirectExchange exchangeDlx) {
        return BindingBuilder.bind(queueInternaRetry).to(exchangeDlx).with(ROUTING_KEY_INTERNA_RETRY);
    }

    @Bean
    public Binding bindingExternaParking(Queue queueExternaParking, DirectExchange exchangeDlx) {
        return BindingBuilder.bind(queueExternaParking).to(exchangeDlx).with(ROUTING_KEY_EXTERNA_PARKING);
    }

    @Bean
    public Binding bindingInternaParking(Queue queueInternaParking, DirectExchange exchangeDlx) {
        return BindingBuilder.bind(queueInternaParking).to(exchangeDlx).with(ROUTING_KEY_INTERNA_PARKING);
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new JacksonJsonMessageConverter();
    }
}
