#  LearnSpringBoot-07-SpringBoot-数据访问-JDBC

[SpringBoot_权威教程__哔哩哔哩 (゜-゜)つロ 干杯~-bilibili](https://www.bilibili.com/video/BV1Et411Y7tQ?p=4)

## 1 JDBC
    ```xml
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-jdbc</artifactId>
    </dependency>
    <dependency>
        <groupId>mysql</groupId>
        <artifactId>mysql-connector-java</artifactId>
        <scope>runtime</scope>
    </dependency>
    ```
### 1.1 JDBC自动配置原理（SpringBoot 1.x版本）
- 在`org.springframework.boot.autoconfigure.jdbc`包下
- 1、参考DataSourceConfiguration，根据配置创建数据源，**默认使用Tomcat连接池（SpringBoot 1.x版本）**；可以使用spring.datasource.type指定自定义的数据源类型；
- 2、SpringBoot默认可以支持：org.apache.tomcat.jdbc.pool.DataSource、**HikariDataSource（SpringBoot 2.x 版本默认）**、BasicDataSource
- 3、自定义数据源DataSource类型

    ```java
    /**
     * Generic DataSource configuration.
     */
    @ConditionalOnMissingBean(DataSource.class)
    @ConditionalOnProperty(name = "spring.datasource.type")
    static class Generic {
    
       @Bean
       public DataSource dataSource(DataSourceProperties properties) {
           //使用DataSourceBuilder创建数据源，利用反射创建响应type的数据源，并且绑定相关属性
          return properties.initializeDataSourceBuilder().build();
       }
    
    }
    ```
- 4、**DataSourceInitializer（实际上是一个ApplicationListener）**；
    - 作用：
        ​- 1）、runSchemaScripts():运行建表语句；
        - 2）、runDataScripts():运行插入数据的sql语句；
    - 在初始化时运行SQL文件，只需要将文件命名为：schema.sql(建表)、data.sql(插入数据)并放在类路径下即可
    - 也可以在主配置文件中指定启动是要加载的SQL文件：
        ```yaml
        spring:
          datasource:
            username: root
            password: 123456
            url: jdbc:mysql://127.0.0.1:3307/jdbc?serverTimezone=UTC
            driver-class-name: com.mysql.cj.jdbc.Driver
            initialization-mode: always
            schema:
              - classpath:department.sql
              - classpath:employee.sql
        ```
    > SpringBoot2.x需要在主配置文件中设置：initialization-mode: always
- 5、操作数据库：自动配置了 JdbcTemplate 操作数据库
            
## 2 整合Druid数据源
- 

## 参考资料
- [SpringBoot配置JDBC连接MySql数据库的时候遇到了报错：HikariPool-1 - Exception during pool initialization - 还可入梦 - 博客园](https://www.cnblogs.com/stilldream/p/11284187.html)
- [SpringBoot启动报错：HikariPool-1 - Exception during pool initialization._Java_Charon博客站-CSDN博客](https://blog.csdn.net/qq_34035160/article/details/82841020)
- [【JAVA】JAVA数据源_Java_xueba8的博客-CSDN博客](https://blog.csdn.net/xueba8/article/details/84107204)
- 










