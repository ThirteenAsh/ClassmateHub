package com.thirteenash.service.impl;

import cn.dev33.satoken.stp.StpUtil;
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

            return new UserLoginInfo(users.getId(), users.getUsername(), Users.Role.user);
        }

        return null;

    }

}
