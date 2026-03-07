package com.thirteenash.controller;

import cn.dev33.satoken.annotation.SaCheckRole;
import cn.dev33.satoken.annotation.SaMode;
import com.thirteenash.common.response.PageResponse;
import com.thirteenash.common.response.Result;
import com.thirteenash.dto.CreateStudentProfileRequestDTO;
import com.thirteenash.dto.PageRequestDTO;
import com.thirteenash.dto.UpdateStudentProfileRequestDTO;
import com.thirteenash.service.IStudentProfileService;
import com.thirteenash.vo.StudentProfileVO;
import com.thirteenash.vo.StudentStatisticsVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 同学信息管理控制器
 */
@RestController
@RequestMapping("/api/v1/students")
public class StudentProfileController {

    @Autowired
    private IStudentProfileService studentProfileService;

    /**
     * 创建同学信息
     */
    @SaCheckRole("admin")
    @PostMapping
    public Result<StudentProfileVO> createStudentProfile(@RequestBody CreateStudentProfileRequestDTO requestDTO) {
        StudentProfileVO studentProfileVO = studentProfileService.createStudentProfile(requestDTO);
        return Result.success(studentProfileVO);
    }

    /**
     * 获取同学列表
     */
    @SaCheckRole("admin")
    @GetMapping
    public Result<PageResponse<StudentProfileVO>> getStudentList(@RequestParam(defaultValue = "0") Integer page, @RequestParam(defaultValue = "10") Integer size) {
        PageRequestDTO pageRequestDTO = new PageRequestDTO();
        pageRequestDTO.setPage(page);
        pageRequestDTO.setSize(size);
        PageResponse<StudentProfileVO> response = studentProfileService.getStudentListByPage(pageRequestDTO);
        return Result.success(response);
    }

    /**
     * 获取同学详情
     */
    @SaCheckRole("admin")
    @GetMapping("/{id}")
    public Result<StudentProfileVO> getStudentById(@PathVariable Long id) {
        StudentProfileVO studentProfileVO = studentProfileService.getStudentById(id);
        return Result.success(studentProfileVO);
    }

    /**
     * 更新同学信息
     */
    @SaCheckRole("admin")
    @PutMapping("/{id}")
    public Result<Boolean> updateStudentProfile(@PathVariable Long id, @RequestBody UpdateStudentProfileRequestDTO requestDTO) {
        Boolean success = studentProfileService.updateStudentProfile(id, requestDTO);
        return Result.success(success);
    }

    /**
     * 删除同学信息
     */
    @SaCheckRole("admin")
    @DeleteMapping("/{id}")
    public Result<Boolean> deleteStudentProfile(@PathVariable Long id) {
        Boolean success = studentProfileService.deleteStudentProfile(id);
        return Result.success(success);
    }

    /**
     * 获取同学统计信息
     * 包括：同学总数、各班级同学数量、性别比例
     */
    @SaCheckRole(value = {"admin", "user"}, mode = SaMode.OR)
    @GetMapping("/statistics/summary")
    public Result<StudentStatisticsVO> getStudentStatistics() {
        StudentStatisticsVO statistics = studentProfileService.getStudentStatistics();
        return Result.success(statistics);
    }
}
