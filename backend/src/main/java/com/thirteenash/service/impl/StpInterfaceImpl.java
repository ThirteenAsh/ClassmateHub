package com.thirteenash.service.impl;

import cn.dev33.satoken.stp.StpInterface;
import com.thirteenash.mapper.AuthMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * 自定义权限加载接口实现类
 */
@Slf4j
@Component    // 保证此类被 SpringBoot 扫描，完成 Sa-Token 的自定义权限验证扩展
public class StpInterfaceImpl implements StpInterface {

    @Autowired
    private AuthMapper authMapper;

    /**
     * 返回一个账号所拥有的权限码集合
     */
    @Override
    public List<String> getPermissionList(Object loginId, String loginType) {

        // 根据 loginId 查询用户的权限
        String userRole = authMapper.selectRoleByUserId((Integer) loginId);

        log.info("查询到id为：{} 的用户权限为：{}",(Integer) loginId, userRole);

        if(Objects.equals(userRole, "admin")) {
            List<String> list = new ArrayList<String>();
            list.add("101");
            list.add("user.add");
            list.add("user.update");
            list.add("user.get");
            list.add("user.delete");
            return list;
        }else if(Objects.equals(userRole, "user")) {
            List<String> list = new ArrayList<String>();
            list.add("101");
            list.add("user.get");
            list.add("art.*");
            return list;
        }else{
            return new ArrayList<>();
        }
    }

    /**
     * 返回一个账号所拥有的角色标识集合 (权限与角色可分开校验)
     */
    @Override
    public List<String> getRoleList(Object loginId, String loginType) {

        // 根据 loginId 查询用户的权限
        String userRole = authMapper.selectRoleByUserId(Integer.parseInt((String) loginId));
        log.info("查询到id为：{} 的用户权限为：{}", Integer.parseInt((String) loginId), userRole);

        // 本 list 仅做模拟，实际项目中要根据具体业务逻辑来查询角色
        if ("admin".equals(userRole)) {
            List<String> list = new ArrayList<String>();
            list.add("admin");
            return list;
        } else if ("user".equals(userRole)) {
            List<String> list = new ArrayList<String>();
            list.add("user");
            return list;
        }else{
            return new ArrayList<>();
        }
    }

}
