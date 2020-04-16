package com.example.springboot.controller;

import com.example.springboot.dao.DepartmentDao;
import com.example.springboot.dao.EmployeeDao;
import com.example.springboot.entities.Department;
import com.example.springboot.entities.Employee;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;

/**
 * 处理员工管理相关请求的控制器
 */
@Controller
public class EmployController {

    @Autowired
    EmployeeDao employeeDao;

    @Autowired
    DepartmentDao departmentDao;

    // 查询所有员工信息，返回列表页面
    @GetMapping("/emps")
    public String list(Model model){
        Collection<Employee> employees = employeeDao.getAll();
        // 放在请求域中供页面显示
        model.addAttribute("emps", employees);
        return "/emps/list"; //thymeleaf默认会把路径拼在类路径classpath:/templates/下
    }

    // 跳转到添加员工的页面
    @GetMapping("/emp")
    public String toAddPage(Model model){
        Collection<Department> departments = departmentDao.getDepartments();
        model.addAttribute("depts",departments );
        return "/emps/add";
    }

    // 实现员工增加,SpringMVC自动将请求参数和入参属性进行一一绑定，要求请求参数的名字和入参的属性名一样
    @PostMapping("/emp")
    public String addEmp(Employee employee){
        System.out.println("保存的员工信息: " + employee);
        employeeDao.save(employee);
        // 添加完之后还是返回员工列表,redirect表示重定向到一个地址，forward表示转发到一个地址
        return "redirect:/emps"; // 如果直接写/emps并不是跳转到当前项目下的/emps
    }

    // 跳转修改页面，回显该员工信息
    @GetMapping("/emp/{id}")
    public String toEditPage(@PathVariable("id") Integer id, Model model){
        Employee employee = employeeDao.get(id);
        model.addAttribute("emp", employee);
        Collection<Department> departments = departmentDao.getDepartments();
        model.addAttribute("depts",departments );
        // 跳转到修改页面(相当于add页面是一个修改添加二合一的页面)
        return "emps/add";
    }


    // 修改员工信息：需要提交要修改的员工id
    @PutMapping("/emp")
    public String updateEmp(Employee employee){
        System.out.println("修改后的员工信息: " + employee);
        employeeDao.save(employee); // save方法会判断id是否存在来决定是新增还是修改
        return "redirect:/emps";
    }

    // 员工删除方法
    @DeleteMapping("/emp/{id}")
    public String deleteEmp(@PathVariable("id") Integer id){
        employeeDao.delete(id);
        return "redirect:/emps";
    }
}
