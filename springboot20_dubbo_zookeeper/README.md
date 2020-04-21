#  LearnSpringBoot-SpringBoot整合-SpringBoot与分布式

[SpringBoot_权威教程__哔哩哔哩 (゜-゜)つロ 干杯~-bilibili](https://www.bilibili.com/video/BV1Et411Y7tQ?p=4)





## 1 分布式应用

- 在分布式系统中，国内常用zookeeper+dubbo组合，而Spring Boot推荐使用 全栈的Spring，Spring Boot+Spring Cloud。
- **ZooKeeper**(动物园管理员)是一个[分布式](https://baike.baidu.com/item/分布式/19276232)的，开放源码的[分布式应用程序](https://baike.baidu.com/item/分布式应用程序/9854429)协调服务（注册中心），是[Google](https://baike.baidu.com/item/Google)的Chubby一个[开源](https://baike.baidu.com/item/开源/246339)的实现，是Hadoop和Hbase的重要组件。它是一个为分布式应用提供一致性服务的软件，提供的功能包括：配置维护、域名服务、分布式同步、组服务等。ZooKeeper的目标就是封装好复杂易出错的关键服务，将简单易用的接口和性能高效、功能稳定的系统提供给用户。

- Dubbo(读音[ˈdʌbəʊ])是阿里巴巴公司开源的一个高性能优秀的[服务框架](https://baike.baidu.com/item/服务框架)，使得应用可通过高性能的 RPC 实现服务的输出和输入功能，可以和 [Spring](https://baike.baidu.com/item/Spring)框架无缝集成。Dubbo是一款高性能、轻量级的开源Java RPC框架，它提供了三大核心能力：面向接口的远程方法调用，智能容错和负载均衡，以及服务自动注册和发现。**负责提供方和消费方的远程过程调用**。
    - ![wVow2za](https://i.imgur.com/wVow2za.png)

## 2 注册中心

>  在微服务架构中，注册中心是最核心的基础服务之一，本文将详细介绍下注册中心的组成部分和它们之前的关系。

- **服务注册中心本质上是为了解耦服务提供者和服务消费者**。对于任何一个微服务，原则上都应存在或者支持多个提供者，这是由微服务的分布式属性决定的。更进一步，为了支持弹性扩缩容特性，一个微服务的提供者的数量和分布往往是动态变化的，也是无法预先确定的。因此，原本在单体应用阶段常用的静态LB机制就不再适用了，需要引入额外的组件来管理微服务提供者的注册与发现，而这个组件就是服务注册中心。

- ![img](https://img2018.cnblogs.com/blog/463242/201909/463242-20190917123937742-820109710.jpg)
    - 各个微服务在启动时，将自己的网络地址等信息注册到注册中心，注册中心存储这些数据。
      2. 服务消费者从注册中心查询服务提供者的地址，并通过该地址调用服务提供者的接口。

      3. 各个微服务与注册中心使用一定机制（例如心跳）通信。如果注册中心与某微服务长时间无法通信，就会注销该实例。

      4. 微服务网络地址发送变化（例如实例增加或IP变动等）时，会重新注册到注册中心。这样，服务消费者就无需人工修改提供者的网络地址了。

- 注册中心应具备以下功能：
    - **服务注册表**  : 服务注册表是注册中心的核心，它用来**记录各个微服务的信息，例如微服务的名称、IP、端口等**。服务注册表提供查询API和管理API，查询API用于查询可用的微服务实例，管理API用于服务的注册与注销。
    - **服务注册与发现**  : 服务注册是指微服务在启动时，将自己的信息注册到注册中心的过程。服务发现是指查询可用的微服务列表及网络地址的机制。
    - **服务检查**  : 注册中心使用一定的机制定时检测已注册的服务，如发现某实例长时间无法访问，就会从服务注册表移除该实例。

- Spring Cloud提供了多种注册中心的支持，例如Eureka、Consul和ZooKeeper等

## 3 SringBoot整合分布式

- 安装并部署Zookeeper

- 创建一个空工程，在其中设置两个module:

    - `Provider-ticket`负责作为生产者模块

    - `consumer-user`作为消费者模块
    - 配置各自的Maven依赖和主配置文件

- 启动`Provider-ticket`在后台运行，再运行`consumer-user`中的测试方法测试效果

### 

### 3.1 Zookeeper部署

- Docker安装Zookeeper镜像

- `docker run --name some-zookeeper --restart always -d zookeeper`

- ` docker  run --name zookeeper --restart always -d -p 2181:2181 zookeeper`

    > This image includes `EXPOSE 2181 2888 3888 8080` (the zookeeper client port, follower port, election port, AdminServer port respectively), so standard container linking will make it automatically available to the linked containers. Since the Zookeeper "fails fast" it's better to always restart it.

### 3.2 生产者模块：Provider-ticket

- `Provider-ticket`负责作为生产者模块

    - 引入相关的Maven依赖:dubbo, zkclient

        - 依赖需要参考[dubbo-spring-boot-project/README_CN.md at master · apache/dubbo-spring-boot-project](https://github.com/apache/dubbo-spring-boot-project/blob/master/README_CN.md)
        - **本次依赖配置比较复杂，问题较多**，最终使用一下的pom.xml解决问题：

        ```xml
        <dependencies>
        		<!--引入阿里巴巴的dubbo-->
        		<dependency>
        			<groupId>com.alibaba</groupId>
        			<artifactId>dubbo</artifactId>
        			<version>2.6.8</version>
        		</dependency>
        
        		<!-- dubbo需要引入netty依赖 -->
        		<dependency>
        			<groupId>io.netty</groupId>
        			<artifactId>netty-all</artifactId>
        			<version>4.1.48.Final</version>
        		</dependency>
        
        		<!-- dubbo需要引入curator依赖 -->
        		<dependency>
        			<groupId>org.apache.curator</groupId>
        			<artifactId>curator-framework</artifactId>
        			<version>4.2.0</version>
        		</dependency>
        		<dependency>
        			<groupId>org.apache.curator</groupId>
        			<artifactId>curator-recipes</artifactId>
        		</dependency>
        
        		<!--引入zookeeper的客户端工具-->
        		<!-- https://mvnrepository.com/artifact/com.101tec/zkclient -->
        		<dependency>
        			<groupId>com.101tec</groupId>
        			<artifactId>zkclient</artifactId>
        			<version>0.11</version>
        		</dependency>
        
        		<dependency>
        			<groupId>org.springframework.boot</groupId>
        			<artifactId>spring-boot-starter-test</artifactId>
        			<scope>test</scope>
        		</dependency>
        
        		<dependency>
        			<groupId>org.springframework.boot</groupId>
        			<artifactId>spring-boot-starter-web</artifactId>
        		</dependency>
        	</dependencies>
        ```

    - 在主配置文件中配置dubbo要扫描的包和注册中心地址\端口:

        ```properties
        # 配置dubbo
        dubbo.application.name=provider-ticket
        dubbo.registry.address=zookeeper://127.0.0.1:2181
        # 将com.example.ticket.service包下的服务发布出去
        dubbo.scan.base-packages=com.example.ticket.service
        ```

    - 实现服务方法：provider-ticket/com.example.ticket.service.impl.TicketServiceImpl

        - 在实现了生产者的方法上标注@Service（**注意该Service注解是Dubbo中的**）

        ```java
        @EnableDubbo
        @Component
        @Service // 注意该Service注解是Dubbo中的，可以将服务发布出去
        public class TicketServiceImpl implements TicketService {
            @Override
            public String getTicket() {
                return "《厉害了，我的国》";
            }
        }
        ```

### 3.2 消费者模块：consumer-user

- 1、消费者模块也需要导入Maven依赖：

    ```xml
    <dependencies>
    		<dependency>
    			<groupId>com.alibaba</groupId>
    			<artifactId>dubbo</artifactId>
    			<version>2.6.8</version>
    		</dependency>
    
    		<!-- dubbo需要引入netty依赖 -->
    		<dependency>
    			<groupId>io.netty</groupId>
    			<artifactId>netty-all</artifactId>
    			<version>4.1.48.Final</version>
    		</dependency>
    
    		<!-- dubbo需要引入curator依赖 -->
    		<dependency>
    			<groupId>org.apache.curator</groupId>
    			<artifactId>curator-framework</artifactId>
    			<version>4.2.0</version>
    		</dependency>
    		<dependency>
    			<groupId>org.apache.curator</groupId>
    			<artifactId>curator-recipes</artifactId>
    		</dependency>
    
    		<!--引入zookeeper的客户端工具-->
    		<!-- https://mvnrepository.com/artifact/com.101tec/zkclient -->
    		<dependency>
    			<groupId>com.101tec</groupId>
    			<artifactId>zkclient</artifactId>
    			<version>0.11</version>
    		</dependency>
    
    		<dependency>
    			<groupId>org.springframework.boot</groupId>
    			<artifactId>spring-boot-starter-web</artifactId>
    		</dependency>
    
    		<dependency>
    			<groupId>org.springframework.boot</groupId>
    			<artifactId>spring-boot-starter-test</artifactId>
    			<scope>test</scope>
    			<exclusions>
    				<exclusion>
    					<groupId>org.junit.vintage</groupId>
    					<artifactId>junit-vintage-engine</artifactId>
    				</exclusion>
    			</exclusions>
    		</dependency>
    	</dependencies>
    ```

- 2、配置消费者模块的主配置文件：

    ```properties
    # 配置dubbo
    dubbo.application.name=consumer-user
    dubbo.registry.address=zookeeper://127.0.0.1:2181
    ```

- 3、**在消费者模块中放一份一模一样的TicketService代码，并且全类名路径要相同，并且只需要拷贝接口即可**
    - ![NUyhbUD](https://i.imgur.com/NUyhbUD.png)

- 4、在UserService下调用TicketService:

    ```java
    @Service // Spring中的@Service
    public class UserService {
    
        @Reference
        TicketService ticketService;
    
        public void hello(){
            String ticket = ticketService.getTicket();
            System.out.println("买到票：" + ticket);
        }
    }
    ```

- 5、在消费者模块中编写测试方法运行：

    ```java
    @SpringBootTest
    class ConsumerUserApplicationTests {
    
    	@Autowired
    	UserService userService;
    
    	@Test
    	void contextLoads() {
    		userService.hello();
    	}
    }
    ```

- 结果如下：![image-20200421165417090](/Users/brightzh/Library/Application Support/typora-user-images/image-20200421165417090.png)

- **掉坑总结**：

    - 消费者和生产者的主运行类上都加上`@EnableDubbo`
    - Log4j的错误是因为jar包的重复，可以参考[解决记录之十二——分布式Dubbo与Zookeeper以及SLF4J: Class path contains multiple SLF4J bindings等错误_Java_江南T雨-CSDN博客](https://blog.csdn.net/Lswx2006/article/details/89950809)。 由于生产者和消费者两侧都需要引入相似的包，因此该问题需要解决2次
    - 不要引入Maven Repository: com.github.sgroschupf » zkclient](https://mvnrepository.com/artifact/com.github.sgroschupf/zkclient)，应该引入更新的[Maven Repository: com.101tec » zkclient](https://mvnrepository.com/artifact/com.101tec/zkclient)
    - 善用IDEA的Maven diagram 发现依赖引用的问题！



## 



## 参考资料

- [微服务注册中心原理，看这篇就够了！ - Java碎碎念 - 博客园](https://www.cnblogs.com/haha12/p/11532910.html)
- [apache/dubbo-spring-boot-project: Spring Boot Project for Apache Dubbo](https://github.com/apache/dubbo-spring-boot-project)
- [解决记录之十二——分布式Dubbo与Zookeeper以及SLF4J: Class path contains multiple SLF4J bindings等错误_Java_江南T雨-CSDN博客](https://blog.csdn.net/Lswx2006/article/details/89950809)