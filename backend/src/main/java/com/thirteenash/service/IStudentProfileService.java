package com.thirteenash.service;

import com.thirteenash.common.response.PageResponse;
import com.thirteenash.dto.CreateStudentProfileRequestDTO;
import com.thirteenash.dto.StudentPageQueryDTO;
import com.thirteenash.dto.UpdateStudentProfileRequestDTO;
import com.thirteenash.vo.StudentProfileVO;
import com.thirteenash.vo.StudentStatisticsVO;

public interface IStudentProfileService {

    //懒得写注释
    StudentProfileVO createStudentProfile(CreateStudentProfileRequestDTO requestDTO);

    StudentProfileVO getStudentById(Long id);

    Boolean updateStudentProfile(Long id, UpdateStudentProfileRequestDTO requestDTO);

    Boolean deleteStudentProfile(Long id);

    PageResponse<StudentProfileVO> getStudentListByPage(StudentPageQueryDTO queryDTO);

    /**
     * 获取同学统计信息（班级分布、性别比例、总数等）
     */
    StudentStatisticsVO getStudentStatistics();
}