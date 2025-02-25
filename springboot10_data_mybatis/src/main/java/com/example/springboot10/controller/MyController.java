package com.example.springboot10.controller;

import com.example.springboot10.bean.Department;
import com.example.springboot10.bean.Employee;
import com.example.springboot10.mapper.DepartmentMapper;
import com.example.springboot10.mapper.EmployeeMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MyController {

    @Autowired
    DepartmentMapper departmentMapper; // DepartmentMapper虽然是一个接口，但是在Mybatis下这样使用是可以的

    @Autowired
    EmployeeMapper employeeMapper;

    @GetMapping("/dept/{id}")
    public Department getDepartment(@PathVariable("id") Integer id){
        return departmentMapper.getDeptById(id);
    }

    @GetMapping("/dept")
    public Department insertDepartment(Department department){
        departmentMapper.insertDept(department);
        return department;
    }

    @GetMapping("emp/{id}")
    public Employee getEmp(@PathVariable("id") Integer id){
        return employeeMapper.getEmpById(id);
    }



}
