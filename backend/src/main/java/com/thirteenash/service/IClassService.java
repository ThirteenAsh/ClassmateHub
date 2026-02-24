package com.thirteenash.service;

import com.thirteenash.common.response.PageResponse;
import com.thirteenash.dto.CreateClassRequestDTO;
import com.thirteenash.dto.PageRequestDTO;
import com.thirteenash.dto.UpdateClassRequestDTO;
import com.thirteenash.vo.ClassInfoVO;

import java.util.List;

/**
 * 班级服务接口
 */
public interface IClassService {
    /**
     * 创建班级
     */
    ClassInfoVO createClass(CreateClassRequestDTO requestDTO);

    /**
     * 获取班级列表
     */
    List<ClassInfoVO> getClassList();

    /**
     * 分页获取班级列表
     */
    PageResponse<ClassInfoVO> getClassListByPage(PageRequestDTO pageRequestDTO);

    /**
     * 获取班级详情
     */
    ClassInfoVO getClassById(Long classId);

    /**
     * 更新班级信息
     */
    Boolean updateClass(Long classId, UpdateClassRequestDTO requestDTO);

    /**
     * 删除班级
     */
    Boolean deleteClass(Long classId);
}