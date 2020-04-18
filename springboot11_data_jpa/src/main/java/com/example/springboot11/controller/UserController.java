package com.example.springboot11.controller;

import com.example.springboot11.entity.User;
import com.example.springboot11.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
public class UserController {

    @Autowired
    UserRepository userRepository;

    @GetMapping("/user/{id}")
    public User getUser(@PathVariable("id") Integer id) {
        // 注意这里如果不加@PathVariable("id")会报500错误。
        // The given id must not be null!; nested exception is java.lang.IllegalArgumentException: The given id must not be null!
        User user = userRepository.findById(id).orElse(null);
        return user;
    }

    @GetMapping("/user")
    public User insertUser(User user){
        User save = userRepository.save(user);
        return save;
    }
}
