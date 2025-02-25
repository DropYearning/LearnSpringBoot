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

## 4 SpringMVC自动配置原理

### 4.1 SpringBoot对SpringMVC的默认配置
- Spring Boot 自动配置好了SpringMVC，以下是SpringBoot对SpringMVC的默认配置：
  * Inclusion of `ContentNegotiatingViewResolver` and `BeanNameViewResolver` beans.
        - 该项配置了视图解析器（ViewResolver），根据方法的return值，得到视图对象View（例如jsp等），视图对象决定如何去渲染
        - ContentNegotiatingViewResolver：用来组合组合所有的视图解析器
        - 如何添加自定义视图解析器？——在自定义视图解析器类上添加@Bean注解，ContentNegotiatingViewResolver会自动的将其组合进来
            ```java
            // 尝试自己给容器中添加一个视图解析器
                @Bean
                public ViewResolver myViewResolver(){
                    return new MyViewResolver();
                }
            
                private static class MyViewResolver implements  ViewResolver{
            
                    @Override
                    public View resolveViewName(String s, Locale locale) throws Exception {
                        return null;
                    }
                }
            ```
    * Support for serving static resources, including support for WebJars (see below)： 提供静态资源，包括对WebJars的支持
    * Static `index.html` support：静态首页访问
    * Custom `Favicon` support (see below)：自定义图标
    * Automatic registration of `Converter`, `GenericConverter`, `Formatter` beans.
        - Converter：转换器，供类型转换用
        - Formatter：格式化器（比如把"2017-11-11"转换成Date类型）
            ```java
            //在文件中配置日期格式化的规则
            @Bean
            @ConditionalOnProperty(prefix = "spring.mvc", name = "date-format")
            public Formatter<Date> dateFormatter() {
                return new DateFormatter(this.mvcProperties.getDateFormat());//日期格式化组件
            }
            ```
        - 如何添加自定义格式化器转换器？——只需要在自定义格式化转换器上添加@Bean放在容器中即可
    * Support for `HttpMessageConverters` (see below)
        - HttpMessageConverters：SpringMVC中用来转换HTTP请求和响应的。例如User类和JSON之间的转换
        - 如果要添加自定义的消息转换器———
    * Automatic registration of `MessageCodesResolver` (see below):定义错误代码生成规则
    * Automatic use of a `ConfigurableWebBindingInitializer` bean (see below).
## 4.2 扩展SpringMVC 

> If you want to keep Spring Boot MVC features, and you just want to add additional [MVC configuration](https://docs.spring.io/spring/docs/4.3.14.RELEASE/spring-framework-reference/htmlsingle#mvc) (interceptors, formatters, view controllers etc.) you can add your own `@Configuration` class of type `WebMvcConfigurerAdapter`, but **without** `@EnableWebMvc`. If you wish to provide custom instances of `RequestMappingHandlerMapping`, `RequestMappingHandlerAdapter` or `ExceptionHandlerExceptionResolver` you can declare a `WebMvcRegistrationsAdapter` instance providing such components.If you want to take complete control of Spring MVC, you can add your own `@Configuration` annotated with `@EnableWebMvc`.
    
- 编写一个配置类（@Configuration），实现WebMvcConfigurer接口；不能标注@EnableWebMvc
    - 例如添加一个视图解析的映射：
        ```java
        // 添加自定义配置类，通过实现WebMvcConfigurer接口可以来扩展SpringMVC的功能
        @Configuration
        public class MyMvcConfig implements WebMvcConfigurer {
            //  添加一个视图解析设置 /test -> success.html
            @Override
            public void addViewControllers(ViewControllerRegistry registry) {
                registry.addViewController("/myview").setViewName("success1");
            }
        }
        ```
- 原理：
    1) WebMvcAutoConfiguration是SpringMVC的自动配置类
​	2) 在做其他自动配置时会导入；@Import(**EnableWebMvcConfiguration**.class)
        ```java
        @Configuration
        public static class EnableWebMvcConfiguration extends DelegatingWebMvcConfiguration {
        private final WebMvcConfigurerComposite configurers = new WebMvcConfigurerComposite();
        //从容器中获取所有的WebMvcConfigurer
        @Autowired(required = false)
        public void setConfigurers(List<WebMvcConfigurer> configurers) {
          if (!CollectionUtils.isEmpty(configurers)) {
              this.configurers.addWebMvcConfigurers(configurers);
                //一个参考实现；将所有的WebMvcConfigurer相关配置都来一起调用；  
                @Override
             // public void addViewControllers(ViewControllerRegistry registry) {
              //    for (WebMvcConfigurer delegate : this.delegates) {
               //       delegate.addViewControllers(registry);
               //   }
              }
          }
        }
        ```
    3） 容器中所有的WebMvcConfigurer都会一起起作用；
