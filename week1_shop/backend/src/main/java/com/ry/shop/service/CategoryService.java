package com.ry.shop.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ry.shop.entity.Category;
import com.ry.shop.mapper.CategoryMapper;
import org.springframework.stereotype.Service;
import com.ry.shop.common.BusinessException;

import java.util.List;

@Service
public class CategoryService {

    private final CategoryMapper categoryMapper;

    public CategoryService(CategoryMapper categoryMapper) {
        this.categoryMapper = categoryMapper;
    }

    public Category create(String name, Integer sort) {
        if(name == null || name.isBlank()) {
            throw new BusinessException("分类名称不能为空");
        }
        Category category = new Category();
        category.setName(name.trim());
        category.setSort(sort == null ? 0 : sort);
        categoryMapper.insert(category);
        return categoryMapper.selectById(category.getId());
    }

    public void update(Long id, String name, Integer sort) {
        Category category = categoryMapper.selectById(id);
        if(category == null) {
            throw new BusinessException("分类不存在");
        }
        if(name != null && !name.isBlank()) {
            category.setName(name.trim());
        }
        if(sort != null) {
            category.setSort(sort);
        }
        categoryMapper.updateById(category);
    }

    public void deleteById(Long id) {
        Category category = categoryMapper.selectById(id);
        if(category == null) {
            throw new BusinessException("分类不存在");
        }
        categoryMapper.deleteById(id);
    }

    public Category getById(Long id) {
        return categoryMapper.selectById(id);
    }

    public List<Category> list() {
        LambdaQueryWrapper<Category> wrapper = new LambdaQueryWrapper<>();
        // wrapper.orderByAsc("sort").orderByDesc("id");
        wrapper.orderByAsc(Category::getSort).orderByDesc(Category::getId);
        return categoryMapper.selectList(wrapper);
    }
}