package com.thirteenash.controller;

import com.thirteenash.annotation.RequiresRole;
import com.thirteenash.common.response.Result;
import com.thirteenash.dto.AddClassMembersRequestDTO;
import com.thirteenash.dto.CreateClassRequestDTO;
import com.thirteenash.dto.UpdateClassRequestDTO;
import com.thirteenash.service.IClassService;
import com.thirteenash.vo.ClassInfoVO;
import com.thirteenash.vo.UserInfoVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 班级管理控制器
 */
@RestController
@RequestMapping("/api/v1/classes")
public class ClassController {

    @Autowired
    private IClassService classService;

    /**
     * 创建班级
     */
    @PostMapping
    @RequiresRole({"ADMIN"})
    public Result<ClassInfoVO> createClass(@RequestBody CreateClassRequestDTO requestDTO) {
        ClassInfoVO classInfoVO = classService.createClass(requestDTO);
        return Result.success(classInfoVO);
    }

    /**
     * 获取班级列表
     */
    @GetMapping
    public Result<List<ClassInfoVO>> getClassList() {
        List<ClassInfoVO> classInfoVOList = classService.getClassList();
        return Result.success(classInfoVOList);
    }

    /**
     * 获取班级详情
     */
    @GetMapping("/{classId}")
    public Result<ClassInfoVO> getClassById(@PathVariable Long classId) {
        ClassInfoVO classInfoVO = classService.getClassById(classId);
        return Result.success(classInfoVO);
    }

    /**
     * 更新班级信息
     */
    @PutMapping("/{classId}")
    @RequiresRole({"ADMIN"})
    public Result<Boolean> updateClass(@PathVariable Long classId, @RequestBody UpdateClassRequestDTO requestDTO) {
        Boolean success = classService.updateClass(classId, requestDTO);
        return Result.success(success);
    }

    /**
     * 删除班级
     */
    @DeleteMapping("/{classId}")
    @RequiresRole({"ADMIN"})
    public Result<Boolean> deleteClass(@PathVariable Long classId) {
        Boolean success = classService.deleteClass(classId);
        return Result.success(success);
    }

    /**
     * 获取班级成员列表
     */
    @GetMapping("/{classId}/members")
    public Result<List<UserInfoVO>> getClassMembers(@PathVariable Long classId) {
        List<UserInfoVO> userInfoVOList = classService.getClassMembers(classId);
        return Result.success(userInfoVOList);
    }

    /**
     * 添加班级成员
     */
    @PostMapping("/{classId}/members")
    @RequiresRole({"ADMIN"})
    public Result<Boolean> addClassMembers(@PathVariable Long classId, @RequestBody AddClassMembersRequestDTO requestDTO) {
        Boolean success = classService.addClassMembers(classId, requestDTO);
        return Result.success(success);
    }

    /**
     * 移除班级成员
     */
    @DeleteMapping("/{classId}/members/{userId}")
    @RequiresRole({"ADMIN"})
    public Result<Boolean> removeClassMember(@PathVariable Long classId, @PathVariable Long userId) {
        Boolean success = classService.removeClassMember(classId, userId);
        return Result.success(success);
    }
}
