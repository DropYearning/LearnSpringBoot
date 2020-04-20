#  LearnSpringBoot-SpringBoot整合-SpringBoot与任务

[SpringBoot_权威教程__哔哩哔哩 (゜-゜)つロ 干杯~-bilibili](https://www.bilibili.com/video/BV1Et411Y7tQ?p=4)



## 1 异步任务
- 在Java应用中，绝大多数情况下都是通过同步的方式来实现交互处理的；但是在处理与第三方系统交互的时候，容易造成响应迟缓的情况，之前大部分都是使用多线程来完成此类任务，其实，在Spring 3.x之后，就已经内置了@Async（异步）来完美解决这个问题。
- **同步**：同步就是**整个处理过程顺序执行，当各个过程都执行完毕，并返回结果**。
- **异步**： **异步调用则是只是发送了调用的指令，调用者无需等待被调用的方法完全执行完毕；而是继续执行下面的流程**。
    - 例如， 在某个调用中，需要顺序调用 A, B, C三个过程方法；如他们都是同步调用，则需要将他们都顺序执行完毕之后，方算作过程执行完毕； 如B为一个异步的调用方法，则在执行完A之后，调用B，并不等待B完成，而是执行开始调用C，待C执行完毕之后，就意味着这个过程执行完毕了。在Java中，一般在处理类似的场景之时，都是基于创建独立的线程去完成相应的异步调用逻辑，通过主线程和不同的业务子线程之间的执行流程，从而在启动独立的线程之后，主线程继续执行而不会产生停滞等待的情况。
## 1.1 同步的任务
```java

/**
 * 同步任务类
 */
@Service
public class SyncService {
    public void hello(){
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("处理数据中");
    }

}

@RestController
public class SyncController {

    @Autowired
    SyncService syncService;

    @GetMapping("/hello")
    public String hello(){
        asyncService.hello();
        return "success";
    }
}
```
- 结果：访问http://localhost:8080/hello后等待3s出现success，制台仍3s后输出"处理数据中"

## 1.2 异步处理
- **@EnableAsync**注解：标注在SpringBoot的启动类上，开启异步注解功能
- **@Async**注解：标注在（业务层）方法上，告诉Spring这是一个异步方法。可使得方法被异步调用，也就是说**调用者会在调用时立即返回**，而被调用方法的实际执行是交给Spring的TaskExecutor来完成。
    ```java
    /**
     * 异步任务类
     */
    @Service
    public class AsyncService {
    
        @Async
        public void hello(){
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("处理数据中");
        }
    }
    ```
- 结果：**访问url立即页面显示success，不需要等待3s，而控制台仍然需要3s后输出"处理数据中"**
- 为什么？ 因为Thread.sleep(3000)这是停止了请求方法的线程，而因为标注了异步，所以请求相当于已经发送出去了，服务器可以立即响应success，而`System.out.println("处理数据中")`会在线程睡眠结束后（3s后）被执行。

## 2 定时任务
- 项目开发中经常需要执行一些定时任务，比如需要在每天凌晨时候，分析一次前一天的日志信息。Spring为我们提供了异步执行任务调度的方式，提供TaskExecutor 、TaskScheduler 接口。
- 两个注解：
    - **@EnableScheduling**：标注在SpringBoot的启动类上，开启定时任务功能
    - **@Scheduled**：标注在需要定时执行的方法上
        - 属性cron:是Linux的cron表达式，指定任务何时执行
- 支持Linux的cron表达式：
    - ![14DRTe2](https://i.imgur.com/14DRTe2.jpg)
    - ![o3m7vkQ](https://i.imgur.com/o3m7vkQ.png)
- 例子：
    ```java
    @Service
    public class ScheduledService {
        // 定时任务
        @Scheduled(cron = "0 * * * * MON-SAT") // 每次秒针到0时执行
        public void hello2(){
            System.out.println("定时执行了hello2...");
        }
    }
    ```
- cron表达式例子：
    *  【0 0/5 14,18 * * ?】 每天14点整，和18点整，每隔5分钟执行一次
    *  【0 15 10 ? * 1-6】 每个月的周一至周六10:15分执行一次
    *  【0 0 2 ? * 6L】每个月的最后一个周六凌晨2点执行一次
    *  【0 0 2 LW * ?】每个月的最后一个工作日凌晨2点执行一次
    *  【0 0 2-4 ? * 1#1】每个月的第一个周一凌晨2点到4点期间，每个整点都执行一次；
    
## 3 邮件任务
![ev56jaZ](https://i.imgur.com/ev56jaZ.png)
-  邮件发送需要引入`spring-boot-starter-mail`
- Spring Boot 自动配置MailSenderAutoConfiguration
- 定义MailProperties内容，配置在application.yml中。例如配置qq邮箱发送email
    ```properties
    spring.mail.username=xxxx@qq.com
    spring.mail.password=gtstkoszjelabijb
    spring.mail.host=smtp.qq.com
    spring.mail.properties.mail.smtp.ssl.enable=true
    ```
- 自动装配JavaMailSender
- 测试邮件发送    
    ```java
    @SpringBootTest
    public class Springboot04TaskApplicationTests {
    
        @Autowired
        JavaMailSenderImpl mailSender;
    
        // 发送简单邮件  
        @Test
        public void contextLoads() {
            SimpleMailMessage message = new SimpleMailMessage();
            //邮件设置
            message.setSubject("通知-今晚开会");
            message.setText("今晚7:30开会");
    
            message.setTo("xxx@163.com");
            message.setFrom("xxx@qq.com");
    
            mailSender.send(message);
        }
    
        @Test
        public void test02() throws  Exception{
            //1、创建一个复杂的消息邮件
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);
    
            //邮件设置
            helper.setSubject("通知-今晚开会");
            helper.setText("<b style='color:red'>今天 7:30 开会</b>",true);
    
            helper.setTo("xxx@163.com");
            helper.setFrom("xxx@qq.com");
    
            //上传文件
            helper.addAttachment("1.jpg",new File("C:\\Users\\lfy\\Pictures\\Saved Pictures\\1.jpg"));
            helper.addAttachment("2.jpg",new File("C:\\Users\\lfy\\Pictures\\Saved Pictures\\2.jpg"));
    
            mailSender.send(mimeMessage);
    
        }
    
    }
    ```

- [SpringBoot_权威教程_邮件任务_雷丰阳_尚硅谷_哔哩哔哩 (゜-゜)つロ 干杯~-bilibili](https://www.bilibili.com/video/BV1Et411Y7tQ?p=97)

    
## 参考资料
- [同步(Synchronous)和异步(Asynchronous) - 简书](https://www.jianshu.com/p/f38460c0c37d)
- [Spring使用@Async注解 - 无涯Ⅱ - 博客园](https://www.cnblogs.com/wlandwl/p/async.html)
- [Spring中@Async注解实现“方法”的异步调用 - 郑斌blog - 博客园](https://www.cnblogs.com/zhengbin/p/6104502.html)
- [Linux Crontab 定时任务 | 菜鸟教程](https://www.runoob.com/w3cnote/linux-crontab-tasks.html)