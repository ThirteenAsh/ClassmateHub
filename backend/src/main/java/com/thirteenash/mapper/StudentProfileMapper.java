package com.thirteenash.mapper;

import com.thirteenash.entity.StudentProfile;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface StudentProfileMapper {
    StudentProfile selectById(Long id);

    List<StudentProfile> selectAll();

    int insert(StudentProfile studentProfile);

    int updateById(StudentProfile studentProfile);

    int deleteById(Long id);

    List<StudentProfile> selectByPage(@Param("offset") int offset, @Param("pageSize") int pageSize);

    Long selectCount();

    Long countTotal();
}