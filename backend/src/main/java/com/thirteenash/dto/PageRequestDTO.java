package com.thirteenash.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 分页请求DTO
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PageRequestDTO {
    private Integer page;
    private Integer size;
}
