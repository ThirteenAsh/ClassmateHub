package com.thirteenash.dto;

import lombok.Data;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * 创建班级请求DTO
 */
@Data
public class CreateClassRequestDTO {
    private String name;
    private String description;
    @JsonProperty("createTime")
    private LocalDateTime createTime;
}