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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
public class ClassController {

    @Autowired
    private IClassService classService;

    //创建班级
    @SaCheckRole("admin")
    @PostMapping("/admin/classes")
    public Result<ClassInfoVO> createClass(@RequestBody CreateClassRequestDTO requestDTO) {
        ClassInfoVO classInfoVO = classService.createClass(requestDTO);
        return Result.success(classInfoVO);
    }

    //获取班级列表
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
    @SaCheckRole("admin")
    @GetMapping("/admin/classes/{classId}")
    public Result<ClassInfoVO> getClassById(@PathVariable Long classId) {
        ClassInfoVO classInfoVO = classService.getClassById(classId);
        return Result.success(classInfoVO);
    }

    //更新班级信息
    @SaCheckRole("admin")
    @PutMapping("/admin/classes/{classId}")
    public Result<Boolean> updateClass(@PathVariable Long classId, @RequestBody UpdateClassRequestDTO requestDTO) {
        Boolean success = classService.updateClass(classId, requestDTO);
        return Result.success(success);
    }

    //删除班级
    @SaCheckRole("admin")
    @DeleteMapping("/admin/classes/{classId}")
    public Result<Boolean> deleteClass(@PathVariable Long classId) {
        Boolean success = classService.deleteClass(classId);
        return Result.success(success);
    }

    //获取班级列表（用户和管理员都可访问）
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
    @SaCheckRole(value = {"admin", "user"}, mode = SaMode.OR)
    @GetMapping("/classes/detail/{classId}")
    public Result<ClassInfoVO> getClassDetailForUser(@PathVariable Long classId) {
        ClassInfoVO classInfoVO = classService.getClassById(classId);
        return Result.success(classInfoVO);
    }
}
