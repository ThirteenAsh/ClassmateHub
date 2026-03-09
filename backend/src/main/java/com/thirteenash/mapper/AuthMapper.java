package com.thirteenash.mapper;

import com.thirteenash.dto.UserLoginRequestDTO;
import com.thirteenash.entity.Users;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface AuthMapper {

    /**
     * 根据用户名和密码查询用户
     * @param userLoginRequestDTO 包含用户名和密码的DTO对象
     * @return 用户对象，如果不存在返回null
     */
    Users selectByUsernameAndPassword(UserLoginRequestDTO userLoginRequestDTO);

    /**
     * 根据用户名查询用户
     * @param username 用户名
     * @return 用户对象，如果不存在返回null
     */
    //Users selectByUsername(@Param("username") String username);

    /**
     * 使用Map参数查询
     */
    //Users selectByUsernameAndPasswordMap(Map<String, Object> params);

    /**
     * 根据用户ID查询用户角色
     * @param id
     * @return
     */
    String selectRoleByUserId(@Param("id") Integer id);

    /**
     * 根据用户名查询用户
     */
    Users selectByUsername(@Param("username") String username);

    /**
     * 插入新用户
     */
    int insertUser(Users user);

    /**
     * 根据用户ID查询用户
     */
    Users selectById(@Param("id") Integer id);

    /**
     * 更新用户密码
     */
    int updatePassword(@Param("id") Integer id, @Param("password") String password);

    /**
     * 更新用户角色
     */
    int updateRole(@Param("id") Integer id, @Param("role") String role);

    /**
     * 删除用户
     */
    int deleteUser(@Param("id") Integer id);
}
