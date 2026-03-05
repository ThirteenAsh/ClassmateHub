package com.thirteenash.mapper;

import com.thirteenash.dto.ClassStudentCountDTO;
import com.thirteenash.dto.GenderCountDTO;
import com.thirteenash.entity.StudentProfile;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface StudentProfileMapper {

    //懒得写注释了，自己看方法名猜吧
    StudentProfile selectById(Long id);

    List<StudentProfile> selectAll();

    int insert(StudentProfile studentProfile);

    int updateById(StudentProfile studentProfile);

    int deleteById(Long id);

    List<StudentProfile> selectByPage(@Param("offset") int offset, @Param("pageSize") int pageSize);

    Long selectCount();

    Long countTotal();

    /**
     * 统计每个班级的同学数量
     */
    List<ClassStudentCountDTO> countStudentsByClass();

    /**
     * 统计各性别的同学数量
     */
    List<GenderCountDTO> countStudentsByGender();

    /**
     * 统计指定班级的同学数量
     */
    Long countByClazzId(@Param("clazzId") Long clazzId);
}