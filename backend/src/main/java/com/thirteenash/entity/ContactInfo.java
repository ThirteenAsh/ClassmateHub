package com.thirteenash.entity;

import lombok.Data;

/**
 * 联系方式
 */
@Data
public class ContactInfo {
    private VisibilityField phone; // 电话
    private VisibilityField wechat; // 微信
    private VisibilityField qq; // QQ
    private VisibilityField email; // 邮箱
    private VisibilityField address; // 地址
}