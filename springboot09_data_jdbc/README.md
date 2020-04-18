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
            
## 2 整合Druid数据源和相关监控
- **Druid**是一个关系型数据库连接池，它是阿里巴巴的一个开源项目。Druid支持所有JDBC兼容数据库，包括了Oracle、MySQL、PostgreSQL、SQL Server、H2等。Druid在监控、可扩展性、稳定性和性能方面具有明显的优势。通过Druid提供的监控功能，可以实时观察数据库连接池和SQL查询的工作情况。使用Druid连接池在一定程度上可以提高数据访问效率。
- 1、引入Druid的Maven依赖（需要log4j支持）
- 2、编写配置类用于导入application.properties中的配置项目和监控设置
    ```java
   // 导入druid数据源的配置类
   @Configuration
   public class DruidConfig {  
       @Bean
       @ConfigurationProperties(prefix = "spring.datasource")
       public DataSource druid(){
           return new DruidDataSource();
       }  
       // 配置Druid监控
       // 1、配置一个管理后台的Servlet
       @Bean
       public ServletRegistrationBean statViewServlet(){
           ServletRegistrationBean servletRegistrationBean = new ServletRegistrationBean(new StatViewServlet(), "/druid/*"); // 注意druid的映射路径/druid/*
           Map<String, String> initParams = new HashMap<>(); // 配置管理druid后台的Servlet
           initParams.put("loginUsername", "admin");
           initParams.put("loginPassword", "123456");
           initParams.put("allow", ""); // 默认允许所有
           servletRegistrationBean.setInitParameters(initParams);
           return servletRegistrationBean;
       }
       // 2、配置一个监控的filter
       @Bean
       public FilterRegistrationBean webStatFilter(){
           FilterRegistrationBean filterRegistrationBean = new FilterRegistrationBean();
           filterRegistrationBean.setFilter(new WebStatFilter());
           Map<String, String> initParams = new HashMap<>();
           initParams.put("exclusions", "*.js, *.css, /druid/*"); // 设置拦截器的排除项
           filterRegistrationBean.setInitParameters(initParams);
           filterRegistrationBean.setUrlPatterns(Arrays.asList("/*"));
           return filterRegistrationBean;
       }
   }
    ```
- 3、在application.properties配置druid
    ```properties
    spring:
      datasource:
        username: root
        password: 123456
        url: jdbc:mysql://127.0.0.1:3307/jdbc?serverTimezone=UTC
        driver-class-name: com.mysql.cj.jdbc.Driver
        initialization-mode: always # 设置SpringBoot 2.x启动时加载SQL必须打开此项
        type: com.alibaba.druid.pool.DruidDataSource # 切换数据源为druid
        # 数据源其他配置（黄色表示下面的这些项目并不能自动导入，需要在配置类中引入，见DruidConfig）
        initialSize: 5
        minIdle: 5
        maxActive: 20
        maxWait: 60000
        timeBetweenEvictionRunsMillis: 60000
        minEvictableIdleTimeMillis: 300000
        validationQuery: SELECT 1 FROM DUAL
        testWhileIdle: true
        testOnBorrow: false
        testOnReturn: false
        poolPreparedStatements: true
        #   配置监控统计拦截的filters，去掉后监控界面sql无法统计，'wall'用于防火墙
        filters: stat,wall,log4j
        maxPoolPreparedStatementPerConnectionSize: 20
        useGlobalDataSourceStat: true
        connectionProperties: druid.stat.mergeSql=true;druid.stat.slowSqlMillis=500
    ```
- 4、在测试类中断点调试看是否引入配置
    - ![6Gtc8xH](https://i.imgur.com/6Gtc8xH.png)
    - ![XFOlCCP](https://i.imgur.com/XFOlCCP.png)
- 5、启动tomcat，进入druid后台
    - ![5ZhObJQ](https://i.imgur.com/5ZhObJQ.png)





## 参考资料
- [SpringBoot配置JDBC连接MySql数据库的时候遇到了报错：HikariPool-1 - Exception during pool initialization - 还可入梦 - 博客园](https://www.cnblogs.com/stilldream/p/11284187.html)
- [SpringBoot启动报错：HikariPool-1 - Exception during pool initialization._Java_Charon博客站-CSDN博客](https://blog.csdn.net/qq_34035160/article/details/82841020)
- [【JAVA】JAVA数据源_Java_xueba8的博客-CSDN博客](https://blog.csdn.net/xueba8/article/details/84107204)
- [alibaba/druid: 阿里巴巴数据库事业部出品，为监控而生的数据库连接池](https://github.com/alibaba/druid)










