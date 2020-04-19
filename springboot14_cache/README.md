#  LearnSpringBoot-SpringBoot整合-缓存

[SpringBoot_权威教程__哔哩哔哩 (゜-゜)つロ 干杯~-bilibili](https://www.bilibili.com/video/BV1Et411Y7tQ?p=4)

## 1 JSR107

- JSR是Java Specification Requests 的缩写 ，Java规范请求，故名思议提交Java规范
-  JSR-107呢就是关于如何使用缓存的规范
- JSR-107定义的五个概念
    - ![HC8XiKd](https://i.imgur.com/HC8XiKd.png)
    - **CachingProvider**：定义了建立，配置，得到，管理和控制多个CacheManager，一个应用在运行时可以访问多个CachingProvider
    - **CacheManager**：它定义了建立，配置，得到，管理和控制多个有着唯一名字的Cache ，一个CacheManager被包含在单一的CachingProvider.
    - **Cache**：Cache是一个Map类型的数据结构，用来存储基于键的数据，很多方面都像java.util.Map数据类型。一个Cache仅被一个CacheManager所拥有
    - **Entry**：Entry是一个存储在Cache中的key-value对
    - **ExpiryPolicy**：缓存有效期。不是所有的数据都一直存在缓存中不改变的，为缓存的数据添加过期的策略会让你的缓存更加灵活和高效。
## 2 Spring缓存抽象
- ![89CsMVQ](https://i.imgur.com/89CsMVQ.png)
- Spring从3.1开始定义了`org.springframework.cache.Cache` 和`org.springframework.cache.CacheManager`接口来统一不同的缓存技术； 并支持**使用JCache（JSR-107）注解简化我们开发**

## 3 搭建SpringBoot缓存的基本环境
- 1、创建需要用到的数据库表
- 2、创建javabean来封装数据
- 3、整合Mybatis操作数据库
    - 3.1 配置数据源（springboot主配置文件中配置datasource）
        ```properties
        # 配置数据源信息
        spring.datasource.url=jdbc:mysql://localhost:3307/spring_cache
        spring.datasource.data-username=root
        spring.datasource.password=123456
        #spring.datasource.driver-class-name=com.mysql.jdbc.Driver  # 驱动可以不写，默认会根据url自动判断
        ```
    - 3.2 @MapperScan在SpringBootApplication上指定需要扫描的mapper接口所在的包
    - 3.3 编写相应的Mapper类，使用注解添加SQL语句
    - 3.4 测试Mybatis能否使用
- 4、快速体验缓存使用
    - 4.1 在SpringBootApplication上开启基于注解的缓存@EnableCaching
    - 4.2 在业务层Service类中对应的方法上标注缓存注解,开启SpringBoot的缓存

        
## 5 缓存注解

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

### 5.1 缓存注解支持的SpEL表达式
- ![8fnEee7](https://i.imgur.com/8fnEee7.png)

## 6 缓存工作原理
### 6.1 缓存的自动配置
- 1、缓存的自动配置类`CacheAutoConfiguration`，导入了`CacheConfigurationImportSelector`,从而导入缓存的配置类
- 2、缓存的配置类：
    - rg.springframework.boot.autoconfigure.cache.GenericCacheConfiguration
   *   org.springframework.boot.autoconfigure.cache.JCacheCacheConfiguration
   *   org.springframework.boot.autoconfigure.cache.EhCacheCacheConfiguration
   *   org.springframework.boot.autoconfigure.cache.HazelcastCacheConfiguration
   *   org.springframework.boot.autoconfigure.cache.InfinispanCacheConfiguration
   *   org.springframework.boot.autoconfigure.cache.CouchbaseCacheConfiguration
   *   org.springframework.boot.autoconfigure.cache.RedisCacheConfiguration
   *   org.springframework.boot.autoconfigure.cache.CaffeineCacheConfiguration
   *   org.springframework.boot.autoconfigure.cache.GuavaCacheConfiguration
   *   org.springframework.boot.autoconfigure.cache.SimpleCacheConfiguration【默认】
   *   org.springframework.boot.autoconfigure.cache.NoOpCacheConfiguration
- 3、默认生效的配置类：`SimpleCacheConfiguration`
- 4、在容器中注册了一个CacheManager：ConcurrentMapCacheManager
- 5、可以获取和创建ConcurrentMapCache类型的缓存组件；他的作用将数据保存在ConcurrentMap中；

> 默认情况下SpringBoot采用ConcurrentMapCacheManager作为缓存组件，并将数据保存在一个ConcurrentMap中。
>> 而实际开发中经常用到的是一些**缓存中间件**，例如MemCached和Redis，这些缓存中间件

### 6.2 缓存的工作流程
以Cacheable为例，运行流程如下：
* 1、方法运行之前，先去查询Cache（缓存组件），按照cacheNames指定的名字获取；（CacheManager先获取相应的缓存），第一次获取缓存如果没有Cache组件会自动创建。
* 2、去Cache中根据key查找缓存的内容，默认的key就是方法的参数；
    - key是按照某种策略生成的；默认是使用keyGenerator生成的，默认使用SimpleKeyGenerator生成key；
    - SimpleKeyGenerator生成key的默认策略如下：
        - 如果没有参数；key=new SimpleKey()；
        - 如果有一个参数：key=参数的值
        - 如果有多个参数：key=new SimpleKey(params)；
    - 3、没有查到缓存就调用目标方法进行查询
    - 4、将目标方法返回的结果，放进缓存中供再次使用
- 总结：@Cacheable标注的方法执行之前先来检查缓存中有没有这个数据，默认按照参数的值作为key去查询缓存，如果没有就运行方法并将结果放入缓存；以后再来调用就可以直接使用缓存中的数据；
- 核心：
    * 1）、使用CacheManager【ConcurrentMapCacheManager】按照名字得到Cache【ConcurrentMapCache】组件
    * 2）、key使用keyGenerator生成的，默认是SimpleKeyGenerator

## 参考资料
- [SpringBoot缓存 | DanqingBlog](https://danqing-hub.github.io/2020/04/13/SpringBoot%E7%BC%93%E5%AD%98/#%EF%BC%881%EF%BC%89%E6%9F%A5%E8%AF%A21%E5%8F%B7%E5%91%98%E5%B7%A5%EF%BC%8C%E5%B9%B6%E6%8A%8A%E6%9F%A5%E8%AF%A2%E7%9A%84%E7%BB%93%E6%9E%9C%E6%94%BE%E5%88%B0%E7%BC%93%E5%AD%98%E4%B8%AD)

































