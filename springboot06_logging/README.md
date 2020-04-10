#  LearnSpringBoot-06-SpringBoot日志配置

[SpringBoot_权威教程__哔哩哔哩 (゜-゜)つロ 干杯~-bilibili](https://www.bilibili.com/video/BV1Et411Y7tQ?p=4)

## 1 日志框架介绍
**市面上的日志框架：JUL、JCL、Jboss-logging、logback、log4j、log4j2、slf4j....

可以按日志门面和日志实现分为下面这些：

| 日志门面（日志的抽象层）       | 日志实现    |           
| --------------------- | ---------------- |
|  ~~JCL（Jakarta  Commons Logging）~~     SLF4j（Simple  Logging Facade for Java）    **~~jboss-logging~~**  | Log4j  JUL（java.util.logging）  Log4j2  **Logback** |


- SpringBoot：底层是Spring框架，Spring框架默认是用JCL
- 使用SLF4J作为日志门面 + Logback作为日志实现来演示。

## 2 使用slf4j日志门面
### 2.1 如何在系统中使用SLF4j
- ![PUlfyyU](https://i.imgur.com/PUlfyyU.png)
- [SLF4J](http://www.slf4j.org/)
- **实际开发的时候，应该调用日志抽象层里面的方法（例如SLF4j），而不是调用日志的实现类(Logback)的方法**
- 每一个日志的实现框架都有自己的配置文件。使用slf4j以后，**配置文件还是使用日志实现的配置文件；**
- 给系统里面导入slf4j的jar和logback的实现jar
    ```java
    import org.slf4j.Logger;
    import org.slf4j.LoggerFactory;
    
    public class HelloWorld {
      public static void main(String[] args) {
        Logger logger = LoggerFactory.getLogger(HelloWorld.class);
        logger.info("Hello World");
      }
    }
    ```
### 2.2 解决问题：项目中的不同框架使用了不同的日志实现的造成的兼容问题
- ![cYbYulN](https://i.imgur.com/cYbYulN.png)
- 例如：项目A（采用slf4j+logback）: Spring（使用commons-logging）、Hibernate（使用jboss-logging），如何统一使用slf4j+logback记录日志？
    - 将系统中其他日志框架先排除出去；
    - 用中间包来替换原有的日志框架（见上图）
    - 我们导入slf4j其他的实现


### 2.3 SpringBoot的日志关系
```xml
<dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-logging</artifactId>
</dependency>
```
- SpringBoot的底层日志依赖关系
    - ![xVE6ZGv](https://i.imgur.com/xVE6ZGv.png)
    - **SpringBoot底层也是使用slf4j+logback的方式进行日志记录**
    - SpringBoot也考虑其他日志的适配，因此使用中间包将其他日志都替换成了slf4j；
    - 中间替换包，以jcl-over-slf4j为例：里面的包名、类名和方法名都与jcl一致，在底层替换了其中的实例化方法等
    - 如果我们要在SpringBoot中引入其他框架，一定要把这个框架的默认日志依赖jar包移除掉，替换上中间包。
    - **Spring框架用的是commons-logging日志框架**，但是SpringBoot在导入Spring依赖的时候exclude掉了Spring本身需要的commons-logging依赖
        ```xml
            <dependency>
                <groupId>org.springframework</groupId>
                <artifactId>spring-core</artifactId>
                <exclusions>
                    <exclusion>
                        <groupId>commons-logging</groupId>
                        <artifactId>commons-logging</artifactId>
                    </exclusion>
                </exclusions>
            </dependency>
        ```
    - **SpringBoot能自动适配所有的日志，而且底层使用slf4j+logback的方式记录日志，引入其他框架的时候，只需要把这个框架依赖的日志框架排除掉即可**


## 3 日志相关设置
### 3.1 日志级别
- 由低到高：trace < debug < info < warn < error
- **SpringBoot模式输出的info级别以上的日志（root级别）**
- 可以调整需要输出的日志级别：在配置文件中添加`logging.level.com.example=trace`可以将com.example包下输出的日志调整成trace级别
### 3.2 输出路径

|**logging.file** |**logging.path** |**Example** | **Description**|
|------|------|------|-------|
|(none) |(none)｜  | 只在控制台输出|
|指定文件名 | (none)| my.log | 输出日志到当前目录下的my.log文件 |
|(none) | 指定目录 | /var/log  | 输出到指定目录下的spring.log文件中 |



### 3.3 日志输出格式
- `logging.pattern.console` 指定在控制台输出的日志的格式
    - `logging.pattern.console=%d{yyyy-MM-dd} [%thread] %-5level %logger{50} - %msg%n`
- `logging.pattern.file` 指定在文件中输入日志的格式
    - `logging.pattern.file=%d{yyyy-MM-dd} === [%thread] === %-5level === %logger{50} ==== %msg%n`
- 日志输出格式：
    - %d表示日期时间，
    - %thread表示线程名，
    - %-5level：级别从左显示5个字符宽度
    - %logger{50} 表示logger名字最长50个字符，否则按照句点分割。 
    - %msg：日志消息，
    - %n是换行符

### 3.4 修改SpringBoot默认日志配置
- 在类路径(resources/)下放上每个日志框架自己的配置文件即可；SpringBoot就不使用他默认配置的了
 
    | Logging System 使用的日志系统         | Customization 自定义配置文件名                                                |
    | ----------------------- | ------------------------------------------------------------ |
    | Logback                 | `logback-spring.xml`, `logback-spring.groovy`, `logback.xml` or `logback.groovy` |
    | Log4j2                  | `log4j2-spring.xml` or `log4j2.xml`                          |
    | JDK (Java Util Logging) | `logging.properties`                              |

- 推荐使用`logback-spring.xml`作为日志配置文件名, 这样日志框架就不直接加载日志的配置项，而是由SpringBoot解析日志配置，**可以使用SpringBoot的高级Profile功能**（使某一段配置仅在某个小环境下生效）
    ```xml
    <!--可以指定某段配置只在某个环境下生效-->
    <!--开发环境日志配置-->
    <springProfile name="dev">
        <!-- configuration to be enabled when the "dev" profile is active -->
    </springProfile>
    <!--非开发环境日志配置-->
    <springProfile name="!dev">
          <!-- configuration to be enabled when the "dev" profile is active -->
    </springProfile>
    ```

## 4 切换日志框架
- 如何从slf4g+logback日志环境切换到slf4j+log4j？
- 根据下图修改POM.XML：
    - ![cYbYulN](https://i.imgur.com/cYbYulN.png)
- slf4j+log4j的方式：   
    ```xml
    <dependency>
      <groupId>org.springframework.boot</groupId>
      <artifactId>spring-boot-starter-web</artifactId>
      <exclusions>
        <exclusion>
          <artifactId>logback-classic</artifactId>
          <groupId>ch.qos.logback</groupId>
        </exclusion>
        <exclusion>
          <artifactId>log4j-over-slf4j</artifactId>
          <groupId>org.slf4j</groupId>
        </exclusion>
      </exclusions>
    </dependency>
    
    <dependency>
      <groupId>org.slf4j</groupId>
      <artifactId>slf4j-log4j12</artifactId>
    </dependency>
    ```
- slf4j+log4j2的方式：
    ```xml
     <dependency>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-starter-web</artifactId>
                <exclusions>
                    <exclusion>
                        <artifactId>spring-boot-starter-logging</artifactId>
                        <groupId>org.springframework.boot</groupId>
                    </exclusion>
                </exclusions>
            </dependency>
    
    <dependency>
      <groupId>org.springframework.boot</groupId>
      <artifactId>spring-boot-starter-log4j2</artifactId>
    </dependency>
    ```





















