package com.thirteenash.common.util;

/**
 * 系统常量类
 */
public class Constants {
    /**
     * 用户会话KEY
     */
    public static final String USER_ID_KEY = "userId";
    public static final String USER_ROLE_KEY = "role";

    /**
     * JWT相关常量
     */
    public static final String JWT_TOKEN_PREFIX = "Bearer ";

    /**
     * 用户角色
     */
    public static final String ROLE_ADMIN = "ADMIN";
    public static final String ROLE_STUDENT = "STUDENT";

    /**
     * 默认密码强度
     */
    public static final int DEFAULT_PASSWORD_LENGTH = 8;

    /**
     * 系统默认状态
     */
    public static final String STATUS_ENABLED = "NORMAL";
    public static final String STATUS_DISABLED = "DISABLED";

    /**
     * 隐私级别
     */
    public static final Integer PRIVACY_PRIVATE = 0; // 仅自己可见
    public static final Integer PRIVACY_CLASSMATE = 1; // 同学可见
    public static final Integer PRIVACY_PUBLIC = 2; // 所有人可见
    
    /**
     * 可见性级别
     */
    public static final String VISIBILITY_PUBLIC = "PUBLIC"; // 所有人可见
    public static final String VISIBILITY_CLASS_ONLY = "CLASS_ONLY"; // 同班可见
    public static final String VISIBILITY_PRIVATE = "PRIVATE"; // 仅本人 & ADMIN 可见
}