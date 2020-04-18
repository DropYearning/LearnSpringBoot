package com.example.springboot10.mapper;

import com.example.springboot10.bean.Department;
import org.apache.ibatis.annotations.*;

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
