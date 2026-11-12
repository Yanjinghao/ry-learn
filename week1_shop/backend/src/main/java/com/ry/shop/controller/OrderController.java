package com.ry.shop.controller;

import com.ry.shop.common.Result;
import com.ry.shop.dto.order.OrderCreate;
import com.ry.shop.entity.Order;
import com.ry.shop.service.OrderService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@Tag(name = "订单")
@RestController
@RequestMapping("/api/order")
public class OrderController {
    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    public Result<Order> create(@RequestBody @Valid OrderCreate body, HttpServletRequest request) {
        // 在jwt拦截器里setAttribute userId 到 request 里, 现在取出来
        Long userId = (Long) request.getAttribute("userId");
        Order order = orderService.create(userId, body.getItems());
        return Result.ok(order);
    }

    @GetMapping 
    public Result<List<Order>> listMine(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        return Result.ok(orderService.listMine(userId));
    }

    @GetMapping("/{id}")
    public Result<Order> getById(@PathVariable Long id) {
        Order order = orderService.getById(id);
        return Result.ok(order);
    }

    @PostMapping("/{id}/pay")
    public Result<Order> pay(@PathVariable Long id, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        Order order = orderService.pay(userId, id);
        return Result.ok(order);
    }

    @PostMapping("/{id}/cancel")
    public Result<Order> cancel(@PathVariable Long id, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        return Result.ok(orderService.cancel(userId, id));
    }
}