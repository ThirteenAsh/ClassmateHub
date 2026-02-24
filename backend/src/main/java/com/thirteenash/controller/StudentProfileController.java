package com.thirteenash.controller;

import com.thirteenash.common.response.PageResponse;
import com.thirteenash.common.response.Result;
import com.thirteenash.dto.CreateStudentProfileRequestDTO;
import com.thirteenash.dto.PageRequestDTO;
import com.thirteenash.dto.UpdateStudentProfileRequestDTO;
import com.thirteenash.service.IStudentProfileService;
import com.thirteenash.vo.StudentProfileVO;
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
    @PostMapping
    public Result<StudentProfileVO> createStudentProfile(@RequestBody CreateStudentProfileRequestDTO requestDTO) {
        StudentProfileVO studentProfileVO = studentProfileService.createStudentProfile(requestDTO);
        return Result.success(studentProfileVO);
    }

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
    @GetMapping("/{id}")
    public Result<StudentProfileVO> getStudentById(@PathVariable Long id) {
        StudentProfileVO studentProfileVO = studentProfileService.getStudentById(id);
        return Result.success(studentProfileVO);
    }

    /**
     * 更新同学信息
     */
    @PutMapping("/{id}")
    public Result<Boolean> updateStudentProfile(@PathVariable Long id, @RequestBody UpdateStudentProfileRequestDTO requestDTO) {
        Boolean success = studentProfileService.updateStudentProfile(id, requestDTO);
        return Result.success(success);
    }

    /**
     * 删除同学信息
     */
    @DeleteMapping("/{id}")
    public Result<Boolean> deleteStudentProfile(@PathVariable Long id) {
        Boolean success = studentProfileService.deleteStudentProfile(id);
        return Result.success(success);
    }
}
