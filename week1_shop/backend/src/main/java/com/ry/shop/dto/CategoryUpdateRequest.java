package com.ry.shop.dto;

import jakarta.validation.constraints.Size;

public class CategoryUpdateRequest {

    @Size(max = 64, message = "分类名称最多 64 个字符")
    private String name;

    private Integer sort;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getSort() {
        return sort;
    }

    public void setSort(Integer sort) {
        this.sort = sort;
    }
}