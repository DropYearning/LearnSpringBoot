package com.example.springboot11.entity;

// 使用JPA注解配置映射关系

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.persistence.*;

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
