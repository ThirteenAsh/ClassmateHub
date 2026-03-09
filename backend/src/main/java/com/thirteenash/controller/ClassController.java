package com.thirteenash.controller;

import cn.dev33.satoken.annotation.SaCheckRole;
import cn.dev33.satoken.annotation.SaMode;
import com.thirteenash.common.response.PageResponse;
import com.thirteenash.common.response.Result;
import com.thirteenash.dto.CreateClassRequestDTO;
import com.thirteenash.dto.PageRequestDTO;
import com.thirteenash.dto.UpdateClassRequestDTO;
import com.thirteenash.service.IClassService;
import com.thirteenash.vo.ClassInfoVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
@Tag(name = "班级管理", description = "班级信息的增删改查接口，包括创建班级、获取班级列表、更新和删除班级等")
public class ClassController {

    @Autowired
    private IClassService classService;

    //创建班级
    @Operation(summary = "创建班级", description = "创建新的班级信息，仅管理员可操作")
    @SaCheckRole("admin")
    @PostMapping("/admin/classes")
    public Result<ClassInfoVO> createClass(@RequestBody CreateClassRequestDTO requestDTO) {
        ClassInfoVO classInfoVO = classService.createClass(requestDTO);
        return Result.success(classInfoVO);
    }

    //获取班级列表
    @Operation(summary = "获取班级列表 (管理员)", description = "分页获取所有班级信息，仅管理员可操作")
    @Parameter(description = "页码", example = "0")
    @Parameter(description = "每页大小", example = "10")
    @SaCheckRole("admin")
    @GetMapping("/admin/classes")
    public Result<PageResponse<ClassInfoVO>> getClassList(@RequestParam(defaultValue = "0") Integer page, 
                                                          @RequestParam(defaultValue = "10") Integer size) {
        PageRequestDTO pageRequestDTO = new PageRequestDTO();
        pageRequestDTO.setPage(page);
        pageRequestDTO.setSize(size);
        PageResponse<ClassInfoVO> pageResponse = classService.getClassListByPage(pageRequestDTO);
        return Result.success(pageResponse);
    }

    //获取班级详情
    @Operation(summary = "获取班级详情 (管理员)", description = "根据 ID 获取班级详细信息，仅管理员可操作")
    @Parameter(description = "班级 ID", example = "1")
    @SaCheckRole("admin")
    @GetMapping("/admin/classes/{classId}")
    public Result<ClassInfoVO> getClassById(@PathVariable Long classId) {
        ClassInfoVO classInfoVO = classService.getClassById(classId);
        return Result.success(classInfoVO);
    }

    //更新班级信息
    @Operation(summary = "更新班级信息", description = "根据 ID 更新班级信息，仅管理员可操作")
    @Parameter(description = "班级 ID", example = "1")
    @SaCheckRole("admin")
    @PutMapping("/admin/classes/{classId}")
    public Result<Boolean> updateClass(@PathVariable Long classId, @RequestBody UpdateClassRequestDTO requestDTO) {
        Boolean success = classService.updateClass(classId, requestDTO);
        return Result.success(success);
    }

    //删除班级
    @Operation(summary = "删除班级", description = "根据 ID 删除班级信息，仅管理员可操作")
    @Parameter(description = "班级 ID", example = "1")
    @SaCheckRole("admin")
    @DeleteMapping("/admin/classes/{classId}")
    public Result<Boolean> deleteClass(@PathVariable Long classId) {
        Boolean success = classService.deleteClass(classId);
        return Result.success(success);
    }

    //获取班级列表（用户和管理员都可访问）
    @Operation(summary = "获取班级列表 (用户)", description = "分页获取所有班级信息，管理员和普通用户都可访问")
    @Parameter(description = "页码", example = "0")
    @Parameter(description = "每页大小", example = "10")
    @SaCheckRole(value = {"admin", "user"}, mode = SaMode.OR)
    @GetMapping("/classes/list")
    public Result<PageResponse<ClassInfoVO>> getClassListForUser(@RequestParam(defaultValue = "0") Integer page,
                                                                   @RequestParam(defaultValue = "10") Integer size) {
        PageRequestDTO pageRequestDTO = new PageRequestDTO();
        pageRequestDTO.setPage(page);
        pageRequestDTO.setSize(size);
        PageResponse<ClassInfoVO> pageResponse = classService.getClassListByPage(pageRequestDTO);
        return Result.success(pageResponse);
    }

    //获取班级详情（用户和管理员都可访问）
    @Operation(summary = "获取班级详情 (用户)", description = "根据 ID 获取班级详细信息，管理员和普通用户都可访问")
    @Parameter(description = "班级 ID", example = "1")
    @SaCheckRole(value = {"admin", "user"}, mode = SaMode.OR)
    @GetMapping("/classes/detail/{classId}")
    public Result<ClassInfoVO> getClassDetailForUser(@PathVariable Long classId) {
        ClassInfoVO classInfoVO = classService.getClassById(classId);
        return Result.success(classInfoVO);
    }
}
