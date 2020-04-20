#  LearnSpringBoot-SpringBoot整合-消息

[SpringBoot_权威教程__哔哩哔哩 (゜-゜)つロ 干杯~-bilibili](https://www.bilibili.com/video/BV1Et411Y7tQ?p=4)



## 1 消息概述

- 大多应用中，可通过消息服务中间件来提升系统异步通信、扩展解耦能力

- 消息服务中两个重要概念：
    - **消息代理（message broker）**：存放消息队列的服务器
    - **目的地（destination）**：消息传递到指定目的地
    - 当消息发送者发送消息以后，将由消息代理接管，消息代理保证消息传递到指定目的地。
    - 消息队列主要有两种形式的目的地：
        - **队列（queue）**：点对点消息通信（point-to-point） 
        - **主题（topic）**：发布（publish）/订阅（subscribe）消息通信
    - 点对点式：
        -  消息发送者发送消息，消息代理将其放入一个队列中，消息接收者从队列中获取消息内容， 消息读取后被移出队列 
        - **消息只有唯一的发送者和接受者**，但并不是说只能有一个接收者
    - 发布订阅式：
        - 发送者（发布者）发送消息到主题，多个接收者（订阅者）监听（订阅）这个主题，那么 就会在消息到达时同时收到消息
- 消息服务规范：
    - JMS（Java Message Service，JAVA消息服务）： 基于JVM消息代理的规范。**ActiveMQ、HornetMQ是JMS实现**
    - AMQP（Advanced Message Queuing Protocol， 高级消息队列协议）：，也是一个消息代理的规范，**兼容JMS， RabbitMQ是AMQP的实现**
    - ![kqbjog8](https://i.imgur.com/kqbjog8.png)
- Spring支持 
    - spring-jms提供了对JMS的支持 
    -  spring-rabbit提供了对AMQP的支持 
    - 需要ConnectionFactory的实现来连接消息代理 
    -  提供JmsTemplate、RabbitTemplate来发送消息 
    -  @JmsListener（JMS）、@RabbitListener（AMQP）注解在方法上监听消息代理发 布的消息
    - @EnableJms、@EnableRabbit开启消息队列支持
- Spring Boot自动配置 
    - JmsAutoConfiguration 
    -  RabbitAutoConfiguration

### 1.1 异步处理

- ![s3Wr8Iq](https://i.imgur.com/s3Wr8Iq.png)

### 1.2  应用解藕

-  ![4AlvADP](https://i.imgur.com/4AlvADP.png)
- 将下单信息写入消息队列，库存系统订阅消息队列中的订单信息，实现订单系统和库存系统的解藕

###  1.3 流量削峰

- ![kYbwLO3](https://i.imgur.com/kYbwLO3.png)
- 使用**定长的消息队列**实现峰控



## 2 SpringBoot整合RabbitMQ

### 2.1 RabbitMQ简介

- RabbitMQ是一个由erlang开发的AMQP(Advanved Message Queue Protocol)的开源实现。

- 核心概念:

    - ![eTVRboB](https://i.imgur.com/eTVRboB.png)
    - **Message 消息**，消息是**不具名**的，它**由消息头和消息体组成**。消息体是不透明的，而消息头则由一系列的可选属性组成，这些属性包括**routing-key（路由键**）、**priority（相对于其他消息的优先权）**、**delivery-mode（指出该消息可能需要持久性存储）**等。
    - **Publisher 消息的生产者**，也是一个向交换器发布消息的客户端应用程序。
    - **Exchange 交换器**，用来接收生产者发送的消息并将这些消息路由给服务器中的队列。 **Exchange有4种类型：direct(默认)，fanout, topic, 和headers**，不同类型的Exchange转发消息的策略有所区别。**交换器来决定消息应该发往哪一个消息队列**

    - **Queue 消息队列**，用来保存消息直到发送给消费者。它是消息的容器，也是消息的终点。一个消息可投入一个或多个队列。消息一直在队列里面，等待消费者连接到这个队列将其取走。
    - **Binding 绑定**，**用于消息队列和交换器之间的关联。一个绑定就是基于路由键将交换器和消息队列连接起来的路由规则**，所以**可以将交换器理解成一个由绑定构成的路由表**。 Exchange 和Queue的绑定可以是多对多的关系。
    - **Connection 网络连接**，比如一个TCP连接。
    - **Channel 信道**，多路复用连接中的一条独立的双向数据流通道。信道是建立在真实的TCP连接内的虚拟连接，AMQP 命令都是通过信道发出去的，不管是发布消息、订阅队列还是接收消息，这些动作都是通过信道完成。因为对于操作系统来说建立和销毁 TCP 都是非常昂贵的开销，所以引入了信道的概念，以**复用一条 TCP连接**。
    - **Consumer 消息的消费者**，表示一个从消息队列中取得消息的客户端应用程序。
    - **Virtual Host 虚拟主机**，表示一批交换器、消息队列和相关对象。虚拟主机是共享相同的身份认证和加密环境的独立服务器域。每个 vhost 本质上就是一个 mini 版的 RabbitMQ 服务器，拥有自己的队列、交换器、绑定和权限机制。vhost 是 AMQP 概念的基础，必须在连接时指定， RabbitMQ 默认的 vhost 是 / 。
    - **Broker** 表示消息队列服务器实体

### 2.2 RabbitMQ运行机制

- AMQP 中的消息路由机制：

    - AMQP 中消息的路由过程和 Java 开发者熟悉的 JMS 存在一些差别，**AMQP 中增加了 Exchange 和 Binding 的角色**。*生产者把消息发布到 Exchange 上，消息最终到达队列并被消费者接收，而 Binding 决定交换器的消息应该发送到那个队列*。
        - ![BQNt4rJ](https://i.imgur.com/BQNt4rJ.png)

- Exchange分发消息时根据类型的不同分发策略有区别，目前共四种类型： **direct、fanout、topic、headers** 。headers 匹配 AMQP 消息的 header 而不是路由键， headers 交换器和 direct 交换器完全一致，但性能差很多， 目前几乎用不到了，所以直接看另外三种类型：

- `Direct Exchange`:**消息中的路由键（routing key）如果和 Binding 中的 binding key 一致**， 交换器就将消息发到对应的队列中。路由键与队列名完全匹配，如果一个队列绑定到交换机要求路由键为 “dog”，则只转发 routing key 标记为“dog”的消息，不会转 发“dog.puppy”，也不会转发“dog.guard”等等。它是完全匹配、**单播**的模式。
    - ![image-20200420174653711](/Users/brightzh/Library/Application Support/typora-user-images/image-20200420174653711.png)

- `Fanout Exchange`:每个发到 fanout 类型交换器的消息都会分到所 有绑定的队列上去。fanout 交换器不处理路由键， 只是简单的将队列绑定到交换器上，每个发送 到交换器的消息都会被转发到与该交换器绑定 的所有队列上。很像子网**广播，每台子网内的 主机都获得了一份复制的消息。fanout 类型转发消息是最快的**。
    - ![4GZhd1M](https://i.imgur.com/4GZhd1M.png)
- `Topic Exchange`:topic 交换器通过模式匹配分配消息的路由键属 性，将路由键和某个模式进行匹配，此时队列需要绑定到一个模式上。它将路由键和绑定键 的字符串切分成单词，这些单词之间用点隔开。 它同样也会识别两个通配符：符号“#”和符号 “*”。#匹配0个或多个单词， *匹配一个单词。
    - ![08Z2Rt9](https://i.imgur.com/08Z2Rt9.png)

### 2.3 RabbitMQ的上手使用
- 1、在Docker中安装带有-management后缀的带有web管理页面的RabbitMQ镜像。启动时需要映射两个端口，分别是消息通信端口和web管理端口：`docker run -d -p 5672:5672 -p 15672:15672 rabbitmq:3.8.3-management`
- 2、在管理页面创建相应的交换器Exchange和消息队列Queue:
    - ![qnlYYUX](https://i.imgur.com/qnlYYUX.png)
    - ![nvwbHS7](https://i.imgur.com/nvwbHS7.png)
    - ![bUDLccO](https://i.imgur.com/bUDLccO.png)
- 3、绑定Exchange和相应的Queue
    - ![Zo1zel2](https://i.imgur.com/Zo1zel2.png)
- 4、在后台页面中测试消息的publish 
    - ![bUDLccO](https://i.imgur.com/bUDLccO.png)
    - ![image-20200420185455790](/Users/brightzh/Library/Application Support/typora-user-images/image-20200420185455790.png)
    - 向exchange.direct发送数据:
        - 以atguigu为路由键：队列atguigu收到
        - 以atguigu.news为路由键，队列atguigu.news收到
        - 以atguigu.emps为路由键，队列atguigu.emps说到
        - 以gulixueyuan.news为路由键，队列gulixueyuan.news收到
        - 总结：**direct交换器是以严格匹配方式发送**
    - 向exchange.fanout发送数据：
        - 以atguigu为路由键：所有队列全部收到
        - 总结：**fanout交换器是以广播方式发送**
        - **使用fanout交换器发送消息时可以省略不指定路由键**
    - 向exchange.topic发送数据：
        - 以atguigu.#为路由键：atguigu\atguigu.news\atguigu.exps收到
        - 总结：**topic交换器以通配匹配的方式发送**

### 2.4 SpringBoot自动配置RabbitMQ的原理
- 1、自动配置类`RabbitAutoConfiguration`
- 2、有自动配置了连接工厂ConnectionFactory；
- 3、`RabbitProperties` 封装了 RabbitMQ的配置
- 4、 `RabbitTemplate` ：在方法中自动注入后可以用来给RabbitMQ发送和接受消息；
- 5、 AmqpAdmin ： RabbitMQ系统管理功能组件;
    * AmqpAdmin：创建和删除 Queue，Exchange，Binding
- 6、@EnableRabbit +  @RabbitListener 监听消息队列的内容

### 2.5 SpringBoot整合RabbitMQ的步骤
- 1、在SpringBoot的主配置文件中配置RabbitMQ
    ```properties
    spring.rabbitmq.host=127.0.0.1
    spring.rabbitmq.username=guest
    spring.rabbitmq.password=guest
    # 可省略端口和V-host（默认/）的配置
    spring.rabbitmq.port=5672
    ```
- 2、编写测试类进行一些简单测试
    ```java
    @SpringBootTest
    class Springboot16AmqpRabbitmqApplicationTests {
    
        @Autowired
        RabbitTemplate rabbitTemplate;
    
    
        // 测试消息发送
        @Test
        void contextLoads() {
            // 方法一：通过send方法，将要发送数据定制成一个Message，定义其中的消息体内容和消息头
            //rabbitTemplate.send(exchange, routeKey, msg);
    
            // 方法二：通过convertAndSend方法，只需要传入一个Object对象，会自动将其序列化并发送
            //rabbitTemplate.convertAndSend(exchange, routeKey, object);
            //Map<String, Object> map = new HashMap<>(); // 存放要传入的数据
            //map.put("msg", "这是第一个消息");
            //map.put("data", Arrays.asList("1", "2", "3"));
            //    // 对象数据会被序列化后发送出去，所以在后台看到的可能是乱码
            //rabbitTemplate.convertAndSend("exchange.direct", "atguigu.news", map);
            rabbitTemplate.convertAndSend("exchange.direct", "atguigu.news", new Book("java","大佬"));
    
        }
    
        // 测试广播消息发送(注意广播消息的routingKey要写为""或者null)
        @Test
        public void test2(){
            rabbitTemplate.convertAndSend("exchange.fanout", "",new Book("python","大佬"));
        }
    
        // 测试消息接受
        @Test
        public void testReceive(){
            // receiveAndConvert自动将Message反序列化为Object对象，并且消息队列的中消息数目--
            Object o = rabbitTemplate.receiveAndConvert("atguigu.news");// 从队列atguigu.news中取一条数据
            System.out.println(o.getClass()); // 输出class java.util.HashMap
            System.out.println(o); // 输出{msg=这是第一个消息, data=[1, 2, 3]}
        }
    }
    ```
### 2.6 自定义配置类实现RabbitMQ以JSON格式发送数据
- **JavaBean需要有无参数构造器和有参数构造器！**
```java
@Configuration
public class MyAmqpConfig {
    @Bean
    public MessageConverter messageConverter(){
        return new Jackson2JsonMessageConverter(); // 以JSON形式发送数据
    }
}
```
- ![8KrxaE7](https://i.imgur.com/8KrxaE7.png)
- ![DKg86fq](https://i.imgur.com/DKg86fq.png)

### 2.7 RabbitMQ的监听机制
- 例如销售系统中，订单处理模块需要监听订单接受模块发送来的消息，一旦有新的订单，订单处理模块就应该收到通知并取出进行处理
- 1、`@EnableRabbit`: 加载SpringBoot的启动方法`Springboot16AmqpRabbitmqApplication`上开启基于注解的RabbitMQ  
- 2、`@RabbitListener`注解：可以加载方法上，只要消息队列中有内容输入，就会触发
    - 属性queus:指定要监听的消息队列名字，可以是一个数组
        ```java
        /**
         * Book业务类，监听消息队列中的内容
         */
        
        @Service
        public class BookService {
        
            @RabbitListener(queues = "atguigu.news")
            public void receive(Book book){
                System.out.println("收到消息:" + book);
            }
            
            @RabbitListener(queues = "atguigu")
            public void receive2(Message message){
                System.out.println(message.getBody());
                System.out.println(message.getMessageProperties());
            }
        }
        ```
### 2.8 在程序中创建exchange\queue\Binding
- 之前我们是在web管理后台操作创建exchange\queue等对象的，如何在程序中创建他们？
- 使用`AmqpAdmin` ： 创建和删除 Queue，Exchange，Binding
- 1、在需要操作这些对象的类中@AutoWired注入AmqpAdmin对象
- 2、使用AmqpAdmin对象的declare/delete方法创建/删除
    - ![kZg8zPG](https://i.imgur.com/kZg8zPG.png)
        ```java
        @SpringBootTest
        class Springboot16AmqpRabbitmqApplicationTests {
        
            @Autowired
            RabbitTemplate rabbitTemplate;
        
            @Autowired
            AmqpAdmin amqpAdmin; //注入AmqpAdmin来操作exchange\queue\Binding
        
            @Test
            public void createExchange(){
                amqpAdmin.declareExchange(new DirectExchange("amqpadmin.exchange"));
                System.out.println("创建完成");
            }
        
            @Test
            public void createQueue(){
                amqpAdmin.declareQueue(new Queue("amqpadmin.queue", true));
                System.out.println("创建完成");
            }
        
            @Test
            public void createBanding(){
                amqpAdmin.declareBinding(new Binding("amqpadmin.queue", Binding.DestinationType.QUEUE,"amqpadmin.exchange", "amqp.haha", null));
                System.out.println("创建完成");
            }
        }
        ```

## 参考
- [消息队列之 RabbitMQ - 简书](https://www.jianshu.com/p/79ca08116d57)
- [消息队列Kafka、RocketMQ、RabbitMQ的优劣势比较 - 知乎](https://zhuanlan.zhihu.com/p/60288391)
    - ![evH2gAf](https://i.imgur.com/evH2gAf.jpg)







