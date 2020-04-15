package com.example.springboot.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.thymeleaf.util.StringUtils;

import javax.servlet.http.HttpSession;
import java.util.Map;

/**
 * 处理登陆请求的控制器
 */
@Controller
public class LoginController {

    @PostMapping("/user/login")
    //@RequestMapping(value = "/user/login", method = RequestMethod.POST)
    public String login(@RequestParam("username") String username,
                        @RequestParam("password") String password,
                        Map<String,Object> map, HttpSession session){
        // 模拟用户校验
        if("admin".equals(username) && "123456".equals(password)){
            // 登陆成功后将用户名存入session
            session.setAttribute("loginUser", username);
            // 登陆成功之后，为了防止表单重复提交，可以重定向到主页
            return "redirect:/main.html";
        }else {
            // 登陆失败
            map.put("msg", "用户名密码错误");
            return "login";
        }
    }
}
