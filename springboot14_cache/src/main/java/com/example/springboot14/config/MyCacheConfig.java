package com.example.springboot14.config;

import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.lang.reflect.Method;
import java.util.Arrays;

@Configuration
public class MyCacheConfig {

    @Bean("myKeyGenerator")
    public KeyGenerator keyGenerator(){
        return new KeyGenerator(){ //匿名内部类

            @Override
            public Object generate(Object o, Method method, Object... objects) {
                System.out.println("myKeyGenerator被调用了");
                return method.getName()+"["+ Arrays.asList(objects).toString() +"]";
            }
        };
    }
}
