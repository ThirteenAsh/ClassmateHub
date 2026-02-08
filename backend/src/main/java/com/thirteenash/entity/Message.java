package com.thirteenash.entity;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 留言实体类
 */
@Data
public class Message {
    private Long id;
    
    private Long senderId; // 发送者ID
    
    private Long receiverId; // 接收者ID
    
    private String content; // 留言内容
    
    private Integer status; // 状态：0-未读，1-已读
    
    private LocalDateTime createTime;
    
    private LocalDateTime updateTime;
}