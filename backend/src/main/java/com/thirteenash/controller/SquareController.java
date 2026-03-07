package com.thirteenash.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.stp.StpUtil;
import com.thirteenash.common.response.Result;
import com.thirteenash.dto.SquareStudentDTO;
import com.thirteenash.service.SquareService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/square")
public class SquareController {

    @Autowired
    private SquareService squareService;

    @SaCheckLogin
    @GetMapping
    public Result<List<SquareStudentDTO>> getSquareStudents() {
        Long userId = StpUtil.getLoginIdAsLong();
        List<SquareStudentDTO> students = squareService.getSquareStudents(userId);
        return Result.success(students);
    }
}
