package com.example.springboot14;

import com.example.springboot14.bean.Employee;
import com.example.springboot14.mapper.EmployeeMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;

@SpringBootTest
class Springboot14CacheApplicationTests {

    @Autowired
    EmployeeMapper employeeMapper;

    @Autowired
    StringRedisTemplate stringRedisTemplate; // 操作字符串

    @Autowired
    RedisTemplate redisTemplate; //<Object, Object>，k-v都是对象

    @Autowired
    RedisTemplate<Object, Employee> empRedisTemplate; // 自定义的RedisTemplate，负责Employee -> JSON

    @Test // 测试redis操作String,List,Set,Hash,ZSet(有序集合)
    public void test1(){
        //stringRedisTemplate.opsForValue().append("msg", "hello");
        //String msg = stringRedisTemplate.opsForValue().get("msg");
        //System.out.println(msg);
        //stringRedisTemplate.opsForList().leftPush("mylist","1" );
        //stringRedisTemplate.opsForList().leftPush("mylist","2" );
        //stringRedisTemplate.opsForList().leftPush("mylist","3" );

    }

    @Test // 测试保存对象
    public void test2(){
        // 默认如果保存对象，使用jdk序列化机制，序列化后的数据保存到redis中
        Employee empById = employeeMapper.getEmpById(1);
        //redisTemplate.opsForValue().set("emp-01", empById);
        // 将数据以json方式保存，使用redisTemplate的Json序列化器
        empRedisTemplate.opsForValue().set("emp-01", empById);
    }

    @Test
    void contextLoads() {
        Employee empById = employeeMapper.getEmpById(1);
        System.out.println(empById);
    }

}
