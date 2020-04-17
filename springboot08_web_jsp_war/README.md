#  LearnSpringBoot-07-SpringBoot WEB开发CRUD案例

[SpringBoot_权威教程__哔哩哔哩 (゜-゜)つロ 干杯~-bilibili](https://www.bilibili.com/video/BV1Et411Y7tQ?p=4)

## 1 使用外置的Servlet容器
- 使用嵌入式Servlet容器的好处：应用打成可执行的`jar`包，简单、便携；缺点：默认不支持JSP、优化定制比较复杂（使用定制器【ServerProperties、自定义EmbeddedServletContainerCustomizer】，自己编写嵌入式Servlet容器的创建工厂）
   - JAR（Java Archive，Java 归档文件）是与平台无关的文件格式，它允许将许多文件组合成一个压缩文件。通常是开发时要引用通用类，打成jar包便于存放管理。
- 外置的Servlet容器：外面安装Tomcat等容器；应用使用`war`包的方式打包；
   - war包是一个可以直接运行的web模块。是做好一个web应用后，通常是网站，打成包部署到容器中。
- [jar包和war包的介绍和区别 - 简书](https://www.jianshu.com/p/3b5c45e8e5bd)
- 
   
### 1.1 使用外置的Servlet容器的步骤
- 1、使用SpringBoot Initializer创建一个打包形式为War的Web应用
    ```java
    public class ServletInitializer extends SpringBootServletInitializer {
        @Override
        protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
            return application.sources(Springboot08WebJspWarApplication.class);
        }
    }
    ```
- 2、创建项目需要用到的webapp文件夹Web Resource Directories
    - ![iLAWZW5](https://i.imgur.com/iLAWZW5.png)
- 3、生成WEB-INF和其下的web.xml配置文件
    - ![eDM2K3v](https://i.imgur.com/eDM2K3v.png)
- 4、编写相应的控制器和JSP文件
    ```java
    @Controller
    public class HelloController {
    
        @GetMapping("/abc")
        public String hello(Model model){
            model.addAttribute("msg", "你好");
            return "success";
        }
    }
    ```
- 5、在SpringBoot中配置MVC需要的视图解析器：
    ```properties
    # 配置视图解析器
    spring.mvc.view.prefix=/WEB-INF/
    spring.mvc.view.suffix=.jsp
    ```
- 6、配置Tomcat，启动运行

> 必须编写一个**SpringBootServletInitializer**的子类，并调用configure方法, 如果使用idea会为我们自动生成一个和SpringBootJspWarApplication同级的ServletInitializer类

> 使用外置Servlet容器之后不再能通过SpringBoot的启动方式启动；必须自己配置Tomcat等容器进行启动

### 1.2 使用外置Servlet容器的SpringBoot项目启动原理
- jar包：执行SpringBoot主类的main方法，启动ioc容器，创建嵌入式的Servlet容器；
- war包：启动自定义服务器，**由服务器启动SpringBoot应用**【SpringBootServletInitializer】，启动ioc容器；
- 流程：
    - 1）、启动Tomcat
    - 2）、org\springframework\spring-web\4.3.14.RELEASE\spring-web-4.3.14.RELEASE.jar!\META-INF\services\javax.servlet.ServletContainerInitializer：
        - Spring的web模块里面有这个文件：**org.springframework.web.SpringServletContainerInitializer**
    - 3）、SpringServletContainerInitializer将@HandlesTypes(WebApplicationInitializer.class)标注的所有这个类型的类都传入到onStartup方法的Set<Class<?>>；为这些WebApplicationInitializer类型的类创建实例；
    - 4）、每一个WebApplicationInitializer都调用自己的onStartup；
    - 5）、相当于我们的SpringBootServletInitializer的类会被创建对象，并执行onStartup方法
    - 6）、SpringBootServletInitializer实例执行onStartup的时候会createRootApplicationContext；创建容器
    - 7）、Spring的应用就启动并且创建IOC容器
- 总结：先启动Servlet容器，再启动SpringBoot应用

    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    