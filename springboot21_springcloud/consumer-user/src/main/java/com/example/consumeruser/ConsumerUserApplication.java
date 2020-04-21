package com.example.consumeruser;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;


@EnableDiscoveryClient // 开启服务发现功能
@SpringBootApplication
public class ConsumerUserApplication {

	public static void main(String[] args) {
		SpringApplication.run(ConsumerUserApplication.class, args);
	}

	@Bean
	@LoadBalanced // 启动负载均衡
	public RestTemplate restTemplate(){
		return new RestTemplate();
	}
}
