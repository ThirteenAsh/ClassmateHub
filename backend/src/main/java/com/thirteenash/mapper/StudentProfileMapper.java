package com.thirteenash.mapper;

import com.thirteenash.entity.StudentProfile;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 同学信息Mapper
 */
@Mapper
public interface StudentProfileMapper {
    /**
     * 根据用户ID查询同学信息
     */
    StudentProfile selectByUserId(Long userId);

    /**
     * 查询所有同学信息
     */
    List<StudentProfile> selectAll();

    /**
     * 插入同学信息
     */
    int insert(StudentProfile studentProfile);

    /**
     * 更新同学信息
     */
    int updateByUserId(StudentProfile studentProfile);

    /**
     * 删除同学信息
     */
    int deleteByUserId(Long userId);
}