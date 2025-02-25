package com.example.amqp.config;

import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MyAmqpConfig {
    @Bean
    public MessageConverter messageConverter(){
        return new Jackson2JsonMessageConverter(); // 以JSON形式发送数据
    }
}
