package com.thirteenash.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.NoArgsConstructor;

/**
 * 创建班级请求DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateClassRequestDTO {
    private String name;
    private String description;
    @JsonProperty("createTime")
    private LocalDateTime createTime;
}