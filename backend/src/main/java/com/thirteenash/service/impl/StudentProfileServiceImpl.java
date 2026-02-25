package com.thirteenash.service.impl;

import com.thirteenash.common.exception.BusinessException;
import com.thirteenash.common.response.PageResponse;
import com.thirteenash.dto.CreateStudentProfileRequestDTO;
import com.thirteenash.dto.PageRequestDTO;
import com.thirteenash.dto.UpdateStudentProfileRequestDTO;
import com.thirteenash.entity.StudentProfile;
import com.thirteenash.mapper.StudentProfileMapper;
import com.thirteenash.service.IStudentProfileService;
import com.thirteenash.vo.StudentProfileVO;
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
    public StudentProfileVO createStudentProfile(CreateStudentProfileRequestDTO requestDTO) {
        StudentProfile studentProfile = new StudentProfile();
        studentProfile.setClazzId(requestDTO.getClazzId());
        studentProfile.setName(requestDTO.getBasic().getName());
        studentProfile.setGender(requestDTO.getBasic().getGender());
        studentProfile.setBirthDate(requestDTO.getBasic().getBirthDate());
        studentProfile.setUniversity(requestDTO.getBasic().getUniversity());
        studentProfile.setPhone(requestDTO.getContact().getPhone());
        studentProfile.setEmail(requestDTO.getContact().getEmail());
        studentProfile.setWechat(requestDTO.getContact().getWechat());
        studentProfile.setQq(requestDTO.getContact().getQq());
        studentProfile.setMessage(requestDTO.getMessage());
        studentProfile.setAvatar(requestDTO.getAvatar());
        studentProfile.setCreateTime(LocalDateTime.now());
        studentProfile.setUpdateTime(LocalDateTime.now());

        int result = studentProfileMapper.insert(studentProfile);

        
        if (result <= 0) {
            throw new BusinessException("创建同学信息失败");
        }

        return convertToVO(studentProfile);
    }

    @Override
    public List<StudentProfileVO> getStudentList() {
        List<StudentProfile> studentProfiles = studentProfileMapper.selectAll();
        List<StudentProfileVO> studentProfileVOs = new ArrayList<>();
        for (StudentProfile studentProfile : studentProfiles) {
            studentProfileVOs.add(convertToVO(studentProfile));
        }
        return studentProfileVOs;
    }

    @Override
    public StudentProfileVO getStudentById(Long id) {
        StudentProfile studentProfile = studentProfileMapper.selectById(id);
        if (studentProfile == null) {
            throw new BusinessException("同学信息不存在");
        }

        return convertToVO(studentProfile);
    }

    @Override
    public Boolean updateStudentProfile(Long id, UpdateStudentProfileRequestDTO requestDTO) {
        StudentProfile existingProfile = studentProfileMapper.selectById(id);
        if (existingProfile == null) {
            throw new BusinessException("同学信息不存在");
        }

        if (requestDTO.getBasic() != null) {
            if (requestDTO.getBasic().getName() != null) {
                existingProfile.setName(requestDTO.getBasic().getName());
            }
            if (requestDTO.getBasic().getGender() != null) {
                existingProfile.setGender(requestDTO.getBasic().getGender());
            }
            if (requestDTO.getBasic().getBirthDate() != null) {
                existingProfile.setBirthDate(requestDTO.getBasic().getBirthDate());
            }
            if (requestDTO.getBasic().getUniversity() != null) {
                existingProfile.setUniversity(requestDTO.getBasic().getUniversity());
            }
        }

        if (requestDTO.getContact() != null) {
            if (requestDTO.getContact().getPhone() != null) {
                existingProfile.setPhone(requestDTO.getContact().getPhone());
            }
            if (requestDTO.getContact().getEmail() != null) {
                existingProfile.setEmail(requestDTO.getContact().getEmail());
            }
            if (requestDTO.getContact().getWechat() != null) {
                existingProfile.setWechat(requestDTO.getContact().getWechat());
            }
            if (requestDTO.getContact().getQq() != null) {
                existingProfile.setQq(requestDTO.getContact().getQq());
            }
        }

        if (requestDTO.getClazzId() != null) {
            existingProfile.setClazzId(requestDTO.getClazzId());
        }

        if (requestDTO.getMessage() != null) {
            existingProfile.setMessage(requestDTO.getMessage());
        }

        if (requestDTO.getAvatar() != null) {
            existingProfile.setAvatar(requestDTO.getAvatar());
        }

        existingProfile.setUpdateTime(LocalDateTime.now());

        int result = studentProfileMapper.updateById(existingProfile);
        return result > 0;
    }

    @Override
    public Boolean deleteStudentProfile(Long id) {
        StudentProfile existingProfile = studentProfileMapper.selectById(id);
        if (existingProfile == null) {
            throw new BusinessException("同学信息不存在");
        }

        int result = studentProfileMapper.deleteById(id);
        return result > 0;
    }

    @Override
    public PageResponse<StudentProfileVO> getStudentListByPage(PageRequestDTO pageRequestDTO) {
        Integer page = pageRequestDTO.getPage();
        Integer size = pageRequestDTO.getSize();
        
        if (page == null) {
            page = 0;
        }
        if (size == null) {
            size = 10;
        }

        int offset = page * size;
        List<StudentProfile> studentProfiles = studentProfileMapper.selectByPage(offset, size);
        Long total = studentProfileMapper.countTotal();

        List<StudentProfileVO> studentProfileVOs = new ArrayList<>();
        for (StudentProfile studentProfile : studentProfiles) {
            studentProfileVOs.add(convertToVO(studentProfile));
        }

        return new PageResponse<>(studentProfileVOs, page, size, total);
    }

    private StudentProfileVO convertToVO(StudentProfile studentProfile) {
        StudentProfileVO vo = new StudentProfileVO();
        vo.setId(studentProfile.getId());
        vo.setClazzId(studentProfile.getClazzId());
        vo.setMessage(studentProfile.getMessage());
        vo.setAvatar(studentProfile.getAvatar());
        vo.setCreateTime(studentProfile.getCreateTime());

        StudentProfileVO.BasicInfo basic = new StudentProfileVO.BasicInfo();
        basic.setName(studentProfile.getName());
        basic.setGender(studentProfile.getGender());
        basic.setBirthDate(studentProfile.getBirthDate());
        basic.setUniversity(studentProfile.getUniversity());
        vo.setBasic(basic);

        StudentProfileVO.ContactInfo contact = new StudentProfileVO.ContactInfo();
        contact.setPhone(studentProfile.getPhone());
        contact.setEmail(studentProfile.getEmail());
        contact.setWechat(studentProfile.getWechat());
        contact.setQq(studentProfile.getQq());
        vo.setContact(contact);

        return vo;
    }
}
