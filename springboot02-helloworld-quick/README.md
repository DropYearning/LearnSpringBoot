#  LearnSpringBoot-01-HelloWorld

[SpringBoot_权威教程__哔哩哔哩 (゜-゜)つロ 干杯~-bilibili](https://www.bilibili.com/video/BV1Et411Y7tQ?p=4)

## 使用Spring Initializer快速创建Spring Boot项目

### 1、IDEA：使用 Spring Initializer快速创建项目
IDE都支持使用Spring的项目创建向导快速创建一个Spring Boot项目；

选择我们需要的模块；向导会联网创建Spring Boot项目；

默认生成的Spring Boot项目；

### 2、Spring Initializer项目的目录结构
- **主程序已经生成好了，我们只需要我们自己的逻辑**
- resources文件夹中目录结构
    - **static**：保存所有的静态资源； js\css\images；
    - **templates**：保存所有的模板页面；（Spring Boot默认jar包使用嵌入式的Tomcat，默认不支持JSP页面）；可以使用模板引擎（freemarker、thymeleaf）；
    - **application.properties**：Spring Boot应用的配置文件；可以修改一些默认设置；
        - 比如端口号

## 提示
- 可以在controller类上面使用**@ResponseBody**注解，表示这个类的所有方法返回的数据直接写给浏览器（如果是对象还可以转为json数据）
    - 这样就不用再在每一个方法上面写@ResponseBody注解了
    ```java
    @ResponseBody //这个类的所有方法返回的数据直接写给浏览器（如果是对象还可以转为json数据）
    @Controller
    public class HelloController {
        @RequestMapping("/hello")
        public String hello(){
            return "Hello World quickly!";
        }
    }
    ```
- 可以直接使用 **@RestController** 注解来替代@ResponseBody、@Controller这两个注解