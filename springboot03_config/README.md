#  LearnSpringBoot-03-SpringBoot项目配置文件

[SpringBoot_权威教程__哔哩哔哩 (゜-゜)つロ 干杯~-bilibili](https://www.bilibili.com/video/BV1Et411Y7tQ?p=4)

## 1 配置文件
- SpringBoot使用一个全局的配置文件，配置文件名是固定的；
    - •application.properties
    - •application.yml
- 配置文件的作用：修改SpringBoot自动配置的默认值；SpringBoot在底层都给我们自动配置好；
- YAML（YAML Ain't Markup Language）.YAML：**以数据为中心**，比json、xml等更适合做配置文件；
- YAML写法：
    ```yaml
    server:
      port: 8081
    ```

## 2 YAML语法：

### 2.1 基本语法
- `k:(空格)v`：表示一对键值对（空格必须有）
- 以**空格的缩进**来控制层级关系；只要是左对齐的一列数据，都是同一个层级的
    ```yaml
    server:
        port: 8081
        path: /hello
    ```
- 属性和值也是**大小写敏感**；

### 2.2 值的写法

#### 2.2.1 字面量：普通的值（数字，字符串，布尔）
- k: v：
- 字面直接来写；
- **字符串默认不用加上单引号或者双引号；**
- ""：**双引号；会转义字符串里面的特殊字符**；特殊字符会作为本身想表示的意思。
    - 输入：`name:   "zhangsan \n lisi"：
    - 输出：zhangsan 换行  lisi`
- ''：**单引号；不会转义特殊字符**，特殊字符最终只是一个普通的字符串数据。
    - 输入：`name:   "zhangsan \n lisi"：
    - 输出：zhangsan \n  lisi`
      
#### 2.2.2 对象、Map（属性和值）（键值对）：
- k: v：在下一行来写对象的属性和值的关系；注意缩进。对象还是k: v的方式
    ```yaml
    friends:
            lastName: zhangsan
            age: 20
    ```
- 行内写法：
    
    ```yaml
    friends: {lastName: zhangsan,age: 18}
    ```

#### 2.2.3 数组（List、Set）：
- 用`-`值表示数组中的一个元素 
    ```yaml
    pets:
     - cat
     - dog
     - pig
    ```
- **行内写法**
    ```yaml
    pets: [cat,dog,pig]
    ```

## 3 配置文件值注入
### 3.3.1 YAML配置文件值注入
- 配置文件：
    ```yaml
    person:
        lastName: hello
        age: 18
        boss: false
        birth: 2017/12/12
        maps: {k1: v1,k2: 12}
        lists:
          - lisi
          - zhaoliu
        dog:
          name: 小狗
          age: 12
    ```
- javaBean：
    ```java
    /**
     * 将配置文件中配置的每一个属性的值，映射到这个组件中
     * @ConfigurationProperties：告诉SpringBoot将本类中的所有属性和配置文件中相关的配置进行绑定；
     *      prefix = "person"：配置文件中哪个下面的所有属性进行一一映射
     * 只有这个组件是容器中的组件，才能容器提供的@ConfigurationProperties功能；
     */
    @Component
    @ConfigurationProperties(prefix = "person")
    public class Person {
    
        private String lastName;
        private Integer age;
        private Boolean boss;
        private Date birth;
    
        private Map<String,Object> maps;
        private List<Object> lists;
        private Dog dog;
          // get\set方法
    
    ```
- test：
    ```java
    /**
     * SpringBoot单元测试：可以在测试期间很方便地进行自动注入等容器功能
     */
    @SpringBootTest
    class Springboot03ConfigApplicationTests {
    
        @Autowired
        Person person;
    
        @Test
        void contextLoads() {
            System.out.println(person);
        } 
    }
    ```

- 我们可以导入配置文件处理器，以后编写配置就有提示了
    ```xml
    <!--导入配置文件处理器，配置文件进行绑定就会有提示-->
            <dependency>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-configuration-processor</artifactId>
                <optional>true</optional>
            </dependency>
    ```

#### 3.3.2 properties配置文件值注入
```
# properties配置文件注入person
person.last-name=张三
person.age=18
person.birth=2015/12/12
person.boss=false
person.maps.k1=v1
person.maps.k2=15
person.lists=a,b,c,d
person.dog.name=dog
person.dog.age=2
```
- 输出结果：
    ![qTf2BmG](https://i.imgur.com/qTf2BmG.png)
- .properties文件注入在idea中默认utf-8可能会乱码
- idea默认使用utf-8编码，修复乱码的设置如下：
    - ![OmuwIBO](https://i.imgur.com/OmuwIBO.png)

> YAML配置文件不存在读取中文乱码的情况

![idea配置乱码](images/搜狗截图20180130161620.png)

#### 3.3.3 松散绑定
- 以Person类中的成员熟悉firstName为例，使用@ConfigurationProperties注入时支持**松散绑定**，以下几种方式会匹配到同一个属性名：
    - person.firstName：使用标准方式
    - person.first-name：大写字母前用-
    - person.first_name：大写字母前用_
    - PERSON_FIRST_NAME：• 推荐系统属性使用这种写法

#### 3.3.4 使用Spring底层的@Value注解注入
- 使用 **@Value** 注解注入
    ```java
    public class Person {
    
        @Value("${person.last-name}")
        private String lastName;
        @Value("#{11*2}")
        private Integer age;
        @Value("true")
        private Boolean boss;
        @Value("${person.birth}")
        private Date birth;
        private Map<String, Object>  maps;
        private List<Object> lists;
        private Dog dog;
    }
    ```
  
>  @Value注解只能注入基本数据类型，对于Map\List\自定义对象等复杂类型不支持注入

#### 3.3.5 @Value获取值和@ConfigurationProperties获取值比较

|    功能               | @ConfigurationProperties | @Value     |
| -------------------- | ------------------------ | ---------- |
| 功能                 | 批量注入配置文件中的属性 | 一个个指定 |
| 松散绑定（松散语法） | 支持                     | 不支持     |
| SpEL                 | 不支持                   | 支持       |
| JSR303数据校验       | 支持                     | 不支持     |
| 复杂类型封装         | 支持                     | 不支持     |

- 配置文件yml还是properties他们都能获取到值；
- **如果我们只是在某个业务逻辑中需要获取一下配置文件中的某项值，使用@Value；**
- **如果我们专门编写了一个javaBean来和配置文件进行映射，我们就直接使用@ConfigurationProperties；**
 
#### 3.3.6 为注入的值添加JSR303校验
- 在需要校验的Bean类前使用**@Validation**注解，支持在需要注入的类成员变量上添加以下注解：
    - `@Null` ：被注释的元素必须为 `null`
    - `@NotNull` ：被注释的元素必须不为 `null`
    - `@AssertTrue`：被注释的元素必须为 `true`
    - `@AssertFalse`：被注释的元素必须为 `false`
    - `@Min(value)`：被注释的元素必须是一个数字，其值必须大于等于指定的最小值
    - `@Max(value)`：被注释的元素必须是一个数字，其值必须小于等于指定的最大值
    - `@DecimalMin(value)`：被注释的元素必须是一个数字，其值必须大于等于指定的最小值
    - `@DecimalMax(value)`：被注释的元素必须是一个数字，其值必须小于等于指定的最大值
    - `@Size(max, min)` ：被注释的元素的大小必须在指定的范围内
    - `@Digits (integer, fraction)` ：被注释的元素必须是一个数字，其值必须在可接受的范围内
    - `@Past`：被注释的元素必须是一个过去的日期
    - `@Future`：被注释的元素必须是一个将来的日期
    - `@Pattern(value)` ：被注释的元素必须符合指定的正则表达式
- JSR303校验的例子：
    ```java
    @Component
    @ConfigurationProperties(prefix = "person")
    @Validated
    public class Person {
    
        /**
         * <bean class="Person">
         *      <property name="lastName" value="字面量/${key}从环境变量、配置文件中获取值/#{SpEL}"></property>
         * <bean/>
         */
    
       //lastName必须是邮箱格式
        @Email
        private String lastName;
        private Integer age;
        private Boolean boss;  
        private Date birth;
        private Map<String,Object> maps;
        private List<Object> lists;
        private Dog dog;
    ```

#### 3.3.7 @PropertySource & @ImportResource & @Bean
- **@ConfigurationProperties(prefix = "person")** 默认从全局配置文件中获取值；
- 如果我们不想把所有配置都写在全局配置中，怎么办？—— 使用@PropertySource & @ImportResource
- @**PropertySource**：加载指定的配置文件。
    - 属性value:数组形式的文件名，可以支持加载多个配置文件
    - `@PropertySource(value = {"classpath:person.properties"})`
    ```java
    /**
     * 将配置文件中配置的每一个属性的值，映射到这个组件中
     * @ConfigurationProperties：告诉SpringBoot将本类中的所有属性和配置文件中相关的配置进行绑定；
     *      prefix = "person"：配置文件中哪个下面的所有属性进行一一映射
     *
     * 只有这个组件是容器中的组件，才能容器提供的@ConfigurationProperties功能；
     *  @ConfigurationProperties(prefix = "person")默认从全局配置文件中获取值；
     *
     */
    @PropertySource(value = {"classpath:person.properties"})
    @Component
    @ConfigurationProperties(prefix = "person")
    public class Person {
       //lastName必须是邮箱格式
       // @Email
        //@Value("${person.last-name}")
        private String lastName;
        //@Value("#{11*2}")
        private Integer age;
        //@Value("true")
        private Boolean boss;
    ```
- **@ImportResource**：导入Spring的配置文件，让配置文件里面的内容生效；Spring Boot里面没有Spring的配置文件，我们自己编写的配置文件，也不能自动识别；想让Spring的配置文件生效，加载进来就需要使用@**ImportResource**标注在一个配置类上
    ```java
    @ImportResource(locations = {"classpath:beans.xml"}) // 导入Spring的配置文件让其生效
    ```
- 使用全注解的方式导入Spring的配置文件：
    - 原来使用XML的方式
    ```xml
    <?xml version="1.0" encoding="UTF-8"?>
    <beans xmlns="http://www.springframework.org/schema/beans"
           xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
           xsi:schemaLocation="http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans.xsd">
    
    
        <bean id="helloService" class="com.atguigu.springboot.service.HelloService"></bean>
    </beans>
    ```
    - SpringBoot推荐给容器中添加bean组件的方式——推荐使用全注解的方式：
        - 1、配置类上添加 **@Configuration**注解，指明当前类是一个Spring配置类（替代beans.xml）
        - 2、使用 **@Bean** 标注配置类中有返回值的**方法**: 作用是将方法的返回值添加到容器中；容器中这个组件默认的id就是方法名
        
    ```java
    /**
     * @Configuration：指明当前类是一个配置类；就是来替代之前的Spring配置文件
     */
    @Configuration
    public class MyAppConfig {  
        //将方法的返回值添加到容器中；容器中这个组件默认的id就是方法名
        @Bean
        public HelloService helloService02(){
            System.out.println("配置类@Bean给容器中添加组件了...");
            return new HelloService();
        }
    }
    ```

##4 配置文件占位符

### 4.1 随机数
- 配置文件中写值的时候可以使用占位符产生随机数
- ${random.value}
- ${random.int} ： int范围中任意数
- ${random.long}
- ${random.int(10)}
- ${random.int[1024,65536]} ：范围内随机数
   

### 4.2 占位符可以获取之前配置过的值
- 占位符可以引用之前配置过的值
- `person.dog.name=${person.hello:hello}_dog`表示，从配置文件值前面寻找`person.hello`作为key的值，若找不到，则使用后面缺省的hello作为`person.hello`的值
- 如果引用的值没有指定的值（比如默认server.port=8080）:获取到的是默认的配置值
    ```properties
    person.last-name=张三${random.uuid}
    person.age=${random.int}
    person.birth=2017/12/15
    person.boss=false
    person.maps.k1=v1
    person.maps.k2=14
    person.lists=a,b,c
    person.dog.name=${person.hello:hello}_dog # 给之前没有指定过的person.hello设置默认值hello
    person.dog.age=15
    ```

## 5 Profile
- Profile是Spring对不同环境提供不同配置功能的支持，可以通过激活、指定参数等方式快速切换环境
- 多profile文件名形式
    - 格式：application-{anyword}.properties/yml
    - 例如：application-dev.properties、application-prod.properties
    






