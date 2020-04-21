package com.example.ticket.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.dubbo.config.spring.context.annotation.EnableDubbo;
import com.example.ticket.service.TicketService;
import org.springframework.stereotype.Component;

@EnableDubbo
@Component
@Service // 注意该Service注解是Dubbo中的，可以将服务发不出去
public class TicketServiceImpl implements TicketService {
    @Override
    public String getTicket() {
        return "《厉害了，我的国》";
    }
}
