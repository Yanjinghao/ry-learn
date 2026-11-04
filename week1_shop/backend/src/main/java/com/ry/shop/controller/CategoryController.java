package com.ry.shop.controller;
import com.ry.shop.common.Result;
import com.ry.shop.entity.Category;
import com.ry.shop.service.CategoryService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "分类")
@RestController 
@RequestMapping("/api/category")
public class CategoryController {
    private final CategoryService categoryService;
    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @PostMapping
    public Result<Category> add(@RequestBody Map<String, Object> body) {
        Category category = categoryService.create(
            body.get("name") == null ? null : body.get("name").toString(),
            body.get("sort") == null ? null : Integer.valueOf(body.get("sort").toString())
        );
        return Result.ok(category);
    }

    @GetMapping("/{id}")
    public Result<Category> getById(@PathVariable Long id) {
        return Result.ok(categoryService.getById(id));
    }

    @GetMapping("/list")
    public Result<List<Category>> list() {
        return Result.ok(categoryService.list());
    }

    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id,
                               @RequestBody Map<String, Object> body) {
        categoryService.update(
            id,
            body.get("name") == null ? null : body.get("name").toString(),
            body.get("sort") == null ? null : Integer.valueOf(body.get("sort").toString())
        );
        return Result.ok();
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        categoryService.deleteById(id);
        return Result.ok();
    }
}