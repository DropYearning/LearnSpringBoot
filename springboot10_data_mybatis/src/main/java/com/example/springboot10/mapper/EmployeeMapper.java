package com.example.springboot10.mapper;

import com.example.springboot10.bean.Employee;

// 一定要标注@Mapper或则会@MapperScan将接口配置到扫描路径下
// 这里没有标注是因为在SpringBootApplication中已经标注@MapperScan(value = "com.example.springboot10.mapper")
public interface  EmployeeMapper {
    public Employee getEmpById(Integer id);

    public void insertEmp(Employee employee);
}
