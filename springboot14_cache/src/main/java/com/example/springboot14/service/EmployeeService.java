package com.example.springboot14.service;

import com.example.springboot14.bean.Employee;
import com.example.springboot14.mapper.EmployeeMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.*;
import org.springframework.stereotype.Service;

@Service
@CacheConfig(cacheNames = "emp")
public class EmployeeService {

    @Autowired
    EmployeeMapper employeeMapper;

    //@Cacheable(cacheNames = "emp", key = "#root.methodName +'[' + #id + ']'", condition = "#id>0", unless = "#result == null") // 将该方法的运行结果进行缓存，以后再要相同的数据，直接从缓存中获取数据，不用调用方法
    //@Cacheable(cacheNames = "emp", keyGenerator = "myKeyGenerator", condition = "#id>0", unless = "#result == null") // 将该方法的运行结果进行缓存，以后再要相同的数据，直接从缓存中获取数据，不用调用方法
    @Cacheable(cacheNames ="emp") // 将该方法的运行结果进行缓存，以后再要相同的数据，直接从缓存中获取数据，不用调用方法
    public Employee getEmp(Integer id){
        System.out.println("查询" + id + "号员工");
        Employee empById = employeeMapper.getEmpById(id);
        return empById;
    }

    @CachePut(cacheNames ="emp",key = "#result.id") // key = "#result.id"：使用返回后的id
    public Employee updateEmp(Employee employee){
        System.out.println("updateEmp:" +employee);
        employeeMapper.updateEmp(employee);
        return  employee; // #result即此处返回的employee
    }


    @CacheEvict(value = "emp", key = "#id", allEntries = true)
    public void deleteEmp(Integer id){
        System.out.println("deleteEmp:" +id); //模拟打印
        // 因为数据表数据较少，此处就不实际删除了(因此实际上只是清除了缓存)
    }

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
}
