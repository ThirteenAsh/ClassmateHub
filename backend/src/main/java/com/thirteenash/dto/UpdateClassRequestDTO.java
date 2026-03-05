package com.thirteenash.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.NoArgsConstructor;

/**
 * 更新班级请求DTO
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateClassRequestDTO {
    private String name;
    private String description;
    @JsonProperty("createTime")
    private LocalDateTime createTime;
}