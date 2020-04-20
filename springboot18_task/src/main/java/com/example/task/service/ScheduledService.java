package com.example.task.service;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class ScheduledService {

    // 定时任务
    //@Scheduled(cron = "0 * * * * MON-SAT") // 每次秒针到0时执行
    //@Scheduled(cron = "0,1,2,3,4,5 * * * * MON-SAT") // 支持枚举某一位的时刻
    //@Scheduled(cron = "0-5 * * * * MON-SAT") //支持区间
    @Scheduled(cron = "0/5 * * * * MON-SAT") //    /代表步长0/5表示从0秒开始每5s执行一次
    public void hello2(){
        System.out.println("定时执行了hello2...");
    }
}
