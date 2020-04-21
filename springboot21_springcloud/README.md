#  LearnSpringBoot-SpringBoot整合SpringCloud实现分布式应用

[SpringBoot_权威教程__哔哩哔哩 (゜-゜)つロ 干杯~-bilibili](https://www.bilibili.com/video/BV1Et411Y7tQ?p=4)



## 1 Spring Cloud

- Dubbo是一个分布式服务框架，主要解决服务之间远程过程调用（RPC）的问题
- **Spring Cloud是一个分布式的整体解决方案**。Spring Cloud 为开发者提供了在分布式系统（配 置管理，服务发现，熔断，路由，微代理，控制总线，一次性token，全局琐，leader选举，分布式session，集群状态）中快速构建的工具，使用Spring Cloud的开发者可以快速的启动服务 或构建应用、同时能够快速和云平台资源进行对接。
- 一般来说，只要是分布式系统中需要用到的功能，Spring Cloud中都有解决方案
- SpringCloud分布式开发五大常用组件：
    - 服务发现——Netflix Eureka 
    - 客户端负载均衡——Netflix Ribbon 
    - 断路器——Netflix Hystrix 
    - 服务网关——Netflix Zuul 
    - 分布式配置——Spring Cloud Config

## 2 实现简单的Spring Cloud分布式应用

- 创建一个空工程，里面具有下面几个模块：
- 注册中心模块：`eureka-server`，替代Zookeeper,使用初始化向导创建时勾选`Eureka Server`
- 服务提供者模块：`provider-ticket`，使用向导创建时勾选`Eureka Discovery Client`，使服务提供者能被注册到注册中心
- 服务消费者模块：`consumer-user`，使用向导创建时勾选`Eureka Discovery Client`，使消费者者能被注册到注册中心

### 2.1 注册中心模块：`eureka-server`

- 1、启动之前进行配置，在/resources下的配置文件写入：

    ```properties
    server.port=8761
        # eureka实例的主机名
    eureka.instance.hostname=eureka-server
        # 不将eureka服务器注册在eureka中
    eureka.client.register-with-eureka=false
        # 不从eureka上获取服务的注册信息（注册中心自己不用获取注册信息）
    eureka.client.fetch-registry=false
    eureka.client.service-url.defaultZone=http://localhost:8761/eureka/
    ```

- 2、在注册中心的主运行类上标注**@EnableEurekaServer**来启用注册中心

- 3、启动注册中心模块：![A16Ighg](https://i.imgur.com/A16Ighg.png)

- 4、访问http://localhost:8761/，结果如下：![peOC4Ov](https://i.imgur.com/peOC4Ov.png)

### 2.2 服务提供者模块：`provider-ticket`

- 1、编写服务提供者方法：

    ```java
    // 业务层
    @Service
    public class TicketService {
    
        public String getTicket(){
            return "《西游记》";
        }
    }
    
    // 控制器
    @RestController
    public class TicketController {
    
        @Autowired
        TicketService ticketService;
    
        @GetMapping("/ticket")
        public String getTicket(){
            return ticketService.getTicket();
        }
    }
    ```

- 2、在生产者模块的主配置文件中配置：

    ```properties
    # 指定本服务的端口和名字
    server.port=8001
    spring.application.name=provider-ticket
    # 配置eureka
        ## 注册服务时候使用服务的ip进行注册
    eureka.instance.prefer-ip-address=true
    eureka.client.service-url.defaultZone=http://localhost:8761/eureka/
    
    ```

- 3、在后台启动生产者模块，注意确实是在8001端口启动的
    - ![LDTLQxf](https://i.imgur.com/LDTLQxf.png)

- 在eureka的管理后台也可以看到服务注册成功：![mJn21Vr](https://i.imgur.com/mJn21Vr.png)

> 可以将运行在不同端口的生产者模块打包成jar，并且同时运行，这样可以在eureka的管理后台看到多个生产者服务的实例
>
> ![drgN2tt](https://i.imgur.com/drgN2tt.png)

### 2.3 服务消费者模块：`consumer-user`

- 1、编写消费者业务类：

    ```java
    @RestController
    public class UserController {
    
        @Autowired
        RestTemplate restTemplate;
    
        @GetMapping("/buy")
        public String buyTicket(String name){
            // RPC获取服务端提供的电影票的名字
            String s = restTemplate.getForObject("http://PROVIDER-TICKET/ticket", String.class);
            return name + "购买了" + s;
        }
    }
    ```

- 2、在消费者主配置文件中配置euruka：

    ```properties
    # 指定本服务的端口和名字
    server.port=8200
    spring.application.name=consumer-user
    # 配置eureka
    ## 注册服务时候使用服务的ip进行注册
    eureka.instance.prefer-ip-address=true
    eureka.client.service-url.defaultZone=http://localhost:8761/eureka/
    ```

- 3、在主运行类上标注**@EnableDiscoveryClient**注解来开启发现服务的功能：

    ```java
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
    ```

- 4、启动消费者应用，测试效果
    - ![4hGdIGl](https://i.imgur.com/4hGdIGl.png)
    - ![zWh0ojZ](https://i.imgur.com/zWh0ojZ.png)

- 5、如果此时在Eureka中注册并启动了多个服务端的实例，并且使用**@LoadBalanced**注解开启了负载均衡机制，那么当在浏览器中发送请求后，会使用负载均衡决定具体请求哪一个服务实例

## 参考资料

- [Spring Cloud中文网-官方文档中文版](https://www.springcloud.cc/)

## 