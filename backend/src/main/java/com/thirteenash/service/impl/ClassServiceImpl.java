package com.thirteenash.service.impl;

import com.thirteenash.common.exception.BusinessException;
import com.thirteenash.dto.AddClassMembersRequestDTO;
import com.thirteenash.dto.CreateClassRequestDTO;
import com.thirteenash.dto.UpdateClassRequestDTO;
import com.thirteenash.entity.ClassMember;
import com.thirteenash.entity.Clazz;
import com.thirteenash.mapper.ClassMemberMapper;
import com.thirteenash.mapper.ClassMapper;
import com.thirteenash.service.IClassService;
import com.thirteenash.vo.ClassInfoVO;
import com.thirteenash.vo.UserInfoVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 班级服务实现类
 */
@Service
public class ClassServiceImpl implements IClassService {

    @Autowired
    private ClassMapper classMapper;

    @Autowired
    private ClassMemberMapper classMemberMapper;

    @Override
    public ClassInfoVO createClass(CreateClassRequestDTO requestDTO) {
        // 创建班级实体
        Clazz clazz = new Clazz();
        BeanUtils.copyProperties(requestDTO, clazz);
        clazz.setCreateTime(LocalDateTime.now());
        clazz.setUpdateTime(LocalDateTime.now());

        // 插入数据库
        int result = classMapper.insert(clazz);
        if (result <= 0) {
            throw new BusinessException("创建班级失败");
        }

        // 构造响应
        ClassInfoVO classInfoVO = new ClassInfoVO();
        BeanUtils.copyProperties(clazz, classInfoVO);
        return classInfoVO;
    }

    @Override
    public List<ClassInfoVO> getClassList() {
        List<Clazz> clazzList = classMapper.selectAll();
        List<ClassInfoVO> classInfoVOList = new ArrayList<>();
        for (Clazz clazz : clazzList) {
            ClassInfoVO classInfoVO = new ClassInfoVO();
            BeanUtils.copyProperties(clazz, classInfoVO);
            classInfoVOList.add(classInfoVO);
        }
        return classInfoVOList;
    }

    @Override
    public ClassInfoVO getClassById(Long classId) {
        Clazz clazz = classMapper.selectById(classId);
        if (clazz == null) {
            throw new BusinessException("班级不存在");
        }

        ClassInfoVO classInfoVO = new ClassInfoVO();
        BeanUtils.copyProperties(clazz, classInfoVO);
        return classInfoVO;
    }

    @Override
    public Boolean updateClass(Long classId, UpdateClassRequestDTO requestDTO) {
        // 查找班级
        Clazz clazz = classMapper.selectById(classId);
        if (clazz == null) {
            throw new BusinessException("班级不存在");
        }

        // 更新班级信息
        BeanUtils.copyProperties(requestDTO, clazz);
        clazz.setUpdateTime(LocalDateTime.now());

        int result = classMapper.updateById(clazz);
        return result > 0;
    }

    @Override
    public Boolean deleteClass(Long classId) {
        // 查找班级
        Clazz clazz = classMapper.selectById(classId);
        if (clazz == null) {
            throw new BusinessException("班级不存在");
        }

        int result = classMapper.deleteById(classId);
        return result > 0;
    }

    @Override
    public List<UserInfoVO> getClassMembers(Long classId) {
        // 查找班级
        Clazz clazz = classMapper.selectById(classId);
        if (clazz == null) {
            throw new BusinessException("班级不存在");
        }

        return classMemberMapper.selectByClassId(classId);
    }

    @Override
    public Boolean addClassMembers(Long classId, AddClassMembersRequestDTO requestDTO) {
        // 查找班级
        Clazz clazz = classMapper.selectById(classId);
        if (clazz == null) {
            throw new BusinessException("班级不存在");
        }

        // 构造班级成员列表
        List<ClassMember> classMembers = new ArrayList<>();
        for (Long userId : requestDTO.getUserIds()) {
            // 检查是否已经是班级成员
            ClassMember existingMember = classMemberMapper.selectByClassIdAndUserId(classId, userId);
            if (existingMember == null) {
                ClassMember classMember = new ClassMember();
                classMember.setClassId(classId);
                classMember.setUserId(userId);
                classMember.setJoinTime(LocalDateTime.now());
                classMembers.add(classMember);
            }
        }

        if (classMembers.isEmpty()) {
            return true;
        }

        // 批量添加班级成员
        int result = classMemberMapper.batchInsert(classMembers);
        return result > 0;
    }

    @Override
    public Boolean removeClassMember(Long classId, Long userId) {
        // 查找班级
        Clazz clazz = classMapper.selectById(classId);
        if (clazz == null) {
            throw new BusinessException("班级不存在");
        }

        // 检查是否是班级成员
        ClassMember existingMember = classMemberMapper.selectByClassIdAndUserId(classId, userId);
        if (existingMember == null) {
            throw new BusinessException("用户不是该班级成员");
        }

        // 删除班级成员
        int result = classMemberMapper.deleteByClassIdAndUserId(classId, userId);
        return result > 0;
    }
}
