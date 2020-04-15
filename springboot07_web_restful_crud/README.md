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

## 5.4 实现CRUD查询并显示员工列表
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








