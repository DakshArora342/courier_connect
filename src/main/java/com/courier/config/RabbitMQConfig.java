package com.courier.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    public static final String MAIN_QUEUE = "courier.main.queue";
    public static final String WAIT_QUEUE = "courier.wait.queue";
    public static final String MAIN_EXCHANGE = "courier.exchange";
    public static final String WAIT_EXCHANGE = "courier.wait.exchange";

    @Bean
    Queue mainQueue() {
        return QueueBuilder.durable(MAIN_QUEUE).deadLetterExchange(WAIT_EXCHANGE).build();
    }

    @Bean
    Queue waitQueue() {
        return QueueBuilder.durable(WAIT_QUEUE).deadLetterExchange(MAIN_EXCHANGE).deadLetterRoutingKey(MAIN_QUEUE)
                .build();
    }

    @Bean
    DirectExchange mainExchange() {
        return new DirectExchange(MAIN_EXCHANGE);
    }

    @Bean
    DirectExchange waitExchange() {
        return new DirectExchange(WAIT_EXCHANGE);
    }

    @Bean
    Binding bindMain() {
        return BindingBuilder.bind(mainQueue()).to(mainExchange()).with(MAIN_QUEUE);
    }

    @Bean
    Binding bindWait() {
        return BindingBuilder.bind(waitQueue()).to(waitExchange()).with(WAIT_QUEUE);
    }

}
