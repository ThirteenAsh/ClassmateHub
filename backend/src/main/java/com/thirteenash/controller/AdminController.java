package com.thirteenash.controller;

import com.thirteenash.annotation.RequiresRole;
import com.thirteenash.common.response.Result;
import com.thirteenash.dto.ResetPasswordRequestDTO;
import com.thirteenash.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 管理员控制器
 */
@RestController
@RequestMapping("/api/v1/admin")
@RequiresRole({"ADMIN"})
public class AdminController {

    @Autowired
    private IUserService userService;

    /**
     * 重置密码（管理员）
     */
    @PostMapping("/users/{userId}/password/reset")
    public Result<Boolean> resetPassword(@PathVariable Long userId, @RequestBody ResetPasswordRequestDTO requestDTO) {
        Boolean success = userService.resetPassword(userId, requestDTO);
        return Result.success(success);
    }
}
