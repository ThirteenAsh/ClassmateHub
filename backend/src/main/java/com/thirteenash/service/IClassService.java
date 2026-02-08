package com.thirteenash.service;

import com.thirteenash.dto.AddClassMembersRequestDTO;
import com.thirteenash.dto.CreateClassRequestDTO;
import com.thirteenash.dto.UpdateClassRequestDTO;
import com.thirteenash.entity.Clazz;
import com.thirteenash.vo.ClassInfoVO;
import com.thirteenash.vo.UserInfoVO;

import java.util.List;

/**
 * 班级服务接口
 */
public interface IClassService {
    /**
     * 创建班级
     */
    ClassInfoVO createClass(CreateClassRequestDTO requestDTO);

    /**
     * 获取班级列表
     */
    List<ClassInfoVO> getClassList();

    /**
     * 获取班级详情
     */
    ClassInfoVO getClassById(Long classId);

    /**
     * 更新班级信息
     */
    Boolean updateClass(Long classId, UpdateClassRequestDTO requestDTO);

    /**
     * 删除班级
     */
    Boolean deleteClass(Long classId);

    /**
     * 获取班级成员列表
     */
    List<UserInfoVO> getClassMembers(Long classId);

    /**
     * 添加班级成员
     */
    Boolean addClassMembers(Long classId, AddClassMembersRequestDTO requestDTO);

    /**
     * 移除班级成员
     */
    Boolean removeClassMember(Long classId, Long userId);
}