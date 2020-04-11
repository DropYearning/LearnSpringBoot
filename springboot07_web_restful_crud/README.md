#  LearnSpringBoot-07-SpringBoot WEB开发CRUD案例

[SpringBoot_权威教程__哔哩哔哩 (゜-゜)つロ 干杯~-bilibili](https://www.bilibili.com/video/BV1Et411Y7tQ?p=4)

## 1 简介
使用SpringBoot开发web项目：
1) 创建SpringBoot应用，选中我们需要的模块
2) SpringBoot已经默认将这些场景配置好了，只需要在配置文件中指定少量配置就可以运行起来
3) 自己编写业务代码

- 自动配置原理：
    - xxxAutoConfiguration：帮我们给容器中自动配置组件；
    - xxxProperties: 配置类来封装配置文件的内容；

## 2 导入web应用的静态资源
### 2.1 webjars
- 所有访问`/webjars/**` 需要的类型资源都去该webjar包的`classpath:/META-INF/resources/webjars/` 找资源
    - webjars：以jar包的方式引入静态资源。参考[WebJars - Web Libraries in Jars](https://www.webjars.org/)，将需要的各种web依赖（jquery \ Bootstrap等）以webjar的形式导入
    - 例如引入jquery,
        ```xml
        <!--引入jquery的webjars-->
        <dependency>
            <groupId>org.webjars</groupId>
            <artifactId>jquery</artifactId>
            <version>3.4.1</version>
        </dependency>
        ```
    - 可以通过url`http://localhost:8080/webjars/jquery/3.4.1/jquery.js`访问jquery.js：
    
### 2.2 静态资源文件夹
- 访问当前项目的任何资源，SpringBoot都会去**静态资源文件夹**找映射.下面几个文件夹称为静态资源文件夹：
    ```xml
    "classpath:/META-INF/resources/", 
    "classpath:/resources/",
    "classpath:/static/", 
    "classpath:/public/" 
    "/"：当前项目的根路径
    ```
- ![GPoaZkp](https://i.imgur.com/GPoaZkp.jpg)
    - 例如访问`localhost:8080/abc`，若没有被处理，则默认去静态资源文件下找abc这个资源
    - 例如，可以通过url`http://localhost:8080/asserts/js/Chart.min.js`访问静态文件下对于的文件。
    - 可能需要在Maven中选择`Generate Sources an Update Folderss For All Projects`来刷新资源，将资源文件Generate到targer文件夹
    - ![NhU18rZ](https://i.imgur.com/NhU18rZ.png)
- 对应源码：
```java
	@WebMvcAuotConfiguration
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        if (!this.resourceProperties.isAddMappings()) {
            logger.debug("Default resource handling disabled");
            return;
        }
        Integer cachePeriod = this.resourceProperties.getCachePeriod();
        if (!registry.hasMappingForPattern("/webjars/**")) {
            customizeResourceHandlerRegistration(
                    registry.addResourceHandler("/webjars/**")
                            .addResourceLocations(
                                    "classpath:/META-INF/resources/webjars/")
                    .setCachePeriod(cachePeriod));
        }
        String staticPathPattern = this.mvcProperties.getStaticPathPattern();
        //静态资源文件夹映射
        if (!registry.hasMappingForPattern(staticPathPattern)) {
            customizeResourceHandlerRegistration(
                    registry.addResourceHandler(staticPathPattern)
                            .addResourceLocations(
                                    this.resourceProperties.getStaticLocations())
                    .setCachePeriod(cachePeriod));
        }
    }
```


### 2.3 欢迎页
    ```java
    //配置欢迎页映射
    @Bean
    public WelcomePageHandlerMapping welcomePageHandlerMapping(
            ResourceProperties resourceProperties) {
        return new WelcomePageHandlerMapping(resourceProperties.getWelcomePage(),
                this.mvcProperties.getStaticPathPattern());
    }
    ```
- 欢迎页: 静态资源文件夹下的所有`index.html`页面；被"/**"映射
- 比如访问:`localhost:8080`会默认寻找静态文件夹下的index.html

### 2.4 图标
- 默认使用`**/favicon.ico `
- 可能需要强制刷新chrome标签：`crtl+F5`或者`ctrl+shift+R`

### 2.5 改变默认的静态文件夹路径
- 在配置文件中添加:`spring.resources.static-locations=classpath://hello/, classpath://me/`
    - 可以使用逗号分隔，设置多个静态资源路径

## 3 使用模版引擎
![Ohrk2lw](https://i.imgur.com/Ohrk2lw.jpg)
- 模版引擎有：JSP, Velocity, Freemarker, Thymeleaf等
- **模板引擎**的作用：将模板和数据交给模板引擎，模版引擎可以解析表达式填充上对应的数据组装成最终的HTML网页。
- **SpringBoot推荐使用Thymeleaf、Freemarker等后现代的模板引擎技术**；一但导入相关依赖，会自动配置ThymeleafAutoConfiguration、FreeMarkerAutoConfiguration。

### 3.1 Thymeleaf介绍
- 特点：
  * 动静结合：Thymeleaf 在有网络和无网络的环境下皆可运行，即它可以让美工在浏览器查看页面的静态效果，也可以让程序员在服务器查看带数据的动态页面效果。这是由于它支持 html 原型，然后在 html 标签里增加额外的属性来达到模板+数据的展示方式。浏览器解释 html 时会忽略未定义的标签属性，所以 thymeleaf 的模板可以静态地运行；当有数据返回到页面时，Thymeleaf 标签会动态地替换掉静态内容，使页面动态显示。
  * 开箱即用：它提供标准和spring标准两种方言，可以直接套用模板实现JSTL、 OGNL表达式效果，避免每天套模板、该jstl、改标签的困扰。同时开发人员也可以扩展和创建自定义的方言。
  * 多方言支持：Thymeleaf 提供spring标准方言和一个与 SpringMVC 完美集成的可选模块，可以快速的实现表单绑定、属性编辑器、国际化等功能。
  * 与SpringBoot完美整合，SpringBoot提供了Thymeleaf的默认配置，并且为Thymeleaf设置了视图解析器，我们可以像以前操作jsp一样来操作Thymeleaf。代码几乎没有任何区别，就是在模板语法上有区别。
- 使用spring-boot-stater引入Thymeleaf:
    ```xml
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-thymeleaf</artifactId>
        </dependency>
        <!--切换thymeleaf版本-->
        <properties>
                <thymeleaf.version>3.0.9.RELEASE</thymeleaf.version>
                <thymeleaf-layout-dialect.version>2.2.2</thymeleaf-layout-dialect.version>
        </properties>
    ```
### 3.2 Thymeleaf的使用
```java
@ConfigurationProperties(prefix = "spring.thymeleaf")
public class ThymeleafProperties {

	private static final Charset DEFAULT_ENCODING = Charset.forName("UTF-8");

	private static final MimeType DEFAULT_CONTENT_TYPE = MimeType.valueOf("text/html");

	public static final String DEFAULT_PREFIX = "classpath:/templates/";

	public static final String DEFAULT_SUFFIX = ".html";
```
- 根据上述配置类，只要把HTML页面放在`classpath:/templates/`路径下，thymeleaf就能自动渲染
- 使用步骤：
    1. 在html页面文件顶部导入thymeleaf的名称空间:`<html lang="en" xmlns:th="http://www.thymeleaf.org">`
    2. 可以在html页面中使用thymeleaf语法
    
    
### 3.3 Thymeleaf的语法
#### th
![zWt4Hm3](https://i.imgur.com/zWt4Hm3.png)
- `th:text`：改变当前元素里面的文本内容；
- `th：任意html属性`：来替换原生属性的值。例如`th:id="${hello}"`写在div标签内可以替换div标签的id属性为${hello}的值

#### 表达式语法
- Variable Expressions: ${...}：获取变量值, OGNL表达式
    - 1）、获取对象的属性、调用方法
    - 2）、使用内置的基本对象：
        - `#ctx` : the context object.
        - `#vars`: the context variables.
        - `#locale` : the context locale.
        - `#request` : (only in Web Contexts) the HttpServletRequest object.
        - `#response` : (only in Web Contexts) the HttpServletResponse object.
        - `#session` : (only in Web Contexts) the HttpSession object.
        - `#servletContext` : (only in Web Contexts) the ServletContext object.
            - ${session.foo}
    - 3）、内置的一些工具对象：
        - `#execInfo` : information about the template being processed.
        - `#messages` : methods for obtaining externalized messages inside variables expressions, in the same way as they would be obtained using #{…} syntax.
        - `#uris` : methods for escaping parts of URLs/URIs
        - `#conversions` : methods for executing the configured conversion service (if any).
        - `#dates` : methods for java.util.Date objects: formatting, component extraction, etc.
        - `#calendars` : analogous to #dates , but for java.util.Calendar objects.
        - `#numbers` : methods for formatting numeric objects.
        - `#strings` : methods for String objects: contains, startsWith, prepending/appending, etc.
        - `#objects` : methods for objects in general.
        - `#bools` : methods for boolean evaluation.
        - `#arrays` : methods for arrays.
        - `#lists` : methods for lists.
        - `#sets` : methods for sets.
        - `#maps` : methods for maps.
        - `#aggregates` : methods for creating aggregates on arrays or collections.
        - `#ids` : methods for dealing with id attributes that might be repeated (for example, as a result of an iteration).

- Selection Variable Expressions: *{...}:选择表达式：和${}在功能上是一样；
    - 补充：配合 th:object="${session.user}
- Message Expressions: #{...}：获取国际化内容
- Link URL Expressions: @{...}：定义URL；
    - @{/order/process(execId=${execId},execType='FAST')}
- Fragment Expressions: ~{...}：片段引用表达式
    - `<div th:insert="~{commons :: main}">...</div>`  
- Literals（字面量）
    - Text literals: 'one text' , 'Another one!' ,…
    - Number literals: 0 , 34 , 3.0 , 12.3 ,…
    - Boolean literals: true , false
    - Null literal: null
    - Literal tokens: one , sometext , main ,…
- Text operations:（文本操作）
    - String concatenation: +
    - Literal substitutions: |The name is ${name}|
- Arithmetic operations:（数学运算）
    - Binary operators: + , - , * , / , %
    - Minus sign (unary operator): -
    - Boolean operations:（布尔运算）
    - Binary operators: and , or
    - Boolean negation (unary operator): ! , not
- Comparisons and equality:（比较运算）
    - Comparators: > , < , >= , <= ( gt , lt , ge , le )
    - Equality operators: == , != ( eq , ne )
- Conditional operators:条件运算（三元运算符）
    - If-then: (if) ? (then)
    - If-then-else: (if) ? (then) : (else)
    - Default: (value) ?: (defaultvalue)
    - Special tokens:
        - No-Operation: _（三元运算符没有操作）


