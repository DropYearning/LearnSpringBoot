package com.example.springboot14.controller;

import com.example.springboot14.bean.Department;
import com.example.springboot14.service.DeptService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DeptController {

    @Autowired
    DeptService deptService;

    @GetMapping("/dept/{id}")
    public Department getDept( @PathVariable("id") Integer id){
        Department deptById = deptService.getDeptById(id);
        return deptById;
    }
}
