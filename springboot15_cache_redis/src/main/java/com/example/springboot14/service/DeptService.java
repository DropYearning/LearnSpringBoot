package com.example.springboot14.service;

import com.example.springboot14.bean.Department;
import com.example.springboot14.mapper.DepartmentMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

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
