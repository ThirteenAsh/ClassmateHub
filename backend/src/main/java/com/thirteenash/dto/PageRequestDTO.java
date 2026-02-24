package com.thirteenash.dto;

import lombok.Data;

/**
 * 分页请求DTO
 */
@Data
public class PageRequestDTO {
    private Integer page;
    private Integer size;
}
