package com.example.providerticket.service;

import org.springframework.stereotype.Service;

@Service
public class TicketService {

    public String getTicket(){
        System.out.println("这是8001端口");
        return "《西游记》";
    }

}
