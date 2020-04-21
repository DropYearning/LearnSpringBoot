package com.example.user.service;

import com.alibaba.dubbo.config.annotation.Reference;
import com.example.ticket.service.TicketService;
import org.springframework.stereotype.Service;

/**
 * 用户服务类：
 *  如何在用户服务类中调用另一个Module中的服务TicketServiceImpl？：
 *      1、将服务提供者TicketServiceImpl注入到"注册中心"中
 *      2、UserService向注册中心请求调用服务
 */

@Service // Spring中的@Service
public class UserService {

    @Reference
    TicketService ticketService;

    public void hello(){
        String ticket = ticketService.getTicket();
        System.out.println("买到票：" + ticket);
    }
}
