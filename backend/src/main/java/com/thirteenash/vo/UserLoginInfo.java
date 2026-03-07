package com.thirteenash.vo;

import com.thirteenash.entity.Users;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserLoginInfo {

    private Integer id;
    private String username;
    private Users.Role role;

}
