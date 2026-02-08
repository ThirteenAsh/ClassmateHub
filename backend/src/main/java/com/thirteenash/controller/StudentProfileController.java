package com.thirteenash.controller;

import com.thirteenash.common.response.Result;
import com.thirteenash.entity.StudentProfile;
import com.thirteenash.service.IStudentProfileService;
import com.thirteenash.vo.StudentProfileVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 同学信息管理控制器
 */
@RestController
@RequestMapping("/api/v1/students")
public class StudentProfileController {

    @Autowired
    private IStudentProfileService studentProfileService;

    /**
     * 获取同学列表
     */
    @GetMapping
    public Result<List<StudentProfileVO>> getStudentList() {
        List<StudentProfileVO> studentProfileVOs = studentProfileService.getStudentList();
        return Result.success(studentProfileVOs);
    }

    /**
     * 获取同学详情
     */
    @GetMapping("/{userId}")
    public Result<StudentProfileVO> getStudentById(@PathVariable Long userId) {
        StudentProfileVO studentProfileVO = studentProfileService.getStudentById(userId);
        return Result.success(studentProfileVO);
    }

    /**
     * 更新同学信息
     */
    @PutMapping("/{userId}")
    public Result<Boolean> updateStudentProfile(@PathVariable Long userId, @RequestBody StudentProfile studentProfile) {
        Boolean success = studentProfileService.updateStudentProfile(userId, studentProfile);
        return Result.success(success);
    }

    /**
     * 删除同学信息
     */
    @DeleteMapping("/{userId}")
    public Result<Boolean> deleteStudentProfile(@PathVariable Long userId) {
        Boolean success = studentProfileService.deleteStudentProfile(userId);
        return Result.success(success);
    }
}
