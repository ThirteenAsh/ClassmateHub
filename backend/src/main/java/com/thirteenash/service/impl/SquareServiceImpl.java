package com.thirteenash.service.impl;

import com.thirteenash.common.exception.BusinessException;
import com.thirteenash.dto.SquareStudentDTO;
import com.thirteenash.entity.StudentProfile;
import com.thirteenash.mapper.StudentProfileMapper;
import com.thirteenash.service.SquareService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class SquareServiceImpl implements SquareService {

    @Autowired
    private StudentProfileMapper studentProfileMapper;

    @Override
    public List<SquareStudentDTO> getSquareStudents(Long userId) {
        StudentProfile userProfile = studentProfileMapper.selectByUserId(userId);
        if (userProfile == null) {
            throw new BusinessException("请先完善个人信息");
        }

        Long clazzId = userProfile.getClazzId();
        if (clazzId == null) {
            throw new BusinessException("请先完善个人信息");
        }

        List<StudentProfile> studentProfiles = studentProfileMapper.selectByClazzId(clazzId);

        List<SquareStudentDTO> result = new ArrayList<>();
        for (StudentProfile profile : studentProfiles) {
            SquareStudentDTO dto = new SquareStudentDTO();
            dto.setName(profile.getName());
            dto.setGender(profile.getGender());
            dto.setBirthDate(profile.getBirthDate());
            dto.setPhone(profile.getPhone());
            dto.setUniversity(profile.getUniversity());
            result.add(dto);
        }

        return result;
    }
}
