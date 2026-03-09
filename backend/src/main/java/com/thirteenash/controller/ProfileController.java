package com.thirteenash.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.annotation.SaCheckRole;
import cn.dev33.satoken.annotation.SaMode;
import cn.dev33.satoken.stp.StpUtil;
import com.thirteenash.common.response.Result;
import com.thirteenash.dto.ProfileDTO;
import com.thirteenash.dto.ProfileRequestDTO;
import com.thirteenash.service.ProfileService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/profile")
@Tag(name = "个人信息管理", description = "用户个人信息的查询、创建和更新接口")
public class ProfileController {

    @Autowired
    private ProfileService profileService;

    @Operation(summary = "获取当前用户个人信息", description = "获取当前登录用户的个人信息")
    @SaCheckLogin
    @SaCheckRole(value = {"admin", "user"}, mode = SaMode.OR)
    @GetMapping("/me")
    public Result<ProfileDTO> getMyProfile() {
        Long userId = StpUtil.getLoginIdAsLong();
        ProfileDTO profile = profileService.getProfileByUserId(userId);
        return Result.success(profile);
    }

    @Operation(summary = "创建个人信息", description = "为当前登录用户创建个人信息档案")
    @SaCheckLogin
    @SaCheckRole(value = {"admin", "user"}, mode = SaMode.OR)
    @PostMapping
    public Result<ProfileDTO> createProfile(@RequestBody ProfileRequestDTO requestDTO) {
        Long userId = StpUtil.getLoginIdAsLong();
        ProfileDTO profile = profileService.createProfile(userId, requestDTO);
        return Result.success(profile);
    }

    @Operation(summary = "更新个人信息", description = "更新当前登录用户的个人信息档案")
    @SaCheckLogin
    @SaCheckRole(value = {"admin", "user"}, mode = SaMode.OR)
    @PutMapping
    public Result<ProfileDTO> updateProfile(@RequestBody ProfileRequestDTO requestDTO) {
        Long userId = StpUtil.getLoginIdAsLong();
        ProfileDTO profile = profileService.updateProfile(userId, requestDTO);
        return Result.success(profile);
    }
}
