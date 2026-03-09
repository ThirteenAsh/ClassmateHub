package com.thirteenash.controller;

import com.thirteenash.common.exception.BusinessException;
import com.thirteenash.common.response.Result;
import com.thirteenash.entity.StudentProfile;
import com.thirteenash.mapper.AuthMapper;
import com.thirteenash.mapper.StudentProfileMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/v1/admin")
@Tag(name = "管理员接口", description = "管理员专用接口，包括修改用户密码、设置管理员等")
public class AdminController {

    @Autowired
    private StudentProfileMapper studentProfileMapper;

    @Autowired
    private AuthMapper authMapper;

    /**
     * 直接修改用户密码
     * @param request 包含学生 id 和新密码的请求
     * @return 操作结果
     */
    @Operation(summary = "修改用户密码 (管理员)", description = "管理员直接修改指定学生的登录密码")
    @Parameter(description = "学生 ID 和新密码", required = true)
    @PostMapping("/change-password")
    public Result<?> changePassword(@RequestBody ChangePasswordRequest request) {
        // 根据学生id查询学生信息
        StudentProfile studentProfile = studentProfileMapper.selectById(request.getStudentId());
        if (studentProfile == null) {
            throw new BusinessException("学生不存在");
        }

        // 获取对应的userId
        Long userId = studentProfile.getUserId();
        if (userId == null) {
            throw new BusinessException("学生未关联用户");
        }

        // 更新密码
        int result = authMapper.updatePassword(userId.intValue(), request.getNewPassword());
        if (result <= 0) {
            throw new BusinessException("修改密码失败");
        }

        return Result.success("密码修改成功");
    }

    /**
     * 将用户设置为管理员
     * @param request 包含学生 id 的请求
     * @return 操作结果
     */
    @Operation(summary = "设置管理员", description = "将指定用户设置为管理员角色")
    @Parameter(description = "学生 ID", required = true)
    @PostMapping("/set-admin")
    public Result<?> setAdmin(@RequestBody SetAdminRequest request) {
        // 根据学生id查询学生信息
        StudentProfile studentProfile = studentProfileMapper.selectById(request.getStudentId());
        if (studentProfile == null) {
            throw new BusinessException("学生不存在");
        }

        // 获取对应的userId
        Long userId = studentProfile.getUserId();
        if (userId == null) {
            throw new BusinessException("学生未关联用户");
        }

        // 更新角色为admin
        int result = authMapper.updateRole(userId.intValue(), "admin");
        if (result <= 0) {
            throw new BusinessException("设置管理员失败");
        }

        return Result.success("设置管理员成功");
    }

    // 请求参数类
    public static class ChangePasswordRequest {
        private Long studentId;
        private String newPassword;

        public Long getStudentId() {
            return studentId;
        }

        public void setStudentId(Long studentId) {
            this.studentId = studentId;
        }

        public String getNewPassword() {
            return newPassword;
        }

        public void setNewPassword(String newPassword) {
            this.newPassword = newPassword;
        }
    }

    public static class SetAdminRequest {
        private Long studentId;

        public Long getStudentId() {
            return studentId;
        }

        public void setStudentId(Long studentId) {
            this.studentId = studentId;
        }
    }

}
