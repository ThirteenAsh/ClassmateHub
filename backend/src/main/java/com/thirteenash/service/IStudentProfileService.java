package com.thirteenash.service;

import com.thirteenash.common.response.PageResponse;
import com.thirteenash.dto.CreateStudentProfileRequestDTO;
import com.thirteenash.dto.PageRequestDTO;
import com.thirteenash.dto.UpdateStudentProfileRequestDTO;
import com.thirteenash.vo.StudentProfileVO;

import java.util.List;

public interface IStudentProfileService {
    StudentProfileVO createStudentProfile(CreateStudentProfileRequestDTO requestDTO);

    List<StudentProfileVO> getStudentList();

    StudentProfileVO getStudentById(Long id);

    Boolean updateStudentProfile(Long id, UpdateStudentProfileRequestDTO requestDTO);

    Boolean deleteStudentProfile(Long id);

    PageResponse<StudentProfileVO> getStudentListByPage(PageRequestDTO pageRequestDTO);
}