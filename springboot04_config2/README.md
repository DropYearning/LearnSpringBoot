#  LearnSpringBoot-04-SpringBoot项目配置2

[SpringBoot_权威教程__哔哩哔哩 (゜-゜)つロ 干杯~-bilibili](https://www.bilibili.com/video/BV1Et411Y7tQ?p=4)

## 6 配置文件加载位置
- springboot 启动会扫描以下位置的application.properties或者application.yml文件作为Spring boot的默认配置文件
    - （文件路径）file:./config/ 
    - file:./
    - （类路径）classpath:/config/
    - classpath:/
    - 以上优先级由高到底，高优先级的配置会覆盖低优先级的配置；SpringBoot会从这四个位置全部加载主配置文件；**互补配置**；
    - 我们还可以改变默认的配置文件位置:项目打包好以后，我们可以使用命令行参数的形式，启动项目的时候来指定配置文件的新位置；指定配置文件和默认加载的这些配置文件共同起作用形成互补配置；`java -jar spring-boot-02-config-02-0.0.1-SNAPSHOT.jar --spring.config.location=G:/application.properties`
- 配置项目的访问路径:`server.servlet.context-path=/boot2`

## 外部配置加载顺序
- 参考：[Spring Boot Reference Documentation](https://docs.spring.io/spring-boot/docs/current-SNAPSHOT/reference/htmlsingle/#boot-features-external-config)
- Spring Boot 支持多种外部配置方式：
    1. 命令行参数
    2. 来自java:comp/env的JNDI属性
    3. Java系统属性（System.getProperties()）
    4. 操作系统环境变量
    5. RandomValuePropertySource配置的random.*属性值
    6. jar包外部的application-{profile}.properties或application.yml(带spring.profile)配置文件
    7. jar包内部的application-{profile}.properties或application.yml(带spring.profile)配置文件
    8. jar包外部的application.properties或application.yml(不带spring.profile)配置文件
    9. jar包内部的application.properties或application.yml(不带spring.profile)配置文件
    10. @Configuration注解类上的@PropertySource
    11. 通过SpringApplication.setDefaultProperties指定的默认属性