​	4） 我们的配置类也会被调用；
​- 效果：SpringMVC的自动配置和我们的扩展配置都会起作用；

### 4.3 全面接管对SpringMVC的配置
- SpringBoot对SpringMVC的自动配置不需要了，所有都是我们自己配置；所有的SpringMVC的自动配置都失效了（全手动配置）
- **我们需要在配置类中添加@EnableWebMvc即可全面接管对SpringMVC的配置**
- 为什么@EnableWebMvc自动配置就失效了？
    - @EnableWebMvc的核心：`@Import(DelegatingWebMvcConfiguration.class)`
    - DelegatingWebMvcConfiguration 继承自 WebMvcConfigurationSupport（用于SpringMVC的自动配置）
    - WebMvcConfigurationSupport中标注@Conditional**OnMissing**Bean(WebMvcConfigurationSupport.class)：容器中**没有**这个组件的时候，这个自动配置类才生效 
    - @EnableWebMvc将WebMvcConfigurationSupport组件导入进来；
- WebMvcConfigurationSupport能自动导入的设置都是SpringMVC最基本的功能，对于高级的拦截器等都需要自行配置

    
### 4.4 SpringBoot的默认配置流程
1. SpringBoot在自动配置很多组件的时候，先看容器中有没有用户自己配置的（@Bean、@Component...）如果有就用用户配置的，如果没有，才自动配置；如果有些组件可以有多个（例如ViewResolver），SpringBoot会将用户配置的和自己默认的组合起来；
2. 在SpringBoot中会有非常多的xxxConfigurer帮助我们进行扩展配置
3. 在SpringBoot中会有很多的xxxCustomizer帮助我们进行定制配置

