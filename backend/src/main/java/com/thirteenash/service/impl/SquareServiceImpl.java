package com.thirteenash.service.impl;

import com.thirteenash.common.exception.BusinessException;
import com.thirteenash.common.exception.ClassException;
import com.thirteenash.dto.SquareStudentDTO;
import com.thirteenash.entity.StudentProfile;
import com.thirteenash.mapper.StudentProfileMapper;
import com.thirteenash.mapper.AuthMapper;
import com.thirteenash.service.SquareService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class SquareServiceImpl implements SquareService {

    @Autowired
    private StudentProfileMapper studentProfileMapper;

    @Autowired
    private AuthMapper authMapper;

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

        if(clazzId == 12){
            throw new ClassException("您的班级为“其他”，为保护同学隐私，这里不会显示其他班级的同学信息，很感谢你填写的内容，我会永远记住你哒~（如果您是因为错误填写看到这个页面，请联系管理员修改您的班级~）");
        }

        List<StudentProfile> studentProfiles = studentProfileMapper.selectByClazzId(clazzId);

        List<SquareStudentDTO> result = new ArrayList<>();
        for (StudentProfile profile : studentProfiles) {
            SquareStudentDTO dto = new SquareStudentDTO();
            // 用户名来自 users 表
            if (profile.getUserId() != null) {
                Integer userIdInt = profile.getUserId().intValue();
                if (userIdInt > 0) {
                    var user = authMapper.selectById(userIdInt);
                    if (user != null) {
                        dto.setUsername(user.getUsername());
                    }
                }
            }
            dto.setName(profile.getName());
            dto.setGender(profile.getGender());
            dto.setBirthDate(profile.getBirthDate());
            dto.setPhone(profile.getPhone());
            dto.setQq(profile.getQq());
            dto.setUniversity(profile.getUniversity());
            result.add(dto);
        }

        return result;
    }
}
