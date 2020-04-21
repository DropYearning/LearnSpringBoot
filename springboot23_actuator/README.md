#  LearnSpringBoot-SpringBoot配置监控管理

[SpringBoot_权威教程__哔哩哔哩 (゜-゜)つロ 干杯~-bilibili](https://www.bilibili.com/video/BV1Et411Y7tQ?p=4)



## 1  SpringBoot配置监控管理

- 通过引入`spring-boot-starter-actuator`，可以使用Spring Boot为我们提供的准 生产环境下的应用监控和管理功能。我们可以通过HTTP，JMX，SSH协议来进行操作，自动得到审计、健康及指标信息等

- ![zqf8NaF](https://i.imgur.com/zqf8NaF.png)

- 相关依赖：

    ```xml
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-actuator</artifactId>
    </dependency>
    ```

- 步骤：

    - 引入`spring-boot-starter-actuator `

        - 关闭对/actuator页面的安全限制

            ```properties
            # 解除安全限制
            management.endpoints.web.exposure.include=*
            ```

            

    - 通过http方式访问监控端点

        - http://localhost:8080/actuator 显示所有可以访问的监控管理地址
        - 例如：http://localhost:8080/actuator/beans 监控所有注入的beans

    - 可进行shutdown（POST 提交，此端点默认关闭）

        - 需要在配置文件中开启:`endpoint.shutdown.enable=true`
        - 可以向http://localhost:8080/actuator/shutdown发送post请求来远程关闭服务

- ![AjigjKl](https://i.imgur.com/AjigjKl.png)



## 2 定制端点信息

- 定制端点一般通过endpoints+端点名+属性名来设置。 

- 修改端点id名（endpoints.beans.id=mybeans）

- 开启远程应用关闭功能（endpoints.shutdown.enabled=true）

- 关闭端点（endpoints.beans.enabled=false） 

- 开启所需端点 	
    - endpoints.enabled=false 
    - endpoints.beans.enabled=true

-  定制端点访问**根路径** 
    -  management.context-path=/manage 
    - 可以结合Spring Security对actuator的根路径做访问控制
-  关闭http端点
    - management.port=-1

## 3 自定义健康状态指示器

 * 1、编写一个指示器 实现 `HealthIndicator` 接口
 * 2、指示器的名字 xxxxHealthIndicator
 - 3、加入容器中

```java
@Component
public class MyAppHealthIndicator implements HealthIndicator {

    @Override
    public Health health() {

        //自定义的检查方法
        //Health.up().build()代表健康
        return Health.down().withDetail("msg","服务异常").build();
    }
}
```



## 参考资料

- [Spring Boot Actuator 使用 - 简书](https://www.jianshu.com/p/af9738634a21)

    