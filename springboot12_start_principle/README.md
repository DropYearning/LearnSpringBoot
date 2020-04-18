#  LearnSpringBoot-07-SpringBoot-启动配置原理（针对SpringBoot 1.x）

[SpringBoot_权威教程__哔哩哔哩 (゜-゜)つロ 干杯~-bilibili](https://www.bilibili.com/video/BV1Et411Y7tQ?p=4)

## 1 启动运行流程

### 1.1 创建SpringApplication对象
1. 保存主配置类
2. 判断当前是否一个web应用
3. 从类路径下找到META-INF/spring.factories配置的所有ApplicationContextInitializer；然后保存起来
4. 从类路径下找到ETA-INF/spring.factories配置的所有ApplicationListener
5. 从多个配置类中找到有main方法的主配置类

    ```java
    initialize(sources);
    private void initialize(Object[] sources) {
        //保存主配置类
        if (sources != null && sources.length > 0) {
            this.sources.addAll(Arrays.asList(sources));
        }
        //判断当前是否一个web应用
        this.webEnvironment = deduceWebEnvironment();
        //从类路径下找到META-INF/spring.factories配置的所有ApplicationContextInitializer；然后保存起来
        setInitializers((Collection) getSpringFactoriesInstances(
            ApplicationContextInitializer.class));
        //从类路径下找到ETA-INF/spring.factories配置的所有ApplicationListener
        setListeners((Collection) getSpringFactoriesInstances(ApplicationListener.class));
        //从多个配置类中找到有main方法的主配置类
        this.mainApplicationClass = deduceMainApplicationClass();
    }
    ```
### 1.2  运行run方法
1. 获取SpringApplicationRunListeners；从类路径下META-INF/spring.factories
2. 回调所有的获取SpringApplicationRunListener.starting()方法
3. 准备环境ConfigurableEnvironment
4. 创建环境完成后回调SpringApplicationRunListener.environmentPrepared()；表示环境准备完成
5. 创建ApplicationContext；决定创建web的ioc还是普通的ioc
6. 准备上下文环境;将environment保存到ioc中；而且applyInitializers()；
7. 回调所有的SpringApplicationRunListener的contextPrepared()；
8. prepareContext运行完成以后回调所有的SpringApplicationRunListener的contextLoaded（）；
9. 刷新容器；ioc容器初始化（如果是web应用还会创建嵌入式的Tomcat）；Spring注解版
10. 扫描，创建，加载所有组件的地方；（配置类，组件，自动配置）
11. 从ioc容器中获取所有的ApplicationRunner和CommandLineRunner进行回调
12. ApplicationRunner先回调，CommandLineRunner再回调
13. 所有的SpringApplicationRunListener回调finished方法
14. 整个SpringBoot应用启动完成以后返回启动的ioc容器；

    ```java
    public ConfigurableApplicationContext run(String... args) {
       StopWatch stopWatch = new StopWatch();
       stopWatch.start();
       ConfigurableApplicationContext context = null;
       FailureAnalyzers analyzers = null;
       configureHeadlessProperty();
        
       //获取SpringApplicationRunListeners；从类路径下META-INF/spring.factories
       SpringApplicationRunListeners listeners = getRunListeners(args);
        //回调所有的获取SpringApplicationRunListener.starting()方法
       listeners.starting();
       try {
           //封装命令行参数
          ApplicationArguments applicationArguments = new DefaultApplicationArguments(
                args);
          //准备环境
          ConfigurableEnvironment environment = prepareEnvironment(listeners,
                applicationArguments);
                //创建环境完成后回调SpringApplicationRunListener.environmentPrepared()；表示环境准备完成
           
          Banner printedBanner = printBanner(environment);
           
           //创建ApplicationContext；决定创建web的ioc还是普通的ioc
          context = createApplicationContext();
           
          analyzers = new FailureAnalyzers(context);
           //准备上下文环境;将environment保存到ioc中；而且applyInitializers()；
           //applyInitializers()：回调之前保存的所有的ApplicationContextInitializer的initialize方法
           //回调所有的SpringApplicationRunListener的contextPrepared()；
           //
          prepareContext(context, environment, listeners, applicationArguments,
                printedBanner);
           //prepareContext运行完成以后回调所有的SpringApplicationRunListener的contextLoaded（）；
           
           //刷新容器；ioc容器初始化（如果是web应用还会创建嵌入式的Tomcat）；Spring注解版
           //扫描，创建，加载所有组件的地方；（配置类，组件，自动配置）
          refreshContext(context);
           //从ioc容器中获取所有的ApplicationRunner和CommandLineRunner进行回调
           //ApplicationRunner先回调，CommandLineRunner再回调
          afterRefresh(context, applicationArguments);
           //所有的SpringApplicationRunListener回调finished方法
          listeners.finished(context, null);
          stopWatch.stop();
          if (this.logStartupInfo) {
             new StartupInfoLogger(this.mainApplicationClass)
                   .logStarted(getApplicationLog(), stopWatch);
          }
           //整个SpringBoot应用启动完成以后返回启动的ioc容器；
          return context;
       }
       catch (Throwable ex) {
          handleRunFailure(context, listeners, analyzers, ex);
          throw new IllegalStateException(ex);
       }
    }
    ```
### 1.3 事件监听机制
- 我们在开发中可能会有这样的情景。需要在容器启动的时候执行一些内容。比如读取配置文件，数据库连接之类的。SpringBoot给我们提供了两个接口来帮助我们实现这种需求。这两个接口分别为`CommandLineRunner`和`ApplicationRunner`。他们的执行时机为容器启动完成的时候。
- 这两个接口中有一个run方法，我们只需要实现这个方法即可。这两个接口的不同之处在于：ApplicationRunner中run方法的参数为ApplicationArguments，而CommandLineRunner接口中run方法的参数为String数组。
- 实现步骤：
    - 配置在META-INF/spring.factories
    - 编写自定义类实现ApplicationContextInitializer或者SpringApplicationRunListener接口
    - **将实现ApplicationContextInitializer或者SpringApplicationRunListener的自定义类配置在/resources/META-INF/spring.factory**
        ```properties
        org.springframework.context.ApplicationContextInitializer=com.example.springboot12.listener.HelloApplicationContextInitializer
        org.springframework.boot.SpringApplicationRunListener=com.example.springboot12.listener.HelloSpringApplicationRunListener
        ```
    - 编写响应的Runner类实现ApplicationRunner和CommandLineRunner接口
        - CommandLineRunner、ApplicationRunner 接口是在容器启动成功后的最后一步回调（类似开机自启动）。
    - **将ApplicationRunner和CommandLineRunner的实现类注入容器即可(@Component)**
> 如果有多个实现类，而你需要他们按一定顺序执行的话，可以在实现类上加上@Order注解。@Order(value=整数值)。SpringBoot会按照@Order中的value值从小到大依次执行。


#### 1.3.1 ApplicationContextInitializer

```java
public class HelloApplicationContextInitializer implements ApplicationContextInitializer {
    @Override
    public void initialize(ConfigurableApplicationContext configurableApplicationContext) {
        System.out.println("ApplicationContextInitializer initialize运行了...");
        System.out.println(configurableApplicationContext);
    }
}

```

#### 1.3.2 SpringApplicationRunListener
```java
public class HelloSpringApplicationRunListener implements SpringApplicationRunListener {
    public HelloSpringApplicationRunListener(SpringApplication application, String[] args) {

    }

    @Override
    public void starting() {
        System.out.println("SpringApplicationRunListener...starting..." );
    }

    @Override
    public void environmentPrepared(ConfigurableEnvironment environment) {
        Object o = environment.getSystemProperties().get("os.name");
        System.out.println("SpringApplicationRunListener...starting...");
        System.out.println(environment);
        System.out.println(o);
    }

    @Override
    public void contextPrepared(ConfigurableApplicationContext context) {
        System.out.println("SpringApplicationRunListener...contextPrepared...");
    }

    @Override
    public void contextLoaded(ConfigurableApplicationContext context) {
        System.out.println("SpringApplicationRunListener...contextLoaded...");
    }

    @Override
    public void started(ConfigurableApplicationContext context) {
        System.out.println("SpringApplicationRunListener...contextLoaded...");
    }

    @Override
    public void running(ConfigurableApplicationContext context) {
        System.out.println("SpringApplicationRunListener...running...");
    }

    @Override
    public void failed(ConfigurableApplicationContext context, Throwable exception) {
        System.out.println("SpringApplicationRunListener...failed...");
    }
}
```
#### 1.3.3 HelloCommandLineRunner
```java
@Component
public class HelloCommandLineRunner implements CommandLineRunner {
    @Override
    public void run(String... args) throws Exception {
        System.out.println("HelloCommandLineRunner..run..." + Arrays.asList(args));
    }
}
```

#### 1.3.4 HelloApplicationRunner
```java
@Component
public class HelloApplicationRunner implements ApplicationRunner {
    @Override
    public void run(ApplicationArguments args) throws Exception {
        System.out.println("ApplicationRunner..run..." + Arrays.asList(args));

    }
}
```

#### 1.3.5 启动结果
```
SpringApplicationRunListener...starting...
SpringApplicationRunListener...environmentPrepared...
StandardServletEnvironment {activeProfiles=[], defaultProfiles=[default], propertySources=[ConfigurationPropertySourcesPropertySource {name='configurationProperties'}, StubPropertySource {name='servletConfigInitParams'}, StubPropertySource {name='servletContextInitParams'}, PropertiesPropertySource {name='systemProperties'}, OriginAwareSystemEnvironmentPropertySource {name='systemEnvironment'}, RandomValuePropertySource {name='random'}]}
Mac OS X
SpringApplicationRunListener...starting...
SpringApplicationRunListener...environmentPrepared...
StandardServletEnvironment {activeProfiles=[], defaultProfiles=[default], propertySources=[ConfigurationPropertySourcesPropertySource {name='configurationProperties'}, StubPropertySource {name='servletConfigInitParams'}, StubPropertySource {name='servletContextInitParams'}, PropertiesPropertySource {name='systemProperties'}, OriginAwareSystemEnvironmentPropertySource {name='systemEnvironment'}, RandomValuePropertySource {name='random'}]}
Mac OS X

  .   ____          _            __ _ _
 /\\ / ___'_ __ _ _(_)_ __  __ _ \ \ \ \
( ( )\___ | '_ | '_| | '_ \/ _` | \ \ \ \
 \\/  ___)| |_)| | | | | || (_| |  ) ) ) )
  '  |____| .__|_| |_|_| |_\__, | / / / /
 =========|_|==============|___/=/_/_/_/
 :: Spring Boot ::        (v2.2.6.RELEASE)

