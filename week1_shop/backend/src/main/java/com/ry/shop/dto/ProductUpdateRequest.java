package com.ry.shop.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public class ProductUpdateRequest {

    private Long categoryId;

    @Size(max = 64, message = "商品名称最多 64 个字符")
    private String name;

    @DecimalMin(value = "0.00", message = "价格不能为负")
    private BigDecimal price;

    @Min(value = 0, message = "库存不能为负")
    private Integer stock;

    @Size(max = 255, message = "封面地址最多 255 个字符")
    private String coverUrl;

    private Integer status;

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public Integer getStock() {
        return stock;
    }

    public void setStock(Integer stock) {
        this.stock = stock;
    }

    public String getCoverUrl() {
        return coverUrl;
    }

    public void setCoverUrl(String coverUrl) {
        this.coverUrl = coverUrl;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }
}