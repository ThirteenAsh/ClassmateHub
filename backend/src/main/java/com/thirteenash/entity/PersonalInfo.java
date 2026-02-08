package com.thirteenash.entity;

import lombok.Data;

/**
 * 个性化信息
 */
@Data
public class PersonalInfo {
    private VisibilityField hobby; // 兴趣爱好
    private VisibilityField motto; // 座右铭
    private VisibilityField memory; // 最难忘的事
    private VisibilityField company; // 公司
    private VisibilityField position; // 职位
}