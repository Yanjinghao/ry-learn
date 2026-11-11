package com.ry.shop.dto.order;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;

// 不需要引入 OrderItemCreate
// 因为 OrderCreate 和 OrderItemCreate 是同一个包，所以可以直接使用

import java.util.List;

public class OrderCreate {

    @NotEmpty(message = "订单明细不能为空")
    @Valid
    private List<OrderItemCreate> items;

    public List<OrderItemCreate> getItems() { return items; }
    public void setItems(List<OrderItemCreate> items) { this.items = items; }
}