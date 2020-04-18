package com.example.springboot10;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan(value = "com.example.springboot10.mapper")
public class Springboot10DataMybatisApplication {

    public static void main(String[] args) {
        SpringApplication.run(Springboot10DataMybatisApplication.class, args);
    }

}
