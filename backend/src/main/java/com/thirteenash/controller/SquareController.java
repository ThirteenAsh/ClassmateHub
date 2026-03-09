package com.thirteenash.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.stp.StpUtil;
import com.thirteenash.common.response.Result;
import com.thirteenash.dto.SquareStudentDTO;
import com.thirteenash.service.SquareService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/square")
@Tag(name = "广场信息", description = "获取广场上的同学信息列表")
public class SquareController {

    @Autowired
    private SquareService squareService;

    @Operation(summary = "获取广场同学列表", description = "获取广场上所有同学的公开信息列表")
    @SaCheckLogin
    @GetMapping
    public Result<List<SquareStudentDTO>> getSquareStudents() {
        Long userId = StpUtil.getLoginIdAsLong();
        List<SquareStudentDTO> students = squareService.getSquareStudents(userId);
        return Result.success(students);
    }
}
