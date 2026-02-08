package com.thirteenash.service.impl;

import com.thirteenash.common.exception.BusinessException;
import com.thirteenash.entity.StudentProfile;
import com.thirteenash.mapper.StudentProfileMapper;
import com.thirteenash.service.IStudentProfileService;
import com.thirteenash.vo.StudentProfileVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 同学信息服务实现类
 */
@Service
public class StudentProfileServiceImpl implements IStudentProfileService {

    @Autowired
    private StudentProfileMapper studentProfileMapper;

    @Override
    public List<StudentProfileVO> getStudentList() {
        List<StudentProfile> studentProfiles = studentProfileMapper.selectAll();
        List<StudentProfileVO> studentProfileVOs = new ArrayList<>();
        for (StudentProfile studentProfile : studentProfiles) {
            StudentProfileVO studentProfileVO = new StudentProfileVO();
            BeanUtils.copyProperties(studentProfile, studentProfileVO);
            studentProfileVOs.add(studentProfileVO);
        }
        return studentProfileVOs;
    }

    @Override
    public StudentProfileVO getStudentById(Long userId) {
        StudentProfile studentProfile = studentProfileMapper.selectByUserId(userId);
        if (studentProfile == null) {
            throw new BusinessException("同学信息不存在");
        }

        StudentProfileVO studentProfileVO = new StudentProfileVO();
        BeanUtils.copyProperties(studentProfile, studentProfileVO);
        return studentProfileVO;
    }

    @Override
    public Boolean updateStudentProfile(Long userId, StudentProfile studentProfile) {
        // 查找同学信息
        StudentProfile existingProfile = studentProfileMapper.selectByUserId(userId);
        if (existingProfile == null) {
            // 如果不存在，创建新的同学信息
            studentProfile.setUserId(userId);
            studentProfile.setCreateTime(LocalDateTime.now());
            studentProfile.setUpdateTime(LocalDateTime.now());
            int result = studentProfileMapper.insert(studentProfile);
            return result > 0;
        } else {
            // 如果存在，更新同学信息
            studentProfile.setId(existingProfile.getId());
            studentProfile.setUserId(userId);
            studentProfile.setCreateTime(existingProfile.getCreateTime());
            studentProfile.setUpdateTime(LocalDateTime.now());
            int result = studentProfileMapper.updateByUserId(studentProfile);
            return result > 0;
        }
    }

    @Override
    public Boolean deleteStudentProfile(Long userId) {
        // 查找同学信息
        StudentProfile existingProfile = studentProfileMapper.selectByUserId(userId);
        if (existingProfile == null) {
            throw new BusinessException("同学信息不存在");
        }

        // 删除同学信息
        int result = studentProfileMapper.deleteByUserId(userId);
        return result > 0;
    }
}
