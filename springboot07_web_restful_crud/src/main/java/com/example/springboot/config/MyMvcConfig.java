package com.example.springboot.config;

import com.example.springboot.component.MyLocaleResolver;
import org.springframework.boot.autoconfigure.web.servlet.WebMvcAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

// 添加自定义配置类，通过实现WebMvcConfigurer接口可以来扩展SpringMVC的功能
@Configuration
public class MyMvcConfig implements WebMvcConfigurer {

    //  添加一个视图解析设置 /test -> success.html
    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/myview").setViewName("success1");
    }


    // 添加自定义的区域信息解析器(方法名必须为localeResolver)
    @Bean
    public LocaleResolver localeResolver(){
        return new MyLocaleResolver();
    }
}
