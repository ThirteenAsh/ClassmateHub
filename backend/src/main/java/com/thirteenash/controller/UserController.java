package com.thirteenash.controller;

import cn.dev33.satoken.annotation.SaCheckRole;
import com.thirteenash.common.response.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/v1/users")
@Tag(name = "用户管理", description = "用户增删改查接口")
public class UserController {


}
