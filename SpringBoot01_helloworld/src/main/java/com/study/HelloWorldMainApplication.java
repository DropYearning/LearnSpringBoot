package com.study;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 标注一个SpringBoot主程序
 */
@SpringBootApplication
public class HelloWorldMainApplication {
    public static void main(String[] args) {
        // 启动Spring应用
        SpringApplication.run(HelloWorldMainApplication.class, args);
    }
}
