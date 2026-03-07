package com.thirteenash.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.annotation.SaCheckRole;
import cn.dev33.satoken.annotation.SaMode;
import cn.dev33.satoken.stp.StpUtil;
import com.thirteenash.common.response.Result;
import com.thirteenash.dto.ProfileDTO;
import com.thirteenash.dto.ProfileRequestDTO;
import com.thirteenash.service.ProfileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/profile")
public class ProfileController {

    @Autowired
    private ProfileService profileService;

    @SaCheckLogin
    @SaCheckRole(value = {"admin", "user"}, mode = SaMode.OR)
    @GetMapping("/me")
    public Result<ProfileDTO> getMyProfile() {
        Long userId = StpUtil.getLoginIdAsLong();
        ProfileDTO profile = profileService.getProfileByUserId(userId);
        return Result.success(profile);
    }

    @SaCheckLogin
    @SaCheckRole(value = {"admin", "user"}, mode = SaMode.OR)
    @PostMapping
    public Result<ProfileDTO> createProfile(@RequestBody ProfileRequestDTO requestDTO) {
        Long userId = StpUtil.getLoginIdAsLong();
        ProfileDTO profile = profileService.createProfile(userId, requestDTO);
        return Result.success(profile);
    }

    @SaCheckLogin
    @SaCheckRole(value = {"admin", "user"}, mode = SaMode.OR)
    @PutMapping
    public Result<ProfileDTO> updateProfile(@RequestBody ProfileRequestDTO requestDTO) {
        Long userId = StpUtil.getLoginIdAsLong();
        ProfileDTO profile = profileService.updateProfile(userId, requestDTO);
        return Result.success(profile);
    }
}
