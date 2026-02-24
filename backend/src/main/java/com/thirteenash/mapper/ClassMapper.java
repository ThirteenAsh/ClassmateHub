package com.thirteenash.mapper;

import com.thirteenash.entity.Clazz;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 班级Mapper
 */
@Mapper
public interface ClassMapper {
    /**
     * 创建班级
     */
    int insert(Clazz clazz);

    /**
     * 查询所有班级
     */
    List<Clazz> selectAll();

    /**
     * 分页查询班级
     */
    List<Clazz> selectByPage(Integer offset, Integer size);

    /**
     * 查询班级总数
     */
    Long selectCount();

    /**
     * 根据ID查询班级
     */
    Clazz selectById(Long id);

    /**
     * 更新班级信息
     */
    int updateById(Clazz clazz);

    /**
     * 删除班级
     */
    int deleteById(Long id);
}