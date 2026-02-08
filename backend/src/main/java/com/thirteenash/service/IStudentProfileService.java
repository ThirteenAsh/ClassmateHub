package com.thirteenash.service;

import com.thirteenash.entity.StudentProfile;
import com.thirteenash.vo.StudentProfileVO;

import java.util.List;

/**
 * 同学信息服务接口
 */
public interface IStudentProfileService {
    /**
     * 获取同学列表
     */
    List<StudentProfileVO> getStudentList();

    /**
     * 获取同学详情
     */
    StudentProfileVO getStudentById(Long userId);

    /**
     * 更新同学信息
     */
    Boolean updateStudentProfile(Long userId, StudentProfile studentProfile);

    /**
     * 删除同学信息
     */
    Boolean deleteStudentProfile(Long userId);
}