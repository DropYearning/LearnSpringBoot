package com.example.springboot11.repository;


import com.example.springboot11.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

// JpaRepository<User,  Integer>：前者是要操作的表对应的实体类；后者是主键
public interface UserRepository  extends JpaRepository<User,  Integer> {

}
