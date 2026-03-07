package com.thirteenash.service.impl;

import com.thirteenash.dto.UserRegisterDTO;
import com.thirteenash.common.exception.BusinessException;
import com.thirteenash.entity.Users;
import com.thirteenash.mapper.AuthMapper;
import com.thirteenash.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final AuthMapper authMapper;
    // private final BCryptPasswordEncoder passwordEncoder;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Integer register(UserRegisterDTO registerDTO) {
        // 1. 检查用户名是否已存在
        Users existingUser = authMapper.selectByUsername(registerDTO.getUsername());
        if (existingUser != null) {
            throw new BusinessException(1001, "用户名已存在");
        }

        // 2. 创建新用户对象
        Users user = new Users();
        user.setUsername(registerDTO.getUsername());

        // 3. 密码加密存储 (这里直接存储明文密码，实际项目中请务必加密处理)
        // String encodedPassword = passwordEncoder.encode(registerDTO.getPassword());
        user.setPassword(registerDTO.getPassword());
        
        // 4. 插入数据库
        int result = authMapper.insertUser(user);
        
        if (result > 0) {
            log.info("用户注册成功：{}", user.getUsername());
            return user.getId();
        } else {
            throw new BusinessException(1002, "注册失败，请稍后重试");
        }
    }
}