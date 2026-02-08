package com.thirteenash.service.impl;

import com.thirteenash.common.exception.BusinessException;
import com.thirteenash.common.util.Constants;
import com.thirteenash.common.util.PasswordUtil;
import com.thirteenash.dto.ChangePasswordRequestDTO;
import com.thirteenash.dto.LoginRequestDTO;
import com.thirteenash.dto.RegisterRequestDTO;
import com.thirteenash.dto.ResetPasswordRequestDTO;
import com.thirteenash.entity.User;
import com.thirteenash.mapper.UserMapper;
import com.thirteenash.security.jwt.JwtUtil;
import com.thirteenash.service.IUserService;
import com.thirteenash.vo.LoginResponseVO;
import com.thirteenash.vo.UserInfoVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * 用户服务实现类
 */
@Service
public class UserServiceImpl implements IUserService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private JwtUtil jwtUtil;

    @Value("${jwt.expire}")
    private Integer expire;

    @Override
    public LoginResponseVO login(LoginRequestDTO loginRequestDTO) {
        // 根据用户名查找用户
        User user = userMapper.findByUsername(loginRequestDTO.getUsername());
        if (user == null) {
            throw new BusinessException("用户不存在");
        }

        // 验证密码
        if (!PasswordUtil.matches(loginRequestDTO.getPassword(), user.getPassword())) {
            throw new BusinessException("密码错误");
        }

        // 生成JWT token
        String token = jwtUtil.generateToken(user.getId(), user.getRole());

        // 构造用户信息
        UserInfoVO userInfoVO = new UserInfoVO();
        userInfoVO.setId(user.getId());
        userInfoVO.setUsername(user.getUsername());
        userInfoVO.setEmail(user.getEmail());
        userInfoVO.setPhone(user.getPhone());
        userInfoVO.setRealName(user.getRealName());
        userInfoVO.setStudentId(user.getStudentId());
        userInfoVO.setRole(user.getRole());
        userInfoVO.setAvatar(user.getAvatar());
        userInfoVO.setStatus(user.getStatus());
        userInfoVO.setCreateTime(user.getCreateTime());
        userInfoVO.setUpdateTime(user.getUpdateTime());

        // 构造响应对象
        LoginResponseVO response = new LoginResponseVO();
        response.setToken(token);
        response.setExpiresIn(expire);
        response.setUser(userInfoVO);

        return response;
    }

    @Override
    public Boolean register(RegisterRequestDTO registerRequestDTO) {
        // 检查用户名是否已存在
        User existingUser = userMapper.findByUsername(registerRequestDTO.getUsername());
        if (existingUser != null) {
            throw new BusinessException("用户名已存在");
        }

        // 创建新用户
        User user = new User();
        user.setUsername(registerRequestDTO.getUsername());
        user.setPassword(PasswordUtil.encode(registerRequestDTO.getPassword()));
        user.setEmail(registerRequestDTO.getEmail());
        user.setPhone(registerRequestDTO.getPhone());
        user.setRealName(registerRequestDTO.getRealName());
        user.setStudentId(registerRequestDTO.getStudentId());
        user.setRole(Constants.ROLE_STUDENT); // 默认为学生角色
        user.setStatus(Constants.STATUS_ENABLED);
        user.setCreateTime(LocalDateTime.now());
        user.setUpdateTime(LocalDateTime.now());

        // 保存用户
        int result = userMapper.insert(user);
        return result > 0;
    }

    @Override
    public UserInfoVO getUserById(Long userId) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }

        UserInfoVO userInfoVO = new UserInfoVO();
        BeanUtils.copyProperties(user, userInfoVO);
        return userInfoVO;
    }

    @Override
    public User findByUsername(String username) {
        return userMapper.findByUsername(username);
    }

    @Override
    public Boolean updateUser(User user) {
        user.setUpdateTime(LocalDateTime.now());
        int result = userMapper.updateById(user);
        return result > 0;
    }

    @Override
    public Boolean changePassword(Long userId, ChangePasswordRequestDTO requestDTO) {
        // 查找用户
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }

        // 验证旧密码
        if (!PasswordUtil.matches(requestDTO.getOldPassword(), user.getPassword())) {
            throw new BusinessException("旧密码错误");
        }

        // 更新密码
        user.setPassword(PasswordUtil.encode(requestDTO.getNewPassword()));
        user.setUpdateTime(LocalDateTime.now());
        int result = userMapper.updateById(user);
        return result > 0;
    }

    @Override
    public Boolean resetPassword(Long userId, ResetPasswordRequestDTO requestDTO) {
        // 查找用户
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }

        // 更新密码
        user.setPassword(PasswordUtil.encode(requestDTO.getNewPassword()));
        user.setUpdateTime(LocalDateTime.now());
        int result = userMapper.updateById(user);
        return result > 0;
    }
}