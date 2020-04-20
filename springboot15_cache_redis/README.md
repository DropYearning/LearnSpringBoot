#  LearnSpringBoot-SpringBoot整合-缓存

[SpringBoot_权威教程__哔哩哔哩 (゜-゜)つロ 干杯~-bilibili](https://www.bilibili.com/video/BV1Et411Y7tQ?p=4)

       
## 1 缓存注解回顾

> 这些缓存注解都被标注在了业务层类中的方法上

- **@Cacheable**
    - 属性cacheNames /value：指定缓存的名字，供CacheManager管理时来区分缓存的名字。**可以使用{}指定多个缓存的名字**
    - 属性key：缓存数据使用的key，默认是使用方法的参数的值（例如请求传入的参数id是1，则以1作为key）.
        - key可以编写SpEL表达式：#id是参数id的值 等同于 #a0 等同于 #p0 等同于root.args[0]
        - **不建议使用简单的id等作为key，因为有可能会和其他缓存key重复**
    - 属性keyGenerator:key的生成器，可以自己指定key的生成器的组件id
        - 自定义keyGenerator实现类并且@Bean注入到容器中
            ```java
            @Configuration
            public class MyCacheConfig {
            
                @Bean("myKeyGenerator")
                public KeyGenerator keyGenerator(){
                    return new KeyGenerator(){ //匿名内部类
            
                        @Override
                        public Object generate(Object o, Method method, Object... objects) {
                            return method.getName()+"["+ Arrays.asList(objects).toString() +"]";
                        }
                    };
                }
            }
            ```
        - 使用自定义的keyGenerator`@Cacheable(cacheNames = "emp", keyGenerator = "myKeyGenerator")`
        - **key和keyGenerator二选一使用**
    - 属性cacheManager：指定使用缓存管理器
    - 属性cacheResolver：指定缓存解析器
        - cacheManager和cacheResolver二选一使用
    - 属性condition：指定符合条件的情况下才缓存，也支持SpEL表达式。
        - 例如，condition = "#id>0"表示仅在id大于0时才缓存数据
    - 属性unless：除非，是condition的反，当unless表达式的值为true，方法的返回值就不会缓存
        - unless可以获取到结果进行判断。例如unless = "#result == null"表示若方法的结果为null则不缓存
    - 属性sync：是否使用异步模式。
        - 默认是方法执行完以同步的方式将结果存在缓存中
- **@CacheEvict**：缓存清除，常用于delete方法中，当数据表项删除后，删除其在SpringBoot中对应的缓存
    - 属性key：指定要清除的数据
        - ![vC6b4nI](https://i.imgur.com/vC6b4nI.png)
    - 属性allEntries = true：指定清除这个缓存中**所有**的数据，默认false。【即整一个cacheNames ="xxx"的缓存空间全部清除，此时不用再指定key了】
        - ![S965FYr](https://i.imgur.com/S965FYr.png)
    - 属性beforeInvocation = false：缓存的清除是否在方法之前执行。默认false代表缓存清除操作是在方法执行之后执行;_如果出现异常缓存就不会清除缓存。_
    - 属性beforeInvocation = true：代表清除缓存操作是在方法运行之前执行，_无论方法是否出现异常，缓存都清除_
    
- **@CachePut** ： 即调用方法，又更新SpringBoot缓存中的数据，适用于知道数据表中数据可能被修改时使用
    - 运行时机：1、先调用目标方法; 2、将目标方法的结果缓存起来
    - 测试步骤：
        - 1、查询1号员工；查到的结果会放在缓存中； key：1  value：lastName：张三
        - 2、以后查询还是之前的结果
        - 3、更新1号员工；【lastName:zhangsan；gender:0】
           *          将方法的返回值也放进缓存了；
           *          key：传入的employee对象的值：返回的employee对象；
        - 4、再次查询1号员工？应该是更新后的员工；但实际上是没更新前的【**即1号员工没有在缓存中更新，因为两次的key不一致**】
        - 5、应该统一指定key：
            - key = "#employee.id":使用传入的参数的员工id；等同于 key = "#result.id"：使用返回后的id
            - @Cacheable的key是不能用#result。
   
   > 注意：在@CachePut的使用中层级出现无法更新缓存的问题。后来排查发现错误在于JavaBean写的不规范。

- 组合注解 **@Caching**: 定义复杂的缓存规则                                                                       
    - @Caching注解可以让我们在一个方法或者类上同时指定多个Spring Cache相关的注解。其拥有三个属性：cacheable、put和evict，分别用于指定@Cacheable、@CachePut和@CacheEvict。
    - **当一个方法被@CachePut或者带有@Put的@Caching注解标注时，此方法一定会查询，因为@CachePut的含义就是重新查询，并更新写入缓存。** 因此当我们使用下面的注解标注按姓名查找时，查找一次过后再查找仍然会调用查找方法，就是@CachePut的原因。 
        ```java
        @Caching( // 配置复杂的缓存规则
            cacheable = {
                @Cacheable(value = "emp", key = "#lastName") // 以name查询还会去查询数据库：因为有@CachePut注解，所以这方法一定要执行的，@CachePut把方法执行的结果缓存到缓存
            },
            put = {
                @CachePut(value = "emp", key = "#result.id"), //方法结束时放入两份缓存到不同的key中
                @CachePut(value = "emp", key = "#result.email")
            }
        )
        public Employee getEmpByLastName(String lastName){
            return employeeMapper.getEmpByLastName(lastName);
        }
        ```                  
- **@CacheConfig** : 用于全局参数提取，标注在类上，表示类中的所有方法都具有某种缓存注解
    - 例如`@CacheConfig(cacheNames = "emp")`表示类中所有方法写入的缓存空间名字都是emp
- **@Primary**：将某个缓存管理器作为默认使用的缓存管理器

## 2 使用redis作为缓存中间件
- REmote DIctionary Server(Redis) 是一个由Salvatore Sanfilippo写的key-value存储系统。Redis是一个开源的使用ANSI C语言编写、遵守BSD协议、支持网络、可基于内存亦可持久化的日志型、Key-Value数据库，并提供多种语言的API。它通常被称为数据结构服务器，因为值（value）可以是 字符串(String), 哈希(Hash), 列表(list), 集合(sets) 和 有序集合(sorted sets)等类型。

### 2.1 RedisTemplate<K,V>类的配置
- **Spring 封装了RedisTemplate<K,V>对象来操作redis。** Spring在 org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration类下配置的两个RedisTemplate的Bean。
    - (1) RedisTemplate<Object, Object>：这个Bean使用JdkSerializationRedisSerializer进行序列化，即key, value需要实现Serializable接口，redis数据格式比较难懂，例如
　　  ![](https://img2018.cnblogs.com/blog/1701744/201906/1701744-20190617004113992-2138956492.png)
　　- (2) StringRedisTemplate，即RedisTemplate<String, String>：key和value都是String。当需要存储实体类时，需要先转为String，再存入Redis。一般转为Json格式的字符串，所以使用StringRedisTemplate，**需要手动将实体类转为Json格式**。
        ```java
        ValueOperations<String, String> valueTemplate = stringTemplate.opsForValue();
        Gson gson = new Gson();
        
        valueTemplate.set("StringKey1", "hello spring boot redis, String Redis");
        String value = valueTemplate.get("StringKey1");
        System.out.println(value);
        
        valueTemplate.set("StringKey2", gson.toJson(new Person("theName", 11)));
        Person person = gson.fromJson(valueTemplate.get("StringKey2"), Person.class);
        System.out.println(person);
        ```
        - ![](https://img2018.cnblogs.com/blog/1701744/201906/1701744-20190617210055854-1780139893.png)
### 2.2 搭建步骤
- 1、Docker安装redis环境
- 2、在SpringBoot中引入redis的starter`spring-boot-starter-data-redis`
- 3、在springboot主配置文件中配置redis
    ```properties
    # 配置redis，端口默认6379，默认无密码
    spring.redis.host=127.0.0.1
    ```
- 4、在需要用到redis的地方@Autowired注入template对象
    - StringRedisTemplate stringRedisTemplate; // 操作字符串 【比较常用】
    - RedisTemplate redisTemplate; //<Object, Object>，k-v都是对象
- 5、编写测试方法测试redis常见的五大数据类型：String,List,Set,Hash,ZSet(有序集合)
     *  stringRedisTemplate.opsForValue()[操作String（字符串）]
     *  stringRedisTemplate.opsForList()[操作List（列表）]
     *  stringRedisTemplate.opsForSet()[操作Set（集合）]
     *  stringRedisTemplate.opsForHash()[操作Hash（散列）]
     *  stringRedisTemplate.opsForZSet()[操作ZSet（有序集合）]
        ```java
        @SpringBootTest
        class Springboot14CacheApplicationTests {
            @Autowired
            EmployeeMapper employeeMapper;
        
            @Autowired
            StringRedisTemplate stringRedisTemplate; // 操作字符串
        
            @Autowired
            RedisTemplate redisTemplate; //<Object, Object>，k-v都是对象
        
        
            @Test // 测试redis操作String,List,Set,Hash,ZSet(有序集合)
            public void test1(){
                //stringRedisTemplate.opsForValue().append("msg", "hello");
                //String msg = stringRedisTemplate.opsForValue().get("msg");
                //System.out.println(msg);
                //stringRedisTemplate.opsForList().leftPush("mylist","1" );
                //stringRedisTemplate.opsForList().leftPush("mylist","2" );
                //stringRedisTemplate.opsForList().leftPush("mylist","3" );
        
            }
        }
        
        ```
- 6、使用自定义序列化器将查询到的对象以JSON格式写入redis
    - 实现<Object, Employee>的RedisTemplate：
        ```java
        @Configuration
        public class MyRedisConfig {
            @Bean // 序列化Employee
            public RedisTemplate<Object, Employee> empRedisTemplate(
                    RedisConnectionFactory redisConnectionFactory)
                    throws UnknownHostException {
                RedisTemplate<Object, Employee> template = new RedisTemplate<Object, Employee>();
                template.setConnectionFactory(redisConnectionFactory);
                Jackson2JsonRedisSerializer<Employee> ser = new Jackson2JsonRedisSerializer<Employee>(Employee.class);
                template.setDefaultSerializer(ser);
                return template;
            }
        }
        ```
    - 使用自定义的的RedisTemplate转换:
        ```java
        @Autowired
        RedisTemplate<Object, Employee> empRedisTemplate; // 自定义的RedisTemplate，负责Employee -> JSON
      
        @Test // 测试保存对象
            public void test2(){
                // 默认如果保存对象，使用jdk序列化机制，序列化后的数据保存到redis中
                Employee empById = employeeMapper.getEmpById(1);
                //redisTemplate.opsForValue().set("emp-01", empById);
                // 将数据以json方式保存，使用redisTemplate的Json序列化器
                empRedisTemplate.opsForValue().set("emp-01", empById);
            }
        ```
    - 效果：![kVdASi9](https://i.imgur.com/kVdASi9.jpg)

### 2.3 使用redis替代SpringBoot默认的缓存组件
- 1）引入redis的starter，容器中保存的是 RedisCacheManager；
- 2）RedisCacheManager 帮我们创建 RedisCache 来作为缓存组件；RedisCache通过操作redis缓存数据的
- 3）默认保存数据 k-v 都是Object；默认使用JDK的序列化保存的
        - ![EgHXP6r](https://i.imgur.com/EgHXP6r.jpg)
         * 1、引入了redis的starter，cacheManager变为 RedisCacheManager；
         * 2、默认创建的 RedisCacheManager 操作redis的时候使用的是 RedisTemplate<Object, Object>
         * 3、RedisTemplate<Object, Object> 默认使用jdk自带的序列化机制
- 4) 如何保存为json？——自定义CacheManager
    ```java
        @Configuration
        public class MyRedisConfig {
            @Bean // 负责Employee序列化JSON的RedisTemplate
            public RedisTemplate<Object, Employee> empRedisTemplate(RedisConnectionFactory redisConnectionFactory) throws UnknownHostException {
                RedisTemplate<Object, Employee> template = new RedisTemplate<Object, Employee>();
                template.setConnectionFactory(redisConnectionFactory);
                Jackson2JsonRedisSerializer<Employee> ser = new Jackson2JsonRedisSerializer<Employee>(Employee.class);
                template.setDefaultSerializer(ser); // 使用Jackson2JsonRedisSerializer作为序列化器
                return template;
            }
    
        // 自定义CacheManager来对缓存进行设置
        @Bean
        public CacheManager cacheManager(RedisTemplate<Object, Employee> empRedisTemplate) {
            RedisCacheConfiguration defaultCacheConfiguration =
                    RedisCacheConfiguration
                            .defaultCacheConfig()
                            // 设置key为String
                            .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(empRedisTemplate.getStringSerializer()))
                            // 设置value 为自动转Json的Object
                            .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(empRedisTemplate.getValueSerializer()))
                            // 不缓存null
                            .disableCachingNullValues()
                            // 缓存数据保存1小时
                            .entryTtl(Duration.ofHours(1));
            RedisCacheManager redisCacheManager =
                    RedisCacheManager.RedisCacheManagerBuilder
                            // Redis 连接工厂
                            .fromConnectionFactory(empRedisTemplate.getConnectionFactory())
                            // 缓存配置
                            .cacheDefaults(defaultCacheConfiguration)
                            // 配置同步修改或删除 put/evict
                            .transactionAware()
                            .build();
            return redisCacheManager;
        }
    }
    ```
- 5) 测试缓存效果：![NKhSsQH](https://i.imgur.com/NKhSsQH.jpg)

### 2.4 解决多个实体缓存redis后不能反序列化的问题(适用于 SpringBoot 1.x)
- 当为DeptService也添加缓存功能后, 还是使用上面的CacheManager和empRedisTemplate操作redis：
    ```java
    @Service
    public class DeptService {
        @Autowired
        DepartmentMapper departmentMapper;
    
        @Cacheable(cacheNames = "dept")
        public Department getDeptById(Integer id){
            System.out.println("查询部门getDeptById:" + id);
            Department deptById = departmentMapper.getDeptById(id);
            return  deptById;
        }
    }
    ```
- 缓存的数据虽然能以JSON存入redis，但是第二次从缓存中查询就无法反序列化为Department对象
    - ![9baFRic](https://i.imgur.com/9baFRic.jpg)
- 如何解决？ Dept和Emp各自使用自己的RedisTemplate和CacheManager。
    - `@Cacheable(cacheNames = "dept", cacheManager = "deptCacheManager")`
    - `@CacheConfig(cacheNames = "emp", cacheManager = "employeeCacheManager")`
        ```java
       @Configuration
       public class MyRedisConfig {
           @Bean // 负责Employee序列化JSON的RedisTemplate
           public RedisTemplate<Object, Employee> empRedisTemplate(RedisConnectionFactory redisConnectionFactory) throws UnknownHostException {
               RedisTemplate<Object, Employee> template = new RedisTemplate<Object, Employee>();
               template.setConnectionFactory(redisConnectionFactory);
               Jackson2JsonRedisSerializer<Employee> ser = new Jackson2JsonRedisSerializer<Employee>(Employee.class);
               template.setDefaultSerializer(ser); // 使用Jackson2JsonRedisSerializer作为序列化器
               return template;
           }
       
           @Bean// 负责Department序列化JSON的RedisTemplate
           public RedisTemplate<Object, Department> deptRedisTemplate(
                   RedisConnectionFactory redisConnectionFactory)
                   throws UnknownHostException {
               RedisTemplate<Object, Department> template = new RedisTemplate<Object, Department>();
               template.setConnectionFactory(redisConnectionFactory);
               Jackson2JsonRedisSerializer<Department> ser = new Jackson2JsonRedisSerializer<Department>(Department.class);
               template.setDefaultSerializer(ser);
               return template;
           }
       
           // 自定义CacheManager来对缓存进行设置
           // 负责Employee缓存的CacheManager
           @Bean
           @Primary // 必须有一个默认使用的CacheManager否则会报错
           public CacheManager employeeCacheManager(RedisTemplate<Object, Employee> empRedisTemplate) {
               RedisCacheConfiguration defaultCacheConfiguration =
                       RedisCacheConfiguration
                               .defaultCacheConfig()
                               // 设置key为String
                               .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(empRedisTemplate.getStringSerializer()))
                               // 设置value 为自动转Json的Object
                               .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(empRedisTemplate.getValueSerializer()))
                               // 不缓存null
                               .disableCachingNullValues()
                               // 缓存数据保存1小时
                               .entryTtl(Duration.ofHours(1));
               RedisCacheManager redisCacheManager =
                       RedisCacheManager.RedisCacheManagerBuilder
                               // Redis 连接工厂
                               .fromConnectionFactory(empRedisTemplate.getConnectionFactory())
                               // 缓存配置
                               .cacheDefaults(defaultCacheConfiguration)
                               // 配置同步修改或删除 put/evict
                               .transactionAware()
                               .build();
               return redisCacheManager;
           }
       
           // 负责Department缓存的CacheManager
           @Bean
           public CacheManager deptCacheManager(RedisTemplate<Object, Department> deptRedisTemplate) {
               RedisCacheConfiguration defaultCacheConfiguration =
                       RedisCacheConfiguration
                               .defaultCacheConfig()
                               // 设置key为String
                               .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(deptRedisTemplate.getStringSerializer()))
                               // 设置value 为自动转Json的Object
                               .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(deptRedisTemplate.getValueSerializer()))
                               // 不缓存null
                               .disableCachingNullValues()
                               // 缓存数据保存1小时
                               .entryTtl(Duration.ofHours(1));
               RedisCacheManager redisCacheManager =
                       RedisCacheManager.RedisCacheManagerBuilder
                               // Redis 连接工厂
                               .fromConnectionFactory(deptRedisTemplate.getConnectionFactory())
                               // 缓存配置
                               .cacheDefaults(defaultCacheConfiguration)
                               // 配置同步修改或删除 put/evict
                               .transactionAware()
                               .build();
               return redisCacheManager;
           }
       }
        ```
    
### 2.5 改进：使用<String ,Object>范型的CacheManager来统一管理所有类型的序列化
```java
@Configuration
public class MyRedisConfigImprove {

    @Bean
    public RedisTemplate<String, Object> template(RedisConnectionFactory factory) {
        // 创建RedisTemplate<String, Object>对象
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        // 配置连接工厂
        template.setConnectionFactory(factory);
        // 定义Jackson2JsonRedisSerializer序列化对象
        Jackson2JsonRedisSerializer<Object> jacksonSeial = new Jackson2JsonRedisSerializer<>(Object.class);
        ObjectMapper om = new ObjectMapper();
        // 指定要序列化的域，field,get和set,以及修饰符范围，ANY是都有包括private和public
        om.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        // 指定序列化输入的类型，类必须是非final修饰的，final修饰的类，比如String,Integer等会报异常
        om.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);
        jacksonSeial.setObjectMapper(om);
        StringRedisSerializer stringSerial = new StringRedisSerializer();
        // redis key 序列化方式使用stringSerial
        template.setKeySerializer(stringSerial);
        // redis value 序列化方式使用jackson
        template.setValueSerializer(jacksonSeial);
        // redis hash key 序列化方式使用stringSerial
        template.setHashKeySerializer(stringSerial);
        // redis hash value 序列化方式使用jackson
        template.setHashValueSerializer(jacksonSeial);
        template.afterPropertiesSet();
        return template;
    }

    /**
     * <p>SpringBoot配置redis作为默认缓存工具</p>
     * <p>SpringBoot 2.0 以上版本的配置</p>
     */
    @Bean
    public CacheManager cacheManager(RedisTemplate<String, Object> template) {
        RedisCacheConfiguration defaultCacheConfiguration =
                RedisCacheConfiguration
                        .defaultCacheConfig()
                        // 设置key为String
                        .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(template.getStringSerializer()))
                        // 设置value 为自动转Json的Object
                        .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(template.getValueSerializer()))
                        // 不缓存null
                        .disableCachingNullValues()
                        // 缓存数据保存1小时
                        .entryTtl(Duration.ofHours(1));
        RedisCacheManager redisCacheManager =
                RedisCacheManager.RedisCacheManagerBuilder
                        // Redis 连接工厂
                        .fromConnectionFactory(template.getConnectionFactory())
                        // 缓存配置
                        .cacheDefaults(defaultCacheConfiguration)
                        // 配置同步修改或删除 put/evict
                        .transactionAware()
                        .build();
        return redisCacheManager;
    }
}
```
- Spring配置的两个RedisTemplate都不太方便使用，所以**可以配置一个RedisTemplate<String,Object> 的Bean**，key使用String即可(包括Redis Hash 的key)，value存取Redis时默认使用Json格式转换。
- 参考：[SpringBoot + Redis：基本配置及使用 - 蔡昭凯 - 博客园](https://www.cnblogs.com/caizhaokai/p/11037610.html)

## 参考资料
- [SpringBoot缓存 | DanqingBlog](https://danqing-hub.github.io/2020/04/13/SpringBoot%E7%BC%93%E5%AD%98/#%EF%BC%881%EF%BC%89%E6%9F%A5%E8%AF%A21%E5%8F%B7%E5%91%98%E5%B7%A5%EF%BC%8C%E5%B9%B6%E6%8A%8A%E6%9F%A5%E8%AF%A2%E7%9A%84%E7%BB%93%E6%9E%9C%E6%94%BE%E5%88%B0%E7%BC%93%E5%AD%98%E4%B8%AD)
- [Redis 教程 | 菜鸟教程](https://www.runoob.com/redis/redis-tutorial.html)
- [AnotherRedisDesktopManager: Github国内镜像，供下载使用，有问题可移步到](https://gitee.com/qishibo/AnotherRedisDesktopManager)
- [Redis命令中心（Redis commands） -- Redis中国用户组（CRUG）](http://www.redis.cn/commands.html)
- [SpringBoot + Redis：基本配置及使用 - 蔡昭凯 - 博客园](https://www.cnblogs.com/caizhaokai/p/11037610.html)
- [spring-boot下CacheManager配置（1.5.x & 2.x对比）_Java_王大人的博客-CSDN博客](https://blog.csdn.net/lanmei618/article/details/80223763)





























