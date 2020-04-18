#  LearnSpringBoot-SpringBoot-自定义Starter

[SpringBoot_权威教程__哔哩哔哩 (゜-゜)つロ 干杯~-bilibili](https://www.bilibili.com/video/BV1Et411Y7tQ?p=4)



## 自定义Starter的几个重点

-  自动装配Bean：自动装配使用配置类（@Configuration）结合Spring4 提供的条件判断注解 @Conditional及Spring Boot的派生注解如@ConditionOnClass完成
- 配置自动装配Bean：将标注@Configuration的自动配置类，放在classpath下METAINF/spring.factories文件中
- 启动器（starter）：启动器模块是一个空 JAR 文件，仅提供辅助性依赖管理，这些依赖可能用于自动 装配或者其他类库
    -  命名规约，推荐使用以下命名规约：
    -  官方命名空间 
        -  前缀：“spring-boot-starter-” 
        -  模式：spring-boot-starter-模块名 
        -  举例：spring-boot-starter-web、spring-boot-starter-actuator、spring-boot-starter-jdbc
    -  自定义命名空间 –
        -  后缀：“-spring-boot-starter” 
        -  模式：模块-spring-boot-starter 
        -  举例：mybatis-spring-boot-starte



## 准备

- 创建两个项目
    - `my-spring-boot-starter`用来做依赖引入
    - `my-spring-boot-autoconfigurer` 用来做自动配置
    - `my-spring-boot-test`测试能否导入依赖并正常使用



> 注意：本项目的结构有问题。

## 参考

- [SpringBoot_权威教程_spring boot_springboot核心篇+springboot整合篇-_雷丰阳_尚硅谷_哔哩哔哩 (゜-゜)つロ 干杯~-bilibili](https://www.bilibili.com/video/BV1Et411Y7tQ?p=70)

- [实战|如何自定义SpringBoot Starter？](http://objcoding.com/2018/02/02/Costom-SpringBoot-Starter/)