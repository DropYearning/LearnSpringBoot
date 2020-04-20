package com.example.amqp;

import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableRabbit // 开启基于注解的RabbitMQ
public class Springboot16AmqpRabbitmqApplication {

    public static void main(String[] args) {
        SpringApplication.run(Springboot16AmqpRabbitmqApplication.class, args);
    }

}
