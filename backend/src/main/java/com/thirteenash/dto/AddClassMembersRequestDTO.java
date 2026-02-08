package com.thirteenash.dto;

import lombok.Data;

import java.util.List;

/**
 * 添加班级成员请求DTO
 */
@Data
public class AddClassMembersRequestDTO {
    private List<Long> userIds; // 用户ID列表
}