package com.thirteenash.entity;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;

/**
 * 网站用户登录表实体类
 * 对应数据库表：users
 */
public class Users implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 用户ID，主键自增
     */
    private Integer id;

    /**
     * 用户名，唯一
     */
    private String username;

    /**
     * 密码（加密存储）
     */
    private String password;

    /**
     * 身份：admin管理员/user普通用户
     */
    private Role role;

    /**
     * 角色枚举
     */
    public enum Role {
        admin, user
    }

    // 无参构造方法
    public Users() {
    }

    // 有参构造方法
    public Users(Integer id, String realName, String username, String password, Role role) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.role = role;
    }

    // Getter和Setter方法
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    // toString方法
    @Override
    public String toString() {
        return "Users{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", role=" + role +
                '}';
    }

    // equals方法
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Users users = (Users) o;
        return Objects.equals(id, users.id) &&
                Objects.equals(username, users.username);
    }

    // hashCode方法
    @Override
    public int hashCode() {
        return Objects.hash(id, username);
    }
}