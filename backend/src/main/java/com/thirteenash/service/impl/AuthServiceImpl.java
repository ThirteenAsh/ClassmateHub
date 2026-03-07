package com.thirteenash.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import com.thirteenash.common.exception.BusinessException;
import com.thirteenash.dto.ChangePasswordRequestDTO;
import com.thirteenash.dto.UserLoginRequestDTO;
import com.thirteenash.entity.Users;
import com.thirteenash.mapper.AuthMapper;
import com.thirteenash.vo.UserLoginInfo;
import com.thirteenash.service.AuthService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class AuthServiceImpl implements AuthService {

    @Autowired
    private AuthMapper authMapper;

    @Override
    public UserLoginInfo login(UserLoginRequestDTO userLoginRequestDTO) {

        Users users = authMapper.selectByUsernameAndPassword(userLoginRequestDTO);

        if(users != null) {
            log.info("登录成功，用户信息：{}", users);

            StpUtil.login(users.getId());

            return new UserLoginInfo(users.getId(), users.getUsername(), users.getRole());
        }

        return null;

    }

    @Override
    public void changePassword(Long userId, ChangePasswordRequestDTO requestDTO) {
        Users user = authMapper.selectById(userId.intValue());
        if (user == null) {
            throw new BusinessException("用户不存在");
        }

        if (!user.getPassword().equals(requestDTO.getOldPassword())) {
            throw new BusinessException("原密码错误");
        }

        if (requestDTO.getOldPassword().equals(requestDTO.getNewPassword())) {
            throw new BusinessException("新密码不能与原密码相同");
        }

        int result = authMapper.updatePassword(userId.intValue(), requestDTO.getNewPassword());
        if (result <= 0) {
            throw new BusinessException("修改密码失败");
        }
    }

}
