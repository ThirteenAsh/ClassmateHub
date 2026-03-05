package com.thirteenash.service.impl;

import com.thirteenash.common.exception.BusinessException;
import com.thirteenash.common.response.PageResponse;
import com.thirteenash.dto.CreateClassRequestDTO;
import com.thirteenash.dto.PageRequestDTO;
import com.thirteenash.dto.UpdateClassRequestDTO;
import com.thirteenash.entity.Clazz;
import com.thirteenash.mapper.ClassMapper;
import com.thirteenash.service.IClassService;
import com.thirteenash.vo.ClassInfoVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 班级服务实现类
 */
@Service
public class ClassServiceImpl implements IClassService {

    @Autowired
    private ClassMapper classMapper;

    /**
     * 创建班级
     */
    @Override
    public ClassInfoVO createClass(CreateClassRequestDTO requestDTO) {
        Clazz clazz = new Clazz();
        clazz.setName(requestDTO.getName());
        clazz.setDescription(requestDTO.getDescription());
        clazz.setCreateTime(requestDTO.getCreateTime() != null ? requestDTO.getCreateTime() : LocalDateTime.now());
        clazz.setUpdateTime(LocalDateTime.now());

        int result = classMapper.insert(clazz);
        if (result <= 0) {
            throw new BusinessException("创建班级失败");
        }

        ClassInfoVO classInfoVO = new ClassInfoVO();
        BeanUtils.copyProperties(clazz, classInfoVO);
        return classInfoVO;
    }

    /**
     * 获取班级列表
     */
    @Override
    public PageResponse<ClassInfoVO> getClassListByPage(PageRequestDTO pageRequestDTO) {
        Integer page = pageRequestDTO.getPage();
        Integer size = pageRequestDTO.getSize();
        Integer offset = page * size;
        
        List<Clazz> clazzList = classMapper.selectByPage(offset, size);
        Long total = classMapper.selectCount();
        
        List<ClassInfoVO> classInfoVOList = new ArrayList<>();
        for (Clazz clazz : clazzList) {
            ClassInfoVO classInfoVO = new ClassInfoVO();
            BeanUtils.copyProperties(clazz, classInfoVO);
            classInfoVOList.add(classInfoVO);
        }
        
        return new PageResponse<>(classInfoVOList, page, size, total);
    }

    /**
     * 获取班级详情
     */
    @Override
    public ClassInfoVO getClassById(Long classId) {
        Clazz clazz = classMapper.selectById(classId);
        if (clazz == null) {
            throw new BusinessException("班级不存在");
        }

        ClassInfoVO classInfoVO = new ClassInfoVO();
        BeanUtils.copyProperties(clazz, classInfoVO);
        return classInfoVO;
    }

    /**
     * 更新班级信息
     */
    @Override
    public Boolean updateClass(Long classId, UpdateClassRequestDTO requestDTO) {
        Clazz clazz = classMapper.selectById(classId);
        if (clazz == null) {
            throw new BusinessException("班级不存在");
        }

        if (requestDTO.getName() != null) {
            clazz.setName(requestDTO.getName());
        }
        if (requestDTO.getDescription() != null) {
            clazz.setDescription(requestDTO.getDescription());
        }
        if (requestDTO.getCreateTime() != null) {
            clazz.setCreateTime(requestDTO.getCreateTime());
        }
        clazz.setUpdateTime(LocalDateTime.now());

        int result = classMapper.updateById(clazz);
        return result > 0;
    }

    /**
     * 删除班级
     */
    @Override
    public Boolean deleteClass(Long classId) {
        // 查找班级
        Clazz clazz = classMapper.selectById(classId);
        if (clazz == null) {
            throw new BusinessException("班级不存在");
        }

        int result = classMapper.deleteById(classId);
        return result > 0;
    }
}
