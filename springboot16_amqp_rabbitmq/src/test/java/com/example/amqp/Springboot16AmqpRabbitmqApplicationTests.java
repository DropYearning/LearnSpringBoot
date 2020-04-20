package com.example.amqp;

import com.example.amqp.bean.Book;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

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
        rabbitTemplate.convertAndSend("exchange.fanout", "",new Book("go","大佬"));
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
