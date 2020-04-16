package com.example.springboot.config;

import com.example.springboot.component.LoginHandlerInterceptor;
import com.example.springboot.component.MyLocaleResolver;
import org.springframework.boot.autoconfigure.web.servlet.WebMvcAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

// 添加自定义配置类，通过实现WebMvcConfigurer接口可以来扩展SpringMVC的功能
@Configuration
public class MyMvcConfig implements WebMvcConfigurer {

    //  添加一个视图解析设置 /url -> xxx(.html)
    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/myview").setViewName("success1");
        registry.addViewController("/index.html").setViewName("login");
        registry.addViewController("/main.html").setViewName("dashboard");
    }


    // 添加自定义的区域信息解析器(方法名必须为localeResolver)
    @Bean
    public LocaleResolver localeResolver(){
        return new MyLocaleResolver();
    }

    // 在WebMvcConfigurer中配置的组件会和SpringBoot的默认自动配置一起生效
    @Bean
    public WebMvcConfigurer webMvcConfigurer(){
        // 匿名类
        WebMvcConfigurer wc = new WebMvcConfigurer() {
            // 注册拦截器
            @Override
            public void addInterceptors(InterceptorRegistry registry) {
                // "/**"：拦截任意多层路径下的任意请求
                // 同时要排除对静态资源的拦截  "/asserts/**" ,"/webjars/**"
                registry.addInterceptor(new LoginHandlerInterceptor()).addPathPatterns("/**")
                        .excludePathPatterns("/index.html", "/", "/user/login", "/statics/asserts/**" ,"/webjars/**","/resources/**");
            }

        };
        return wc;
    }
}
