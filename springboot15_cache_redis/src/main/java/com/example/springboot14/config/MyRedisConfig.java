package com.example.springboot14.config;

import com.example.springboot14.bean.Department;
import com.example.springboot14.bean.Employee;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;

import java.net.UnknownHostException;
import java.time.Duration;

@Configuration
public class MyRedisConfig {
    //@Bean // 负责Employee序列化JSON的RedisTemplate
    //public RedisTemplate<Object, Employee> empRedisTemplate(RedisConnectionFactory redisConnectionFactory) throws UnknownHostException {
    //    RedisTemplate<Object, Employee> template = new RedisTemplate<Object, Employee>();
    //    template.setConnectionFactory(redisConnectionFactory);
    //    Jackson2JsonRedisSerializer<Employee> ser = new Jackson2JsonRedisSerializer<Employee>(Employee.class);
    //    template.setDefaultSerializer(ser); // 使用Jackson2JsonRedisSerializer作为序列化器
    //    return template;
    //}
    //
    //@Bean// 负责Department序列化JSON的RedisTemplate
    //public RedisTemplate<Object, Department> deptRedisTemplate(
    //        RedisConnectionFactory redisConnectionFactory)
    //        throws UnknownHostException {
    //    RedisTemplate<Object, Department> template = new RedisTemplate<Object, Department>();
    //    template.setConnectionFactory(redisConnectionFactory);
    //    Jackson2JsonRedisSerializer<Department> ser = new Jackson2JsonRedisSerializer<Department>(Department.class);
    //    template.setDefaultSerializer(ser);
    //    return template;
    //}
    //
    //// 自定义CacheManager来对缓存进行设置
    //// 负责Employee缓存的CacheManager
    //@Bean
    //@Primary // 必须有一个默认使用的CacheManager否则会报错
    //public CacheManager employeeCacheManager(RedisTemplate<Object, Employee> empRedisTemplate) {
    //    RedisCacheConfiguration defaultCacheConfiguration =
    //            RedisCacheConfiguration
    //                    .defaultCacheConfig()
    //                    // 设置key为String
    //                    .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(empRedisTemplate.getStringSerializer()))
    //                    // 设置value 为自动转Json的Object
    //                    .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(empRedisTemplate.getValueSerializer()))
    //                    // 不缓存null
    //                    .disableCachingNullValues()
    //                    // 缓存数据保存1小时
    //                    .entryTtl(Duration.ofHours(1));
    //    RedisCacheManager redisCacheManager =
    //            RedisCacheManager.RedisCacheManagerBuilder
    //                    // Redis 连接工厂
    //                    .fromConnectionFactory(empRedisTemplate.getConnectionFactory())
    //                    // 缓存配置
    //                    .cacheDefaults(defaultCacheConfiguration)
    //                    // 配置同步修改或删除 put/evict
    //                    .transactionAware()
    //                    .build();
    //    return redisCacheManager;
    //}
    //
    //// 负责Department缓存的CacheManager
    //@Bean
    //public CacheManager deptCacheManager(RedisTemplate<Object, Department> deptRedisTemplate) {
    //    RedisCacheConfiguration defaultCacheConfiguration =
    //            RedisCacheConfiguration
    //                    .defaultCacheConfig()
    //                    // 设置key为String
    //                    .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(deptRedisTemplate.getStringSerializer()))
    //                    // 设置value 为自动转Json的Object
    //                    .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(deptRedisTemplate.getValueSerializer()))
    //                    // 不缓存null
    //                    .disableCachingNullValues()
    //                    // 缓存数据保存1小时
    //                    .entryTtl(Duration.ofHours(1));
    //    RedisCacheManager redisCacheManager =
    //            RedisCacheManager.RedisCacheManagerBuilder
    //                    // Redis 连接工厂
    //                    .fromConnectionFactory(deptRedisTemplate.getConnectionFactory())
    //                    // 缓存配置
    //                    .cacheDefaults(defaultCacheConfiguration)
    //                    // 配置同步修改或删除 put/evict
    //                    .transactionAware()
    //                    .build();
    //    return redisCacheManager;
    //}

}
