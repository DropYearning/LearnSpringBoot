package com.example.springboot.eexception;

/**
 * 自定义异常，用户不存在
 */
public class UserNotExistException extends RuntimeException {
    public UserNotExistException(){
        super("用户不存在");
    }
}
