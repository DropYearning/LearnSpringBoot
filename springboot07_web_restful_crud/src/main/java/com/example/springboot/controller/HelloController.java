package com.example.springboot.controller;

import com.example.springboot.eexception.UserNotExistException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Arrays;
import java.util.Map;

@Controller
public class HelloController {

    @ResponseBody
    @RequestMapping("/hello")
    public String hello(@RequestParam("user") String user){
        if (user.equals("aaa")){ // 模拟自定义异常的抛出
            throw new UserNotExistException();
        }
        return "Hello World!";
    }

    // 由thymeleaf解析，需求：查出一些数据显示在页面
    @RequestMapping("/success")
    public String success(Map<String, Object> map){

        map.put("hello", "<h1>你好</h1>");
        map.put("users", Arrays.asList("张三", "李四", "王五"));

        // 将对/success的url路径解析到：class:path:/templates/success1.html文件
        return "success1";
    }

    @RequestMapping({"/", "/login.html"})
    public String index(){
        return "login";
    }
}
