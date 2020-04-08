#  LearnSpringBoot-01-HelloWorld

[SpringBoot_权威教程__哔哩哔哩 (゜-゜)つロ 干杯~-bilibili](https://www.bilibili.com/video/BV1Et411Y7tQ?p=4)

## SpringBoot HelloWorld搭建步骤
一个功能：浏览器发送hello请求，服务器接受请求并处理，响应Hello World字符串；
### 1、创建一个maven工程；（jar）
### 2、导入spring boot相关的依赖

```xml
    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>1.5.9.RELEASE</version>
    </parent>
    <dependencies>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>
    </dependencies>
```
### 3、编写一个主程序；启动Spring Boot应用

```java
/**
 *  @SpringBootApplication 来标注一个主程序类，说明这是一个Spring Boot应用
 */
@SpringBootApplication
public class HelloWorldMainApplication {

    public static void main(String[] args) {

        // Spring应用启动起来
        SpringApplication.run(HelloWorldMainApplication.class,args);
    }
}
```

### 4、编写相关的Controller、Service

```java
@Controller
public class HelloController {

    @ResponseBody
    @RequestMapping("/hello")
    public String hello(){
        return "Hello World!";
    }
}

```

### 5、运行主程序测试
### 6、简化部署

```xml
 <!-- 这个插件，可以将应用打包成一个可执行的jar包；-->
    <build>
        <plugins>
            <plugin>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-maven-plugin</artifactId>
            </plugin>
        </plugins>
    </build>
```
将这个应用打成jar包，直接使用java -jar的命令进行执行；

## Hello World项目探究
### 1、POM文件
#### 1、父项目
```xml
<parent>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-parent</artifactId>
    <version>1.5.9.RELEASE</version>
</parent>

<!--他的父项目是-->
<parent>
  <groupId>org.springframework.boot</groupId>
  <artifactId>spring-boot-dependencies</artifactId>
  <version>1.5.9.RELEASE</version>
  <relativePath>../../spring-boot-dependencies</relativePath>
</parent>
<!--他来真正管理Spring Boot应用里面的所有依赖版本；-->
```

Spring Boot的版本仲裁中心；以后我们导入依赖默认是不需要写版本；（没有在dependencies里面管理的依赖自然需要声明版本号）

#### 2、启动器
```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
</dependency>
```

**spring-boot-starter**-web：spring-boot-starter：spring-boot场景启动器；帮我们导入了web模块正常运行所依赖的组件；

> Spring Boot将所有的功能场景都抽取出来，做成一个个的starters（启动器），只需要在项目里面引入这些starter相关场景的所有依赖都会导入进来。要用什么功能就导入什么场景的启动器

### 主程序类，主入口类

```java
/**
 *  @SpringBootApplication 来标注一个主程序类，说明这是一个Spring Boot应用
 */
@SpringBootApplication
public class HelloWorldMainApplication {

    public static void main(String[] args) {

        // Spring应用启动起来
        SpringApplication.run(HelloWorldMainApplication.class,args);
    }
}
```

- **@SpringBootApplication** :Spring Boot应用标注在某个类上说明这个类是SpringBoot的主配置类，SpringBoot就应该运行这个类的main方法来启动SpringBoot应用；
    - 查找源码可以发现@SpringBootApplication是一个组合注解：
    ```java
    @Target(ElementType.TYPE)
    @Retention(RetentionPolicy.RUNTIME)
    @Documented
    @Inherited
    @SpringBootConfiguration
    @EnableAutoConfiguration
    @ComponentScan(excludeFilters = {
          @Filter(type = FilterType.CUSTOM, classes = TypeExcludeFilter.class),
          @Filter(type = FilterType.CUSTOM, classes = AutoConfigurationExcludeFilter.class) })
    public @interface SpringBootApplication {
    ```
    - **@SpringBootConfiguration**:Spring Boot的配置类；标注在某个类上，表示这是一个Spring Boot的配置类；
         - **@Configuration**:配置类上来标注这个注解；
         - 配置类 等价于  配置文件；配置类也是容器中的一个组件，即@Component
    - @**EnableAutoConfiguration**：开启自动配置功能；以前我们需要配置的东西，Spring Boot帮我们自动配置；@**EnableAutoConfiguration**告诉SpringBoot开启自动配置功能；这样自动配置才能生效；
        ```java
        @AutoConfigurationPackage
        @Import(EnableAutoConfigurationImportSelector.class)
        public @interface EnableAutoConfiguration {
        ```
        - @**AutoConfigurationPackage**：自动配置包
        - @**Import**(AutoConfigurationPackages.Registrar.class)：Spring的底层注解@Import，给容器中导入一个组件；导入的组件由AutoConfigurationPackages.Registrar.class；
        - 将主配置类（@SpringBootApplication标注的类）的所在包及下面所有子包里面的所有组件扫描到Spring容器；
        - @**Import**(EnableAutoConfigurationImportSelector.class)；给容器中导入组件？**EnableAutoConfigurationImportSelector**：导入哪些组件的选择器；将所有需要导入的组件以全类名的方式返回；这些组件就会被添加到容器中；会给容器中导入非常多的自动配置类（xxxAutoConfiguration）；就是给容器中导入这个场景需要的所有组件，并配置好这些组件；有了自动配置类，免去了我们手动编写配置注入功能组件等的工作；
        - SpringFactoriesLoader.loadFactoryNames(EnableAutoConfiguration.class,classLoader)；
        - Spring Boot在启动的时候从类路径下的`META-INF/spring.factories`中获取EnableAutoConfiguration指定的值，将这些值作为自动配置类导入到容器中，自动配置类就生效，帮我们进行自动配置工作；以前我们需要自己配置的东西，自动配置类都帮我们做好了；
        - J2EE的整体整合解决方案和自动配置都在`·`spring-boot-autoconfigure.RELEASE.jar`
     








