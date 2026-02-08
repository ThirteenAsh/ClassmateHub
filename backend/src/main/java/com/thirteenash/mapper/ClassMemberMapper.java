package com.thirteenash.mapper;

import com.thirteenash.entity.ClassMember;
import com.thirteenash.vo.UserInfoVO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 班级成员Mapper
 */
@Mapper
public interface ClassMemberMapper {
    /**
     * 添加班级成员
     */
    int insert(ClassMember classMember);

    /**
     * 批量添加班级成员
     */
    int batchInsert(List<ClassMember> classMembers);

    /**
     * 根据班级ID和用户ID删除班级成员
     */
    int deleteByClassIdAndUserId(Long classId, Long userId);

    /**
     * 根据班级ID查询班级成员列表
     */
    List<UserInfoVO> selectByClassId(Long classId);

    /**
     * 根据班级ID和用户ID查询班级成员
     */
    ClassMember selectByClassIdAndUserId(Long classId, Long userId);
}