ApplicationContextInitializer initialize运行了...
org.springframework.boot.web.servlet.context.AnnotationConfigServletWebServerApplicationContext@53ce1329, started on Thu Jan 01 08:00:00 CST 1970
SpringApplicationRunListener...contextPrepared...
2020-04-18 20:57:48.728  INFO 13638 --- [           main] .s.Springboot12StartPrincipleApplication : Starting Springboot12StartPrincipleApplication on DESKTOP-MEJNDAC.local with PID 13638 (/Users/brightzh/WorkSpace/LearnSpringBoot/springboot12_start_principle/target/classes started by brightzh in /Users/brightzh/WorkSpace/LearnSpringBoot/springboot12_start_principle)
2020-04-18 20:57:48.730  INFO 13638 --- [           main] .s.Springboot12StartPrincipleApplication : No active profile set, falling back to default profiles: default
SpringApplicationRunListener...contextLoaded...
2020-04-18 20:57:49.454  INFO 13638 --- [           main] o.s.b.w.embedded.tomcat.TomcatWebServer  : Tomcat initialized with port(s): 8080 (http)
2020-04-18 20:57:49.461  INFO 13638 --- [           main] o.apache.catalina.core.StandardService   : Starting service [Tomcat]
2020-04-18 20:57:49.462  INFO 13638 --- [           main] org.apache.catalina.core.StandardEngine  : Starting Servlet engine: [Apache Tomcat/9.0.33]
2020-04-18 20:57:49.515  INFO 13638 --- [           main] o.a.c.c.C.[Tomcat].[localhost].[/]       : Initializing Spring embedded WebApplicationContext
2020-04-18 20:57:49.515  INFO 13638 --- [           main] o.s.web.context.ContextLoader            : Root WebApplicationContext: initialization completed in 745 ms
2020-04-18 20:57:49.647  INFO 13638 --- [           main] o.s.s.concurrent.ThreadPoolTaskExecutor  : Initializing ExecutorService 'applicationTaskExecutor'
2020-04-18 20:57:49.763  INFO 13638 --- [           main] o.s.b.w.embedded.tomcat.TomcatWebServer  : Tomcat started on port(s): 8080 (http) with context path ''
2020-04-18 20:57:49.767  INFO 13638 --- [           main] .s.Springboot12StartPrincipleApplication : Started Springboot12StartPrincipleApplication in 1.276 seconds (JVM running for 2.007)
SpringApplicationRunListener...started...
ApplicationRunner..run...[org.springframework.boot.DefaultApplicationArguments@1abfe081]
HelloCommandLineRunner..run...[]
SpringApplicationRunListener...running...
2020-04-18 20:59:11.919  INFO 13638 --- [extShutdownHook] o.s.s.concurrent.ThreadPoolTaskExecutor  : Shutting down ExecutorService 'applicationTaskExecutor'
```

## 参考资料
- [回调函数（callback）是什么？ - 知乎](https://www.zhihu.com/question/19801131)