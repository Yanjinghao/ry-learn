package com.ry.shop.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ry.shop.entity.Category;
import com.ry.shop.entity.Product;
import com.ry.shop.mapper.CategoryMapper;
import com.ry.shop.mapper.ProductMapper;
import org.springframework.stereotype.Service;
import com.ry.shop.common.BusinessException;

import java.util.List;

@Service
public class CategoryService {

    private final CategoryMapper categoryMapper;
    private final ProductMapper productMapper;

    public CategoryService(CategoryMapper categoryMapper, ProductMapper productMapper) {
        this.categoryMapper = categoryMapper;
        this.productMapper = productMapper;
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

        LambdaQueryWrapper<Product> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Product::getCategoryId, id);
        Long productCount = productMapper.selectCount(wrapper);
        if(productCount > 0) {
            throw new BusinessException("分类下有商品，不能删除");
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