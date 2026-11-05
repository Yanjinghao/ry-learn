package com.ry.shop.controller;

import com.ry.shop.common.Result;
import com.ry.shop.entity.Product;
import com.ry.shop.service.ProductService;
import org.springframework.web.bind.annotation.*;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import com.ry.shop.dto.ProductCreateRequest;
import com.ry.shop.dto.ProductUpdateRequest;
import jakarta.validation.Valid;

import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "商品")
@RestController 
@RequestMapping("/api/product")
public class ProductController {
    private final ProductService productService;
    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @PostMapping
    public Result<Product> add(@Valid @RequestBody ProductCreateRequest body) {
        Product product = productService.create(
            body.getCategoryId(),
            body.getName(),
            body.getPrice(),
            body.getStock(),
            body.getCoverUrl(),
            body.getStatus()
        );
        return Result.ok(product);
    }

    // /page 必须写在 /{id} 前面，否则 "page" 会被当成 id
    @GetMapping("/page")
    public Result<Page<Product>> page(
        @RequestParam(defaultValue = "1") Integer pageNum,
        @RequestParam(defaultValue = "10") Integer pageSize,
        @RequestParam(required = false) String name,
        @RequestParam(required = false) Long categoryId,
        @RequestParam(required = false) Integer status
    ) {
        return Result.ok(productService.page(
            pageNum,
            pageSize,
            name,
            categoryId,
            status
        ));
    }

    @GetMapping("/{id}")
    public Result<Product> getById(@PathVariable Long id) {
        return Result.ok(productService.getById(id));
    }

    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id,
                               @Valid @RequestBody ProductUpdateRequest body) {
        productService.update(
            id,
            body.getCategoryId(),
            body.getName(),
            body.getPrice(),
            body.getStock(),
            body.getCoverUrl(),
            body.getStatus()
        );
        return Result.ok();
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        productService.deleteById(id);
        return Result.ok();
    }
}