package com.thirteenash.controller;

import cn.dev33.satoken.annotation.SaCheckRole;
import cn.dev33.satoken.annotation.SaMode;
import com.thirteenash.common.response.PageResponse;
import com.thirteenash.common.response.Result;
import com.thirteenash.dto.CreateStudentProfileRequestDTO;
import com.thirteenash.dto.StudentPageQueryDTO;
import com.thirteenash.dto.UpdateStudentProfileRequestDTO;
import com.thirteenash.service.IStudentProfileService;
import com.thirteenash.vo.StudentProfileVO;
import com.thirteenash.vo.StudentStatisticsVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 同学信息管理控制器
 */
@RestController
@RequestMapping("/api/v1/students")
@Tag(name = "学生管理", description = "学生信息的增删改查接口，包括创建、查询、更新和删除学生信息等")
public class StudentProfileController {

    @Autowired
    private IStudentProfileService studentProfileService;

    /**
     * 创建同学信息
     */
    @Operation(summary = "创建学生信息", description = "创建新的学生信息档案，仅管理员可操作")
    @SaCheckRole("admin")
    @PostMapping
    public Result<StudentProfileVO> createStudentProfile(@RequestBody CreateStudentProfileRequestDTO requestDTO) {
        StudentProfileVO studentProfileVO = studentProfileService.createStudentProfile(requestDTO);
        return Result.success(studentProfileVO);
    }

    /**
     * 获取同学列表
     */
    @Operation(summary = "获取学生列表", description = "分页获取学生信息，支持按姓名、性别、班级过滤，仅管理员可操作")
    @Parameter(description = "页码", example = "0")
    @Parameter(description = "每页大小", example = "10")
    @Parameter(description = "姓名（模糊搜索）", example = "张三")
    @Parameter(description = "性别", example = "男")
    @Parameter(description = "班级 ID", example = "1")
    @SaCheckRole("admin")
    @GetMapping
    public Result<PageResponse<StudentProfileVO>> getStudentList(
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String gender,
            @RequestParam(required = false) Long classId) {
        StudentPageQueryDTO queryDTO = new StudentPageQueryDTO(page, size, name, gender, classId);
        PageResponse<StudentProfileVO> response = studentProfileService.getStudentListByPage(queryDTO);
        return Result.success(response);
    }

    /**
     * 获取同学详情
     */
    @Operation(summary = "获取学生详情", description = "根据 ID 获取学生详细信息，仅管理员可操作")
    @Parameter(description = "学生 ID", example = "1")
    @SaCheckRole("admin")
    @GetMapping("/{id}")
    public Result<StudentProfileVO> getStudentById(@PathVariable Long id) {
        StudentProfileVO studentProfileVO = studentProfileService.getStudentById(id);
        return Result.success(studentProfileVO);
    }

    /**
     * 更新同学信息
     */
    @Operation(summary = "更新学生信息", description = "根据 ID 更新学生信息，仅管理员可操作")
    @Parameter(description = "学生 ID", example = "1")
    @SaCheckRole("admin")
    @PutMapping("/{id}")
    public Result<Boolean> updateStudentProfile(@PathVariable Long id, @RequestBody UpdateStudentProfileRequestDTO requestDTO) {
        Boolean success = studentProfileService.updateStudentProfile(id, requestDTO);
        return Result.success(success);
    }

    /**
     * 删除同学信息
     */
    @Operation(summary = "删除学生信息", description = "根据 ID 删除学生信息，仅管理员可操作")
    @Parameter(description = "学生 ID", example = "1")
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
    @Operation(summary = "获取学生统计信息", description = "获取学生统计数据，包括学生总数、各班级学生数量和性别比例")
    @SaCheckRole(value = {"admin", "user"}, mode = SaMode.OR)
    @GetMapping("/statistics/summary")
    public Result<StudentStatisticsVO> getStudentStatistics() {
        StudentStatisticsVO statistics = studentProfileService.getStudentStatistics();
        return Result.success(statistics);
    }
}
