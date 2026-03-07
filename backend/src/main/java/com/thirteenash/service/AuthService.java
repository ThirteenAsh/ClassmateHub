package com.thirteenash.service;

import com.thirteenash.dto.ChangePasswordRequestDTO;
import com.thirteenash.dto.UserLoginRequestDTO;
import com.thirteenash.vo.UserLoginInfo;

public interface AuthService {

    UserLoginInfo login(UserLoginRequestDTO userLoginRequestDTO);

    void changePassword(Long userId, ChangePasswordRequestDTO requestDTO);

}
