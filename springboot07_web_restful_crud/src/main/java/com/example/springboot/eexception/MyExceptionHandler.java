package com.example.springboot.eexception;


import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class MyExceptionHandler  {

    // 方法一：利用`ExceptionHandler`继承实现自定义的异常处理器，并返回一个Map<String,Object>，SpringBoot会自动解析为JSON返回：
    // 这种方法无论是浏览器还是客户端返回的都是JSON格式数据
    //@ResponseBody
    //@ExceptionHandler(UserNotExistException.class)
    //public Map<String, Object> handleException(Exception e){
    //    // 响应自己的JSON数据
    //    Map<String, Object> map = new HashMap<>();
    //    map.put("code", "user.notexist");
    //    map.put("message", e.getMessage());
    //    return map;
    //
    //}

    // 方法二：在前面的基础上实现自适应
    @ExceptionHandler(UserNotExistException.class)
    public String handleException(Exception e, HttpServletRequest request){
        // 响应自己的JSON数据
        Map<String, Object> map = new HashMap<>();
        // 传入自定义的错误状态码，否则一定是200
        request.setAttribute("javax.servlet.error.status_code", 404);
        map.put("code", "user.notexist");
        map.put("message", e.getMessage());
        request.setAttribute("ext", map);
        return "forward:/error"; //转发到/error
    }
}
