#  LearnSpringBoot-07-SpringBoot-数据访问-SpringData&JPA

[SpringBoot_权威教程__哔哩哔哩 (゜-゜)つロ 干杯~-bilibili](https://www.bilibili.com/video/BV1Et411Y7tQ?p=4)

## 1 SpringData
- Spring Data 项目的目的是为了**简化构建基于 Spring 框架应用的数据访问技术**，包括非关系数据库、 Map-Reduce 框架、云数据服务等等；另外也包含对关系数据库的访问支持。

- SpringData为我们**提供使用统一的API来对数据访问层进行操作**；这主要是Spring Data Commons项目来实现的。Spring Data Commons让我们在使用关系型或者非关系型数据访问 技术时都基于Spring提供的统一标准，标准包含了CRUD（创建、获取、更新、删除）、查询、 排序和分页的相关操作

- 统一的Repository接口：
    - ![image-20200418155116404](/Users/brightzh/Library/Application Support/typora-user-images/image-20200418155116404.png)
    - Repository：统一接口 
    - RevisionRepository>：基于乐观 锁机制 
    - CrudRepository：基本CRUD操作 
    - PagingAndSortingRepository：基本CRUD及分页
- **Spring Data提供了数据访问模板类** xxxTemplate：例如MongoTemplate、RedisTemplate等
- Spring Data默认底层使用Hibernate实现
    - ![5sR2va6](https://i.imgur.com/5sR2va6.png)

## 2 JPA
- Jpa (Java Persistence API) 是 Sun 官方提出的 Java 持久化规范。它为 Java 开发人员提供了一种对象/关联映射工具来管理 Java 应用中的关系数据。它的出现主要是为了简化现有的持久化开发工作和整合 ORM 技术，结束现在 Hibernate，TopLink，JDO 等 ORM 框架各自为营的局面。
- 值得注意的是，Jpa是在充分吸收了现有 Hibernate，TopLink，JDO 等 ORM 框架的基础上发展而来的，具有易于使用，伸缩性强等优点。从目前的开发社区的反应上看，Jpa 受到了极大的支持和赞扬，其中就包括了 Spring 与 EJB3. 0的开发团队。
- JSR317规范

## 3 Spring Boot Jpa
- Spring Boot Jpa 是 Spring 基于 ORM 框架、Jpa 规范的基础上封装的一套 Jpa 应用框架，可使开发者用极简的代码即可实现对数据的访问和操作。它提供了包括增删改查等在内的常用功能，且易于扩展
- Spring Boot Jpa 让我们解脱了 DAO 层的操作，基本上所有 CRUD 都可以依赖于它来实现

## 4 SpringBoot整合JPA
- 1、编写一个实体类（Bean）和数据库表进行映射，并且配置使用JPA注解映射关系
    - `@Entity`：作用在类上，告诉JAP这是一个实体类(用来与数据表映射的类)
    - `@Table(name = "tbl_user")` ：作用在类上，指明和哪一个表对应，如果省略默认表名就是类名的小写user
    - `@Id` ：作用属性上，标注这是一个主键
    - `@GeneratedValue(strategy = GenerationType.IDENTITY)`：作用在属性上，标注主键的生成策略是自增
    - `@Column(name = "last_name", length = 50)` ：作用在属性上，这是和数据表对应的一个列，省略属性的情况下默认列名就是属性名email
    ```java
    @Entity // 告诉JAP这是一个实体类(用来与数据表映射的类)
    @Table(name = "tbl_user") // 指明和哪一个表对应，如果省略默认表名就是类名的小写user
    @JsonIgnoreProperties(value = {"hibernateLazyInitializer"})
    public class User {
        @Id // 标注这是一个主键
        @GeneratedValue(strategy = GenerationType.IDENTITY) // 标注主键的生成策略是自增
        private Integer id;
        @Column(name = "last_name", length = 50) // 这是和数据表对应的一个列
        private String lastName;
        @Column // 省略属性的情况下默认列名就是属性名email
        private String email;
    
        public Integer getId() {
            return id;
        }
    
        public void setId(Integer id) {
            this.id = id;
        }
    
        public String getLastName() {
            return lastName;
        }
    
        public void setLastName(String lastName) {
            this.lastName = lastName;
        }
    
        public String getEmail() {
            return email;
        }
    
        public void setEmail(String email) {
            this.email = email;
        }
    }
    ```
- 2、编写一个Dao接口（Repository）来操作实体类对应的数据表
    ```java
    // JpaRepository<User,  Integer>：前者是要操作的表对应的实体类；后者是主键
    public interface UserRepository  extends JpaRepository<User,  Integer> {
    }
    ```
    - 只需是一个接口即可，SpringData会帮我们使用动态代理创建实例
- 3、在SpringBoot主配置文件中对JAP进行配置：
    ```yaml
    spring:
      datasource:
        username: root
        password: 123456
        url: jdbc:mysql://127.0.0.1:3307/jpa?serverTimezone=UTC
        driver-class-name: com.mysql.cj.jdbc.Driver
        initialization-mode: always # 设置SpringBoot 2.x启动时加载SQL必须打开此项
      jpa:
        hibernate:
          # 在启动时直接根据实体类更新或者创建数据表
          ddl-auto: update
        # 控制台可以显示SQL
        show-sql: true
    #    schema: # 指定启动时要加载的SQL文件,schema需要填一个list
    #      - classpath:sql/department.sql
    #      - classpath:sql/employee.sql
    ```
    - 这样设置在SpringBoot启动时会自动在数据库中生成响应的数据表：![FEHhKK3](https://i.imgur.com/FEHhKK3.png)
    - 若数据表已存在，则不创建
- 4、编写控制类实现CRUD
    ```java   
    @RestController
    public class UserController {
    
        @Autowired
        UserRepository userRepository;
    
        @GetMapping("/user/{id}")
        public User getUser(@PathVariable("id") Integer id) {
            // 注意这里如果不加@PathVariable("id")会报500错误。
            // The given id must not be null!; nested exception is java.lang.IllegalArgumentException: The given id must not be null!
            User user = userRepository.findById(id).orElse(null);
            return user;
        }
    
        @GetMapping("/user")
        public User insertUser(User user){
            User save = userRepository.save(user);
            return save;
        }
    }
    ```
> 注意在使用Restful的路径请求查询时，参数方法变量前面一定要加上@PathVariable("xx")的注解，否则会报500错误。

![OQsgSBo](https://i.imgur.com/OQsgSBo.png)


## 参考资料

- [使用 Spring Data JPA 简化 JPA 开发](https://www.ibm.com/developerworks/cn/opensource/os-cn-spring-jpa/index.html)
- [JPA教程™](https://www.yiibai.com/jpa/)
- [SpringBoot开发使用Mybatis还是Spring Data JPA?? - 知乎](https://www.zhihu.com/question/316458408)
- 