package com.thirteenash.service;

import com.thirteenash.dto.ProfileDTO;
import com.thirteenash.dto.ProfileRequestDTO;

public interface ProfileService {
    ProfileDTO getProfileByUserId(Long userId);
    ProfileDTO createProfile(Long userId, ProfileRequestDTO requestDTO);
    ProfileDTO updateProfile(Long userId, ProfileRequestDTO requestDTO);
}
