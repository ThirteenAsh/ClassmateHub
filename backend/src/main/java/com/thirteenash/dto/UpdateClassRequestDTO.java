package com.thirteenash.dto;

import lombok.Data;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * 更新班级请求DTO
 */
@Data
public class UpdateClassRequestDTO {
    private String name;
    private String description;
    @JsonProperty("createTime")
    private LocalDateTime createTime;
}