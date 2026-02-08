package com.thirteenash.common.response;

import lombok.Data;

import java.util.List;

/**
 * 分页响应类
 */
@Data
public class PageResponse<T> {
    private List<T> items; // 数据列表
    private Integer page; // 当前页码
    private Integer pageSize; // 每页大小
    private Long total; // 总记录数

    public PageResponse(List<T> items, Integer page, Integer pageSize, Long total) {
        this.items = items;
        this.page = page;
        this.pageSize = pageSize;
        this.total = total;
    }
}