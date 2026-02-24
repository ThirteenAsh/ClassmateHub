package com.thirteenash.controller;

import com.thirteenash.common.response.PageResponse;
import com.thirteenash.common.response.Result;
import com.thirteenash.dto.CreateClassRequestDTO;
import com.thirteenash.dto.PageRequestDTO;
import com.thirteenash.dto.UpdateClassRequestDTO;
import com.thirteenash.service.IClassService;
import com.thirteenash.vo.ClassInfoVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/classes")
public class ClassController {

    @Autowired
    private IClassService classService;

    @PostMapping
    public Result<ClassInfoVO> createClass(@RequestBody CreateClassRequestDTO requestDTO) {
        ClassInfoVO classInfoVO = classService.createClass(requestDTO);
        return Result.success(classInfoVO);
    }

    @GetMapping
    public Result<PageResponse<ClassInfoVO>> getClassList(@RequestParam(defaultValue = "0") Integer page, 
                                                          @RequestParam(defaultValue = "10") Integer size) {
        PageRequestDTO pageRequestDTO = new PageRequestDTO();
        pageRequestDTO.setPage(page);
        pageRequestDTO.setSize(size);
        PageResponse<ClassInfoVO> pageResponse = classService.getClassListByPage(pageRequestDTO);
        return Result.success(pageResponse);
    }

    @GetMapping("/{classId}")
    public Result<ClassInfoVO> getClassById(@PathVariable Long classId) {
        ClassInfoVO classInfoVO = classService.getClassById(classId);
        return Result.success(classInfoVO);
    }

    @PutMapping("/{classId}")
    public Result<Boolean> updateClass(@PathVariable Long classId, @RequestBody UpdateClassRequestDTO requestDTO) {
        Boolean success = classService.updateClass(classId, requestDTO);
        return Result.success(success);
    }

    @DeleteMapping("/{classId}")
    public Result<Boolean> deleteClass(@PathVariable Long classId) {
        Boolean success = classService.deleteClass(classId);
        return Result.success(success);
    }

}
