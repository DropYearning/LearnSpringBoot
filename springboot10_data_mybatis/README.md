#  LearnSpringBoot-07-SpringBoot-数据访问-整合Mybatis

[SpringBoot_权威教程__哔哩哔哩 (゜-゜)つロ 干杯~-bilibili](https://www.bilibili.com/video/BV1Et411Y7tQ?p=4)

## 1 整合Mybatis
- 1、引入Mybatis依赖
- 2、引入Druid方法同上一次
- 3、添加相关的数据表和相关Java Bean

## 2 配置Mybatis(注解法，例如Department表的操作)
- 4、编写Mapper SQL映射类：
    ```java
       @Mapper // 指定这是一个操作数据库的Mapper，只需是接口即可
       public interface DepartmentMapper {
       
           @Select("select * from department where id = #{id}")
           public Department getDeptById(Integer id);
       
           @Delete("delete from department where id=#{id}")
           public int deleteDeptById(Integer id);
       
           @Options(useGeneratedKeys = true, keyProperty = "id") // 配置使用自动生成主键
           @Insert("insert into department(department_name) values(#{departmentName}) ")
           public int insertDept(Department department);
       
           @Update("update department set department_name=#{departmentName} where id=#{id}")
           public int updateDept(Department department);
       }
    ```
- 5、编写Controller处理url请求：
    ```java
    @RestController
    public class DeptController {   
        @Autowired
        DepartmentMapper departmentMapper; // DepartmentMapper虽然是一个接口，但是在Mybatis下这样使用是可以的
    
        @GetMapping("/dept/{id}")
        public Department getDepartment(@PathVariable("id") Integer id){
            return departmentMapper.getDeptById(id);
        }
    
        @GetMapping("/dept")
        public Department insertDepartment(Department department){
            departmentMapper.insertDept(department);
            return department;
        }
    }
    ```
- 6、可以在容器中注入一个Mybatis配置类来实现例如兼容驼峰命名等设置：
    ```java
    @org.springframework.context.annotation.Configuration
    public class MyBatisConfig {
    @Bean
    public ConfigurationCustomizer configurationCustomizer(){
        return new ConfigurationCustomizer() {
            @Override
            public void customize(Configuration configuration) {
                configuration.setMapUnderscoreToCamelCase(true);
                }
            };
        }
    }
    ```
- 7、如果Mapper类数量过多，可以在标注有`@SpringBootApplication`的SpringBoot的启动类上标注`@MapperScan`来批量扫描：
    ```java
    @SpringBootApplication
    @MapperScan(value = "com.example.springboot10.mapper")
    public class Springboot10DataMybatisApplication {
    
        public static void main(String[] args) {
            SpringApplication.run(Springboot10DataMybatisApplication.class, args);
        }
    }
    ```
## 3 配置Mybatis(XML法，例如Employee表的操作)
- 1、在SpringBoot主配置文件中配置Mybatis的配置文件所在路径:
    ```yaml
    mybatis:
      # Mybatis全局配置文件的路径
      config-location: classpath:mybatis/mybatis-config.xml
      mapper-locations: classpath:mybatis/mapper/*.xml # 使用*通配，否则要写成YAML数组形式
    ```
- 2、编写全局Mybatis配置XML
    ```xml
    <?xml version="1.0" encoding="UTF-8" ?>
    <!DOCTYPE configuration
            PUBLIC "-//mybatis.org//DTD Config 3.0//EN"
            "http://mybatis.org/dtd/mybatis-3-config.dtd">
    <!--Mybatis全局配置文件-->
    <configuration>
        <settings>
            <setting name="mapUnderscoreToCamelCase" value="true"/>
        </settings>
    </configuration>
    ```
- 3、编写具体的Mapper配置XML
    ```xml
    <?xml version="1.0" encoding="UTF-8" ?>
    <!DOCTYPE mapper
            PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN"
            "http://mybatis.org/dtd/mybatis-3-mapper.dtd">
    <!--具体的Mapper配置文件-->
    <mapper namespace="com.example.springboot10.mapper.EmployeeMapper">
    
        <select id="getEmpById" resultType="com.example.springboot10.bean.Employee">
            select * from employee where id = #{id}
        </select>
    
        <select id="insertEmp" >
            insert into employee(lastName, email, gender, d_id) values(#{lastName}, #{gender},#{email},#{dId})
        </select>
    </mapper>
    ```
- 4、编写Controller类
    ```java
    @RestController
    public class MyController {
        @Autowired
        EmployeeMapper employeeMapper;
    
    
        @GetMapping("emp/{id}")
        public Employee getEmp(@PathVariable("id") Integer id){
            return employeeMapper.getEmpById(id);
        }
    
    }
    
    ```
- 5、启动测试
    - ![ntvLPDZ](https://i.imgur.com/ntvLPDZ.png)



## 参考资源
- [清官谈mysql中utf8和utf8mb4区别 | OurMySQL](http://ourmysql.com/archives/1402)
- [mybatis – MyBatis 3 | 配置](https://mybatis.org/mybatis-3/zh/configuration.html)