## 5 Restful CRUD
### 5.1 引入静态资源
- 在[WebJars - Web Libraries in Jars](https://www.webjars.org/)中找到BootStrap的Maven依赖并且引入
- 修改html页面文件中引用BootStrap的位置：
    ```html
    <!-- Bootstrap core CSS -->
    <link th:href="@{/webjars/bootstrap/4.4.1-1/css/bootstrap.css}" rel="stylesheet">
    <!-- Custom styles for this template -->
    <link th:href="@{/asserts/css/signin.css}" rel="stylesheet">
    ```
- 使用`th:href`导入静态资源的好处：当将来在SpringBoot配置文件中修改`server.context-path=crud`之后，thymeleaf会自动为我们在静态资源的url前面加上当前的server.context-path。
    ![5MJJ0Vq](https://i.imgur.com/5MJJ0Vq.png)

### 5.2 实现登陆页面的国际化效果（切换界面语言）
- 原来SpringMVC国际化的步骤：
    1. 编写国际化配置文件
    2. 使用ResourceBundleMessageSource管理国际化资源文件
    3. 在页面使用fmt:message来取出国际化的内容
- 使用SpringBoot的步骤：
    1. 编写国际化配置文件，抽取页面需要显示的国际化消息：在/resources目录下新建i18n文件夹，添加配置文件`login.properties`来为登陆页面做国际化配置。根据需要配置的语言数目新建相应个数的.properties文件。
        ![08kx0it](https://i.imgur.com/08kx0it.png)
    2. SpringBoot中有MessageSourceAutoConfiguration来帮我们自动管理国际化资源文件
        ```java
        @ConfigurationProperties(prefix = "spring.messages")
        public class MessageSourceAutoConfiguration {
        
        /**
         * Comma-separated list of basenames (essentially a fully-qualified classpath
         * location), each following the ResourceBundle convention with relaxed support for
         * slash based locations. If it doesn't contain a package qualifier (such as
         * "org.mypackage"), it will be resolved from the classpath root.
         */
        private String basename = "messages";  
        //我们的配置文件可以直接放在类路径下叫messages.properties；
        
        @Bean
        public MessageSource messageSource() {
            ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
            if (StringUtils.hasText(this.basename)) {
                //设置国际化资源文件的基础名（去掉语言国家代码的）
                messageSource.setBasenames(StringUtils.commaDelimitedListToStringArray(
                        StringUtils.trimAllWhitespace(this.basename)));
            }
            if (this.encoding != null) {
                messageSource.setDefaultEncoding(this.encoding.name());
            }
            messageSource.setFallbackToSystemLocale(this.fallbackToSystemLocale);
            messageSource.setCacheSeconds(this.cacheSeconds);
            messageSource.setAlwaysUseMessageFormat(this.alwaysUseMessageFormat);
            return messageSource;
        }
        ```
    3. 在SprintBoot配置文件中配置国家化包名`spring.messages.basename=i18n.login`
    4. 在html页面获取国际化的值：thymeleaf-Message使用`#{}`获取
    5. 配置登陆页下方的语言选择按钮生效:SpringBoot通过LocalResolver获取区域对象解析,默认的就是根据请求头带来的区域信息获取Locale进行国际化。因此我们要自己实现一个自定义LocalResolver，并将其交给SpringBoot管理（在配置类中注入）
        ```java
           /**
            * 可以在链接上携带国际化的区域信息
            */
           public class MyLocaleResolver implements LocaleResolver {
           
               // 重写解析区域信息的方法
               @Override
               public Locale resolveLocale(HttpServletRequest httpServletRequest) {
                   String l = httpServletRequest.getParameter("l");
                   Locale locale = Locale.getDefault();
                   if(!StringUtils.isEmpty(l)){
                       String[] split = l.split("_");
                       locale = new Locale(split[0], split[1]);
                   }
                   return locale;
               }
           
               @Override
               public void setLocale(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Locale locale) {
           
               }
           }
        ```
   6. 在SpringBoot配置类`MyMvcConfig`中将自定义的区域解析器注入容器：
        ```java
        // 添加自定义的区域信息解析器(方法名必须为localeResolver)
        @Bean
        public LocaleResolver localeResolver(){
            return new MyLocaleResolver();
        }
        ```
    7. 之后就可以在页面中使用按钮切换语言了～
        ![9uGcUih](https://i.imgur.com/9uGcUih.png)

### 5.3 配置拦截器实现用户登陆
> 开发环境应该禁用模板引擎的缓存功能：在SpringBoot配置文件中修改如下`spring.thymeleaf.cache=false`

> 修改完HTML页面之后可以按 cmd+F9 来让重新编译 

> 要保证用户登陆请求发送之后跳转到的页面css样式不消失，需要在html页面中使用thymeleaf的th方式来设置相对路径 `th:href="@{/resourcesPath}"`

- 1、编写控制器：模拟登陆，不查询数据库，只校验固定的默认用户名和密码
    - 注意：登陆成功之后，为了防止表单重复提交，可以重定向到主页
    - 可以利用session存入用户登陆的凭证
    ```java
    /**
     * 处理登陆请求的控制器
     */
    @Controller
    public class LoginController {
        @PostMapping("/user/login")
        //@RequestMapping(value = "/user/login", method = RequestMethod.POST)
        public String login(@RequestParam("username") String username,
                            @RequestParam("password") String password,
                            Map<String,Object> map, HttpSession session){
            // 模拟用户校验
            if("admin".equals(username) && "123456".equals(password)){
                // 登陆成功后将用户名存入session
                session.setAttribute("loginUser", username);
                // 登陆成功之后，为了防止表单重复提交，可以重定向到主页
                return "redirect:/main.html";
            }else {
                // 登陆失败
                map.put("msg", "用户名密码错误");
                return "login";
            }
        }
    }
    ```

- 2、实现自定义拦截器实现登陆检查
    ```java
    /**
     * 用于检查登陆状态的拦截器，实现登陆检查
     */
    public class LoginHandlerInterceptor implements HandlerInterceptor {
    
        // 登陆前检查
        @Override
        public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
            Object user = request.getSession().getAttribute("loginUser");
            if (user == null){
                // 未登陆，返回登陆页面
                request.setAttribute("msg", "没有权限，请先登陆");
                request.getRequestDispatcher("/index.html").forward(request, response);
                return false;
            }else{
                // 已登陆，放行
                return true;
            }
        }
    
    }
    
    ```
- 3、在SpringBoot配置类`MyMvcConfig`中添加拦截器的相关配置
    ```java
    // 在WebMvcConfigurer中配置的组件会和SpringBoot的默认自动配置一起生效
    @Bean
    public WebMvcConfigurer webMvcConfigurer(){
        // 匿名类
        WebMvcConfigurer wc = new WebMvcConfigurer() {
            // 注册拦截器
            @Override
            public void addInterceptors(InterceptorRegistry registry) {
                // "/**"：拦截任意多层路径下的任意请求
                // 同时要排除对静态资源的拦截  "/asserts/**" ,"/webjars/**"
                registry.addInterceptor(new LoginHandlerInterceptor()).addPathPatterns("/**")
                        .excludePathPatterns("/index.html", "/", "/user/login", "/asserts/**" ,"/webjars/**");
            }

        };
        return wc;
        }
    ```

>  /webjars下静态资源的排除不再需要手动配置，SpringBoot会自动排除。

### 5.4 Restful CRUD需求分析
- 要求Restful CRUD: CRUD满足Rest风格
    - URI：/资源名称/资源标识, 以HTTP请求方式区分对资源CRUD操作
    
|  操作 | 普通CRUD（uri来区分操作） | RestfulCRUD       |
| ---- | ------------------------- | ----------------- |
| 查询 | getEmp                    | emp---GET         |
| 添加 | addEmp?xxx                | emp---POST        |
| 修改 | updateEmp?id=xxx&xxx=xx   | emp/{id}---PUT    |
| 删除 | deleteEmp?id=1            | emp/{id}---DELETE |

- 需求的请求架构

| 实验功能                             | 请求URI | 请求方式 |
| ------------------------------------ | ------- | -------- |
| 查询所有员工                         | emps    | GET      |
| 查询某个员工(来到修改页面)           | emp/1   | GET      |
| 来到添加页面                         | emp     | GET      |
| 添加员工                             | emp     | POST     |
| 来到修改页面（查出员工进行信息回显） | emp/1   | GET      |
| 修改员工                             | emp     | PUT      |
| 删除员工                             | emp/1   | DELETE   |

### 5.5 员工列表页面：抽取页面公共的样式片段
- 引入list.html，修改静态资源的引入url
- 抽取公共样式片段的方法一：使用Thymeleaf中抽取公共片段(th:fragment法) 见"Thymeleaf中抽取公共片段"
- 抽取公共样式片段的方法二：新写一个bar.html
        ```html
        <!--引入公共的部分1：topbar-->
		<div th:replace="commons/bar::topbar"></div>
        ```
- 设置菜单栏对应选项高亮：
    - 使用Thymeleaf的Parameterizable fragment signatures
    - 在bar.html中
- 在body内编写显示用户信息的表格
- 格式化显示的日期

### 5.6 员工添加页面
- 在显示添加页面之前需要先查出所有的部门，供添加页面显示
- 添加项目最容易的引起的问题就是提交的数据格式不对（例如日期的格式不对）
    - 日期的格式化；SpringMVC将页面提交的值需要转换为指定的类型; 
    - 默认日期是按照`dd/mm/dd`的方式；
    - 可以在SpringBoot的主配置文件`application.properties`中

> 由于此时并没有连接数据库，因此重启项目后新添加的员工信息就会消失


### 5.7 员工信息修改页面
- 复用员工信息修改页面add.html，在thymeleaf语句中使用判断符来检查当前是要完成修改还是添加功能，`<input name="lastName" type="text" class="form-control" placeholder="zhangsan" th:value="${emp!=null}?${emp.lastName}">`
- 发送put请求修改员工数据
    - 1、SpringMVC中配置HiddenHttpMethodFilter;（`spring.mvc.hiddenmethod.filter.enabled=true`）
    - 2、页面创建一个post表单
    - 3、创建一个input项，name="_method";值就是我们指定的请求方式

- [springboot表单添加隐藏域name=_method后依旧无法转化put和delete，无法被相应的控制器捕获，出现Request method POST not supported错误_Java_Sirius_lin的博客-CSDN博客](https://blog.csdn.net/Sirius_lin/article/details/102909593)

### 5.8 删除员工操作
- 需要用DELETE方式发送请求，因此需要使用表单
- 方法一：使用表单删除
    ```html
    <td>
        <a class="btn btn-sm btn-primary" th:href="@{emp/}+${emp.id}">编辑</a>
        <form th:action="@{/emp/}+${emp.id}" method="post">
            <input type="hidden" name="_method" value="delete"/>
            <button  class="btn btn-sm btn-danger">删除</button>
        </form>
    </td>
    ```
- 方法二：使用js删除: 见list.html
  

### 5.9 Thymeleaf中抽取公共片段
- 1、抽取公共片段
    ```html
    <div th:fragment="copy">
      & 2011 The Good Thymes Virtual Grocery
    </div>
    ```
- 2、引入公共片段
    ```html
    <div th:insert="~{footer :: copy}"></div>
    ```
    - ~{templatename::selector}：模板名::选择器
    - ~{templatename::fragmentname}:模板名::片段名  
    - 模板名：会使用thymeleaf的前后缀配置规则进行解析（例如在dashboard.html中定义的公共片段，直接使用`dashboard::topbar`引入即可）
   
- 3、默认效果：
    - insert的公共片段在div标签中
    - 如果使用th:insert等属性进行引入，可以不用写~{}：
    - 行内写法可以加上：[[~{}]];[(~{})]；

- 三种引入公共片段的th属性：
    **th:insert**：将公共片段整个插入到声明引入的元素中
    **th:replace**：将声明引入的元素替换为公共片段
    **th:include**：将被引入的片段的内部内容包含进这个标签中   
     
```html
<!--要被公共使用的HTML片段-->
<footer th:fragment="copy">
&copy; 2011 The Good Thymes Virtual Grocery
</footer>

<!--3种引入方式-->
<div th:insert="footer :: copy"></div>
<div th:replace="footer :: copy"></div>
<div th:include="footer :: copy"></div>

<!--3种效果-->
<div>
    <footer>
    &copy; 2011 The Good Thymes Virtual Grocery
    </footer>
</div>

<footer>
&copy; 2011 The Good Thymes Virtual Grocery
</footer>

<div>
&copy; 2011 The Good Thymes Virtual Grocery
</div>
```

## 6 SpringBoot错误处理机制
- 若使用浏览器发送请求默认错误页面：
    - ![gWLVqnv](https://i.imgur.com/gWLVqnv.png)
	- ![https://i.imgur.com/WWhevlr.png](https://i.imgur.com/WWhevlr.png)
- 若使用Postman请求，响应的错误是JSON数据
    - ![FHBAEhg](https://i.imgur.com/FHBAEhg.png)
	- ![https://i.imgur.com/2WCKKZ9.png](https://i.imgur.com/2WCKKZ9.png)
- SpringBoot错误配置原理可以参照`ErrorMvcAutoConfiguration`
	- 该类会给容器中添加组件`DefaultErrorAttributes`
	- BasicErrorController：处理默认/error请求的控制器
	- ErrorPageCustomizer：系统出现错误之后来到error请求进行处理，
	- DefaultErrorViewResolver：默认的错误页面视图解析器
		- 1、默认SpringBoot会去找到 `error/ 状态码`组合起来的视图地址，由模板引擎解析
		- 2、如果模板引擎可用的情况下返回到errorViewName指定的视图地址
		- 3、如果模板引擎不可用，就在静态资源文件夹下找errorViewName对应的页面  。相当于去找`error/404.html`
- 步骤：一但系统出现4xx或者5xx之类的错误；ErrorPageCustomizer就会生效（定制错误的响应规则），就会发送/error请求；该请求就会被控制器BasicErrorController处理；

### 6.1 如何定制错误响应页面？
- 1、有模板引擎的情况下自动会去解析`error/状态码`，因此可以将错误页面命名为`错误状态码.html`放在模板引擎文件夹里面的/error文件夹下，发生此状态码的错误就会来到对应的页面
    - 我们可以使用`4xx`和`5xx`作为错误页面的文件名来**通配**这种类型的所有错误。SpringBoot会以精确优先（优先寻找精确的状态码.html），若找不到就调用4xx和5xx
    - 跳转错误页面时会返回 ModelAndView，会携带以下信息：
        - `timestamp`：时间戳
        ​- `status`：状态码
        - `error`：错误提示
        - `exception`：异常对象
        - `message`：异常消息
        ​- `errors`：JSR303数据校验的错误都在这里
    > 注意，获取自定义异常信息需要在配置文件中添加`server.error.include-exception=true`
- 2、若没有模板引擎（模板引擎找不到这个错误页面），会静态资源文件夹下找`错误状态码.html`，但是由于没有模板引擎，无法获取和解析ModelAndView中的信息
- 3、若以上都没有或者都没有找到对应的页面，默认来到SpringBoot默认的错误提示页面：
    - ![https://i.imgur.com/WWhevlr.png](https://i.imgur.com/WWhevlr.png)


### 6.2 如何定制错误返回的JSON数据？
- 假设抛出的异常如下：
    ```java
        @ResponseBody
        @RequestMapping("/hello")
        public String hello(@RequestParam("user") String user){
            if (user.equals("aaa")){ // 模拟自定义异常的抛出
                throw new UserNotExistException(); // UserNotExistException是自定义的异常类
            }
            return "Hello World!";
        }
    ```
- 方法一：利用`ExceptionHandler`继承实现自定义的异常处理器，并返回一个Map<String,Object>，SpringBoot会自动解析为JSON返回：
    - 这种方法无论是浏览器还是客户端返回的都是JSON格式数据
    - ![e53A93L](https://i.imgur.com/e53A93L.png)
    ```java
    /**
     * 自定义异常，用户不存在
     */
    public class UserNotExistException extends RuntimeException {
        public UserNotExistException(){
            super("用户不存在");
        }
    }
    ```
  
    ```html
    @ControllerAdvice
    public class MyExceptionHandler  {
        @ResponseBody
        @ExceptionHandler(UserNotExistException.class)
        public Map<String, Object> handleException(Exception e){
            // 响应自己的JSON数据
            Map<String, Object> map = new HashMap<>();
            map.put("code", "user.notexist");
            map.put("message", e.getMessage());
            return map;
        }
    }
    ```
- 方法二：转发到/error实现自适应
    - ![BiWO7uU](https://i.imgur.com/BiWO7uU.png)
    - 客户端返回JSON，浏览器返回错误页面
    - **一定要传入自定义的错误状态码，否则一定是200**，`request.setAttribute("javax.servlet.error.status_code", 404);`
    ```java
       // 方法二：在前面的基础上实现自适应
       @ExceptionHandler(UserNotExistException.class)
       public String handleException(Exception e, HttpServletRequest request){
           // 响应自己的JSON数据
           Map<String, Object> map = new HashMap<>();
           // 传入自定义的错误状态码，否则一定是200
           request.setAttribute("javax.servlet.error.status_code", 404);
           map.put("code", "user.notexist");
           map.put("message", e.getMessage());
           return "forward:/error"; //转发到/error
       }
    ```
- 方法三：不仅实现自适应，还在JSON中返回自定义数据
    - 出现错误以后，会来到/error请求，会被BasicErrorController处理，响应出去可以获取的数据是由getErrorAttributes得到的（是AbstractErrorController（ErrorController）规定的方法）；
    - ​（第一种思路）完全来编写一个ErrorController的实现类【或者是编写AbstractErrorController的子类】，放在容器中；
    - （第二种思路）页面上能用的数据，或者是json返回能用的数据都是通过errorAttributes.getErrorAttributes得到；容器中DefaultErrorAttributes.getErrorAttributes()是默认定义要输出的数据的方法
        - 效果：![draxc2K](https://i.imgur.com/draxc2K.png)
        ```java
        // 在容器中加入我们自定义的错误属性类
        @Component
        public class MyErrorAttributes extends DefaultErrorAttributes {
        
            // 返回的errorAttributes就是页面和JSON能获取到所有字段
            @Override
            public Map<String, Object> getErrorAttributes(WebRequest webRequest, boolean includeStackTrace) {
                Map<String, Object> errorAttributes = super.getErrorAttributes(webRequest,includeStackTrace );
                errorAttributes.put("company", "mycompany");
                // 我们的异常处理器携带的数据
                Map<String, Object> ext = (Map<String, Object>) webRequest.getAttribute("ext", 0);
                errorAttributes.put("ext", ext);
                return errorAttributes;
            }
        }

        ```
        - 修改自定义异常处理器：
        ```java
        // 方法二：在前面的基础上实现自适应
            @ExceptionHandler(UserNotExistException.class)
            public String handleException(Exception e, HttpServletRequest request){
                // 响应自己的JSON数据
                Map<String, Object> map = new HashMap<>();
                // 传入自定义的错误状态码，否则一定是200
                request.setAttribute("javax.servlet.error.status_code", 404);
                map.put("code", "user.notexist");
                map.put("message", e.getMessage());
                request.setAttribute("ext", map);
                return "forward:/error"; //转发到/error
            }
        ```
## 7 配置嵌入式Servlet容器(配置SpringBoot内置的Tomcat)
- SpringBoot默认使用Tomcat作为嵌入式的Servlet容器

### 7.1 如何定制和修改Servlet容器的相关配置
- 方法一：在SpringBoot的主配置文件中修改和server有关的配置（SpringBoot的设置定义类：`ServerProperties`）【也是EmbeddedServletContainerCustomizer】
- 方法二：在配置类中编写一个返回**EmbeddedServletContainerCustomizer**类的方法（嵌入式的Servlet容器的定制器）来修改Servlet容器的配置
    ```java
    // 添加自定义配置类，通过实现WebMvcConfigurer接口可以来扩展SpringMVC的功能
    @Configuration
    public class MyMvcConfig implements WebMvcConfigurer {
        // 定制嵌入式Servlet容器的相关设置
        @Bean
        public WebServerFactoryCustomizer webServerFactoryCustomizer(){
            return new WebServerFactoryCustomizer<ConfigurableWebServerFactory>() {
                @Override
                public void customize(ConfigurableWebServerFactory factory) {
                    factory.setPort(8080); // 例如修改端口
                }
            };
        }
      }
    ```
- 这两种方法本质上是一样的

### 7.2 配置嵌入式容器中的Servlet三大组件【Servlet、Filter、Listener】
- 由于SpringBoot默认是以jar包的方式启动嵌入式的Servlet容器来启动SpringBoot的web应用，因此在/WEB-INF目录下没有web.xml配置文件了，该如何配置Servlet中的三大组件？
- 在配置类中添加方法返回`ServletRegistrationBean`用于注册Servlet
    ```java
    @Configuration
    public class MyServerConfig {
        // 注册Servlet
        @Bean
        public ServletRegistrationBean servletRegistrationBean(){
            ServletRegistrationBean<Servlet> registrationBean = new ServletRegistrationBean<>(new MyServlet(), "/myServlet");
            registrationBean.setLoadOnStartup(1); // 原来在XML中可以设置的项在这里都可以设置
            return registrationBean;
        }
    }

    public class MyServlet  extends HttpServlet {
        @Override
        protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
            doPost(req, resp);
        }
    
        @Override
        protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
            resp.getWriter().write("Hello My Servlet");
        }
    }
    ```
- 在配置类中添加方法返回`FilterRegistrationBean`用于注册Filter
    ```java
    public class MyFilter implements Filter {
        @Override
        public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
            System.out.println("MyFilter doFilter()");
            filterChain.doFilter(servletRequest, servletResponse);
        }
    }
  
    @Configuration
    public class MyServerConfig {
      // 注册Filter
      @Bean
      public FilterRegistrationBean filterRegistrationBean(){
          FilterRegistrationBean<Filter> registrationBean = new FilterRegistrationBean<Filter>();
          registrationBean.setFilter(new MyFilter());
          registrationBean.setUrlPatterns(Arrays.asList("/hello", "/myServlet"));
          return registrationBean;
      }
    }

    ```
- 在配置类中添加方法返回`ServletListenerRegistrationBean`用于注册Listener
    ```java
    public class MyListener implements ServletContextListener {
        @Override
        public void contextInitialized(ServletContextEvent sce) {
            System.out.println("contextInitialized..web应用启动了");
        }
    
        @Override
        public void contextDestroyed(ServletContextEvent sce) {
            System.out.println("contextDestroyed..web应用销毁了");
        }
    }
      // 自定义配置类
      @Configuration
      public class MyServerConfig {
          // 注册Listener
          @Bean
          public ServletListenerRegistrationBean servletListenerRegistrationBean(){
              ServletListenerRegistrationBean<MyListener> listenerRegistrationBean = new ServletListenerRegistrationBean<>(new MyListener());
              return listenerRegistrationBean;
          }
      }
    ```
- 实例：SpringBoot帮我们自动配置SpringMVC的时候，自动的注册SpringMVC的前端控制器DispatcherServlet就是一个很好的例子`DispatcherServletAutoConfiguration`

### 7.3 替换使用其他嵌入式Servlet容器
- **Servlet容器**主要是JavaWeb应用提供运行时环境，所以也可以称之为JavaWeb应用容器，或者Servlet/JSP容器。Servlet容器主要负责管理Servlet、JSP的生命周期以及它们的共享数据。
    - 目前最流行的Servlet容器软件包括: Tomcat、Jetty、Jboss等。
    - ![SpD9FDe](https://i.imgur.com/SpD9FDe.jpg)
- ![xVU66Rg](https://i.imgur.com/xVU66Rg.png)
- **Tomcat**（默认使用）：
    ```xml
    <dependency>
       <groupId>org.springframework.boot</groupId>
       <artifactId>spring-boot-starter-web</artifactId>
       引入web模块默认就是使用嵌入式的Tomcat作为Servlet容器；
    </dependency>
    ```

- **Jetty** 的依赖（适合开发长连接应用，例如聊天软件）
    ```xml
    <!-- 引入web模块 -->
    <dependency>
       <groupId>org.springframework.boot</groupId>
       <artifactId>spring-boot-starter-web</artifactId>
       <exclusions>
          <exclusion>
             <artifactId>spring-boot-starter-tomcat</artifactId>
             <groupId>org.springframework.boot</groupId>
          </exclusion>
       </exclusions>
    </dependency>
    
    <!--引入其他的Servlet容器-->
    <dependency>
       <artifactId>spring-boot-starter-jetty</artifactId>
       <groupId>org.springframework.boot</groupId>
    </dependency>
    ```
- **Undertow** 的依赖（Undertow不支持JSP，轻量）
    ```xml
    <!-- 引入web模块 -->
    <dependency>
       <groupId>org.springframework.boot</groupId>
       <artifactId>spring-boot-starter-web</artifactId>
       <exclusions>
          <exclusion>
             <artifactId>spring-boot-starter-tomcat</artifactId>
             <groupId>org.springframework.boot</groupId>
          </exclusion>
       </exclusions>
    </dependency>
    
    <!--引入其他的Servlet容器-->
    <dependency>
       <artifactId>spring-boot-starter-undertow</artifactId>
       <groupId>org.springframework.boot</groupId>
    </dependency>
    ```
### 7.4 替换Servlet容器背后的自动配置原理
- `EmbeddedServletContainerAutoConfiguration`类:嵌入式的Servlet容器自动配置
    - `EmbeddedServletContainerFactory`:嵌入式Servlet容器工厂）
        - ![GD9Vnzt](https://i.imgur.com/GD9Vnzt.png)
    - `EmbeddedServletContainer`：嵌入式的Servlet容器
    
- 步骤：
    - 1、SpringBoot根据导入的依赖情况，给容器中添加相应的EmbeddedServletContainerFactory【例如，TomcatEmbeddedServletContainerFactory】
    - 2、容器中某个组件要创建对象就会惊动后置处理器EmbeddedServletContainerCustomizerBeanPostProcessor；只要是嵌入式的Servlet容器工厂，后置处理器就工作；
    - 3、后置处理器从容器中获取所有的**EmbeddedServletContainerCustomizer**，调用定制器的定制方法

### 7.5 嵌入式Servlet容器启动原理
- SpringBoot什么时候创建嵌入式的Servlet容器工厂？什么时候获取嵌入式的Servlet容器并启动Tomcat？
- 问题一：SpringBoot什么时候创建嵌入式的Servlet容器工厂？
    - 1）、SpringBoot应用启动运行run方法
    - 2）、refreshContext(context);SpringBoot刷新IOC容器【创建IOC容器对象，并初始化容器，创建容器中的每一个组件】；如果是web应用就创建**AnnotationConfigEmbeddedWebApplicationContext**，否则就创建**AnnotationConfigApplicationContext**
    - 3）、refresh(context);**刷新刚才创建好的ioc容器；**
    - 4）、  onRefresh(); web的ioc容器重写了onRefresh方法
    - 5）、webioc容器会创建嵌入式的Servlet容器；**createEmbeddedServletContainer**();
    - 6）、**获取嵌入式的Servlet容器工厂：**EmbeddedServletContainerFactory containerFactory = getEmbeddedServletContainerFactory();从ioc容器中获取EmbeddedServletContainerFactory 组件；**TomcatEmbeddedServletContainerFactory**创建对象，后置处理器一看是这个对象，就获取所有的定制器来先定制Servlet容器的相关配置；
    - 7）、**使用容器工厂获取嵌入式的Servlet容器**：this.embeddedServletContainer = containerFactory.getEmbeddedServletContainer(getSelfInitializer());
    - 8）、嵌入式的Servlet容器创建对象并启动Servlet容器；**先启动嵌入式的Servlet容器，再将ioc容器中剩下没有创建出的对象获取出来**
    - **关键：IOC容器启动创建嵌入式的Servlet容器**

## 8 使用外置的Servlet容器
- 使用嵌入式Servlet容器的好处：应用打成可执行的`jar`包，简单、便携；缺点：默认不支持JSP、优化定制比较复杂（使用定制器【ServerProperties、自定义EmbeddedServletContainerCustomizer】，自己编写嵌入式Servlet容器的创建工厂）
    - JAR（Java Archive，Java 归档文件）是与平台无关的文件格式，它允许将许多文件组合成一个压缩文件。通常是开发时要引用通用类，打成jar包便于存放管理。
- 外置的Servlet容器：外面安装Tomcat等容器；应用使用`war`包的方式打包；
    - war包是一个可以直接运行的web模块。是做好一个web应用后，通常是网站，打成包部署到容器中。
- [jar包和war包的介绍和区别 - 简书](https://www.jianshu.com/p/3b5c45e8e5bd)
- 

### 8.1 使用外置的Servlet容器的步骤


## 参考资料
- [SpringBoot_权威教程_spring boot_springboot核心篇+springboot整合篇-_雷丰阳_尚硅谷_哔哩哔哩 (゜-゜)つロ 干杯~-bilibili](https://www.bilibili.com/video/BV1Et411Y7tQ?p=1)
- [Web容器、Servlet容器、Spring容器、SpringMVC容器之间的关系 - Jie~ - 博客园](https://www.cnblogs.com/jieerma666/p/10805966.html)
- [jar包和war包的介绍和区别 - 简书](https://www.jianshu.com/p/3b5c45e8e5bd)














































