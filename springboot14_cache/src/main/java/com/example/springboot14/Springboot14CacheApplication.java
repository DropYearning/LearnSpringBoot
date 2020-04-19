package com.example.springboot14;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@MapperScan("com.example.springboot14.mapper")
@EnableCaching
public class Springboot14CacheApplication {

    public static void main(String[] args) {
        SpringApplication.run(Springboot14CacheApplication.class, args);
    }

}
