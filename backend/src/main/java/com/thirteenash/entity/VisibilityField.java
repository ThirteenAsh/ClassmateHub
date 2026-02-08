package com.thirteenash.entity;

import lombok.Data;

/**
 * 带可见性的字段
 */
@Data
public class VisibilityField {
    private String value; // 字段值
    private String visibility; // 可见性：PUBLIC, CLASS_ONLY, PRIVATE
}