#  LearnSpringBoot-04-SpringBoot项目配置2

[SpringBoot_权威教程__哔哩哔哩 (゜-゜)つロ 干杯~-bilibili](https://www.bilibili.com/video/BV1Et411Y7tQ?p=4)

## 8、自动配置原理
配置文件到底能写什么？怎么写？自动配置原理；
[配置文件能配置的属性参照](https://docs.spring.io/spring-boot/docs/1.5.9.RELEASE/reference/htmlsingle/#common-application-properties)

### 8.1 步骤
1) SpringBoot启动的时候加载主配置类，开启了自动配置功能**@EnableAutoConfiguration**
    - @EnableAutoConfiguration的作用：利用EnableAutoConfigurationImportSelector给容器中导入一些组件。可以查看selectImports()方法的内容。
    - xxxAutoConfiguration类都是容器中的一个组件，都加入到容器中；用他们来做自动配置；
2) 每一个自动配置类进行自动配置功能

### 8.2 HttpEncodingAutoConfiguration（Http编码自动配置）为例
- 以 **HttpEncodingAutoConfiguration（Http编码自动配置）** 为例解释自动配置原理:
```java
// 表示这是一个配置类，以前编写的配置文件一样，也可以给容器中添加组件
@Configuration   
// 启动指定类的ConfigurationProperties功能；将配置文件中对应的值和HttpEncodingProperties绑定起来；并把HttpEncodingProperties加入到ioc容器中
@EnableConfigurationProperties(HttpEncodingProperties.class) 
// Spring底层有一个@Conditional注解（Spring注解版），根据不同的条件，如果满足指定的条件，整个配置类里面的配置就会生效；即判断当前应用是否是web应用，如果是，当前配置类生效。
@ConditionalOnWebApplication 
// 判断当前项目有没有这个类CharacterEncodingFilter：SpringMVC中进行乱码解决的过滤器；
@ConditionalOnClass(CharacterEncodingFilter.class)  
// 判断配置文件中是否存在某个配置spring.http.encoding.enabled；如果不存在，判断也是成立的
// 即使我们配置文件中不配置pring.http.encoding.enabled=true，也是默认生效的；
@ConditionalOnProperty(prefix = "spring.http.encoding", value = "enabled", matchIfMissing = true)  

public class HttpEncodingAutoConfiguration {
  
  	//已经和SpringBoot的配置文件映射了
  	private final HttpEncodingProperties properties;
  
   //只有一个有参构造器的情况下，参数的值就会从容器中拿
  	public HttpEncodingAutoConfiguration(HttpEncodingProperties properties) {
		this.properties = properties;
	}
  
    @Bean   //给容器中添加一个组件，这个组件的某些值需要从properties中获取
    //判断容器没有这个组件
	@ConditionalOnMissingBean(CharacterEncodingFilter.class) 
	public CharacterEncodingFilter characterEncodingFilter() {
		CharacterEncodingFilter filter = new OrderedCharacterEncodingFilter();
		filter.setEncoding(this.properties.getCharset().name());
		filter.setForceRequestEncoding(this.properties.shouldForce(Type.REQUEST));
		filter.setForceResponseEncoding(this.properties.shouldForce(Type.RESPONSE));
		return filter;
	}
```
- xxxAutoConfiguration：自动配置类，用来给容器中添加组件
- xxxProperties:封装配置文件中相关属性；
- 我们能在配置文件中配置的属性都是来源于这个功能的properties类
- 根据当前不同的条件判断，决定这个配置类是否生效
- 一但这个配置类生效；这个配置类就会给容器中添加各种组件；这些组件的属性是从对应的properties类中获取的，这些类里面的每一个属性又是和配置文件绑定的；

5) **所有在配置文件中能配置的属性都是在xxxProperties类中封装着**；配置文件能配置什么就可以参照某个功能对应的这个属性类


### 8.3 自动配置总结

​	**1）SpringBoot启动会加载大量的自动配置类**

​	**2）我们看我们需要的功能有没有SpringBoot默认写好的自动配置类；**

​	**3）我们再来看这个自动配置类中到底配置了哪些组件；（只要我们要用的组件有，我们就不需要再来配置了）**

​	**4）给容器中自动配置类添加组件的时候，会从properties类中获取某些属性。我们就可以在配置文件中指定这些属性的值；**


### 8.4 @Conditional派生注解
- 作用：必须是 **@Conditional** 指定的条件成立，才给容器中添加组件，配置类里面的所有内容才生效
- @Conditional扩展注解的总结：

| @Conditional扩展注解            | 作用（判断是否满足当前指定条件）                 |
| ------------------------------- | ---------------------------------------- |
| @ConditionalOnJava              | 系统的java版本是否符合要求                     |
| @ConditionalOnMissingBean       | 容器中不存在指定Bean；                       |
| @ConditionalOnExpression        | 满足SpEL表达式指定                           |
| @ConditionalOnClass             | 系统中有指定的类                            |
| @ConditionalOnMissingClass      | 系统中没有指定的类                            |
| @ConditionalOnSingleCandidate   | 容器中只有一个指定的Bean，或者这个Bean是首选Bean |
| @ConditionalOnResource          | 类路径下是否存在指定资源文件                   |
| @ConditionalOnWebApplication    | 当前是web环境                               |
| @ConditionalOnNotWebApplication | 当前不是web环境                              |
| @ConditionalOnJndi              | JNDI存在指定项                           |
- **自动配置类必须在一定的条件下才能生效；**
- 我们可以在配置文件中设置`debug=true`属性；来让**控制台**打印自动配置报告，这样我们就可以很方便的知道哪些自动配置类生效；
```
=========================
AUTO-CONFIGURATION REPORT
=========================


Positive matches:（自动配置类启用的）
-----------------

   DispatcherServletAutoConfiguration matched:
      - @ConditionalOnClass found required class 'org.springframework.web.servlet.DispatcherServlet'; @ConditionalOnMissingClass did not find unwanted class (OnClassCondition)
      - @ConditionalOnWebApplication (required) found StandardServletEnvironment (OnWebApplicationCondition)
        
    
Negative matches:（没有启动，没有匹配成功的自动配置类）
-----------------

   ActiveMQAutoConfiguration:
      Did not match:
         - @ConditionalOnClass did not find required classes 'javax.jms.ConnectionFactory', 'org.apache.activemq.ActiveMQConnectionFactory' (OnClassCondition)

   AopAutoConfiguration:
      Did not match:
         - @ConditionalOnClass did not find required classes 'org.aspectj.lang.annotation.Aspect', 'org.aspectj.lang.reflect.Advice' (OnClassCondition)
        
```

