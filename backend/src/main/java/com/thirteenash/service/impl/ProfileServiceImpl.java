package com.thirteenash.service.impl;

import com.thirteenash.common.exception.BusinessException;
import com.thirteenash.dto.ProfileDTO;
import com.thirteenash.dto.ProfileRequestDTO;
import com.thirteenash.entity.StudentProfile;
import com.thirteenash.mapper.StudentProfileMapper;
import com.thirteenash.service.ProfileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class ProfileServiceImpl implements ProfileService {

    @Autowired
    private StudentProfileMapper studentProfileMapper;

    @Override
    public ProfileDTO getProfileByUserId(Long userId) {
        StudentProfile profile = studentProfileMapper.selectByUserId(userId);
        if (profile == null) {
            return null;
        }
        return convertToDTO(profile);
    }

    @Override
    public ProfileDTO createProfile(Long userId, ProfileRequestDTO requestDTO) {
        StudentProfile existingProfile = studentProfileMapper.selectByUserId(userId);
        if (existingProfile != null) {
            throw new BusinessException("个人信息已存在，无法再次创建");
        }

        StudentProfile profile = new StudentProfile();
        profile.setUserId(userId);
        profile.setClazzId(requestDTO.getClazzId());
        profile.setName(requestDTO.getName());
        profile.setGender(requestDTO.getGender());
        profile.setBirthDate(requestDTO.getBirthDate());
        profile.setPhone(requestDTO.getPhone());
        profile.setUniversity(requestDTO.getUniversity());
        profile.setEmail(requestDTO.getEmail());
        profile.setWechat(requestDTO.getWechat());
        profile.setQq(requestDTO.getQq());
        profile.setMessage(requestDTO.getMessage());
        profile.setAvatar(requestDTO.getAvatar());
        profile.setCreateTime(LocalDateTime.now());
        profile.setUpdateTime(LocalDateTime.now());

        int result = studentProfileMapper.insert(profile);
        if (result <= 0) {
            throw new BusinessException("创建个人信息失败");
        }

        return convertToDTO(profile);
    }

    @Override
    public ProfileDTO updateProfile(Long userId, ProfileRequestDTO requestDTO) {
        StudentProfile existingProfile = studentProfileMapper.selectByUserId(userId);
        if (existingProfile == null) {
            throw new BusinessException("个人信息不存在");
        }

        if (requestDTO.getClazzId() != null && !requestDTO.getClazzId().equals(existingProfile.getClazzId())) {
            throw new BusinessException("班级信息不可修改，请联系管理员");
        }

        if (requestDTO.getName() != null) {
            existingProfile.setName(requestDTO.getName());
        }
        if (requestDTO.getGender() != null) {
            existingProfile.setGender(requestDTO.getGender());
        }
        if (requestDTO.getBirthDate() != null) {
            existingProfile.setBirthDate(requestDTO.getBirthDate());
        }
        if (requestDTO.getPhone() != null) {
            existingProfile.setPhone(requestDTO.getPhone());
        }
        if (requestDTO.getUniversity() != null) {
            existingProfile.setUniversity(requestDTO.getUniversity());
        }
        if (requestDTO.getEmail() != null) {
            existingProfile.setEmail(requestDTO.getEmail());
        }
        if (requestDTO.getWechat() != null) {
            existingProfile.setWechat(requestDTO.getWechat());
        }
        if (requestDTO.getQq() != null) {
            existingProfile.setQq(requestDTO.getQq());
        }
        if (requestDTO.getMessage() != null) {
            existingProfile.setMessage(requestDTO.getMessage());
        }
        if (requestDTO.getAvatar() != null) {
            existingProfile.setAvatar(requestDTO.getAvatar());
        }

        existingProfile.setUpdateTime(LocalDateTime.now());

        int result = studentProfileMapper.updateById(existingProfile);
        if (result <= 0) {
            throw new BusinessException("更新个人信息失败");
        }

        return convertToDTO(existingProfile);
    }

    private ProfileDTO convertToDTO(StudentProfile profile) {
        ProfileDTO dto = new ProfileDTO();
        dto.setId(profile.getId());
        dto.setUserId(profile.getUserId());
        dto.setClazzId(profile.getClazzId());
        dto.setName(profile.getName());
        dto.setGender(profile.getGender());
        dto.setBirthDate(profile.getBirthDate());
        dto.setPhone(profile.getPhone());
        dto.setUniversity(profile.getUniversity());
        dto.setEmail(profile.getEmail());
        dto.setWechat(profile.getWechat());
        dto.setQq(profile.getQq());
        dto.setMessage(profile.getMessage());
        dto.setAvatar(profile.getAvatar());
        return dto;
    }
}
