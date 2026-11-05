package com.ry.shop.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ry.shop.common.BusinessException;
import com.ry.shop.entity.Category;
import com.ry.shop.entity.Product;
import com.ry.shop.mapper.CategoryMapper;
import com.ry.shop.mapper.ProductMapper;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class ProductService {
    private final ProductMapper productMapper;
    private final CategoryMapper categoryMapper;
    public ProductService(ProductMapper productMapper, CategoryMapper categoryMapper) {
        this.productMapper = productMapper;
        this.categoryMapper = categoryMapper;
    }

    public Product create(Long categoryId, String name, BigDecimal price,
                          Integer stock, String coverUrl, Integer status) {
        assertCategoryExists(categoryId);

        Product product = new Product();
        product.setCategoryId(categoryId);
        product.setName(name.trim());
        product.setPrice(price);
        product.setStock(stock);
        product.setCoverUrl(coverUrl);
        product.setStatus(status == null ? 1 : status);
        productMapper.insert(product);
        return productMapper.selectById(product.getId());
    }

    public void update(Long id, Long categoryId, String name, BigDecimal price,
                       Integer stock, String coverUrl, Integer status) {
        Product product = productMapper.selectById(id);
        if(product == null) {
            throw new BusinessException("商品不存在");
        }
        if (categoryId != null) {
            assertCategoryExists(categoryId);
            product.setCategoryId(categoryId);
        }
        if (name != null && !name.isBlank()) {
            product.setName(name.trim());
        }
        if (price != null) {
            product.setPrice(price);
        }
        if (stock != null) {
            product.setStock(stock);
        }
        if (coverUrl != null) {
            product.setCoverUrl(coverUrl);
        }
        if (status != null) {
            if (status != 0 && status != 1) {
                throw new BusinessException("状态值不合法");
            }
            product.setStatus(status);
        }
        productMapper.updateById(product);
    }

    public void deleteById(Long id) {
        Product product = productMapper.selectById(id);
        if (product == null) {
            throw new BusinessException("商品不存在");
        }
        // 有 @TableLogic：这里实际是 UPDATE deleted=1
        productMapper.deleteById(id);
    }

    public Product getById(Long id) {
        Product product = productMapper.selectById(id);
        if (product == null) {
            throw new BusinessException("商品不存在");
        }
        return product;
    }

    public Page<Product> page(Integer pageNum, Integer pageSize, String name, Long categoryId, Integer status) {
        Page<Product> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<Product> wrapper = new LambdaQueryWrapper<>();
        if (name != null && !name.isBlank()) {
            wrapper.like(Product::getName, name);
        }
        if (categoryId != null) {
            wrapper.eq(Product::getCategoryId, categoryId);
        }
        if (status != null) {
            wrapper.eq(Product::getStatus, status);
        }
        // @TableLogic 会自动加 deleted=0，一般不用手写
        wrapper.orderByDesc(Product::getId);
        return productMapper.selectPage(page, wrapper);
    }

    private void assertCategoryExists(Long categoryId) {
        Category category = categoryMapper.selectById(categoryId);
        if(category == null) {
            throw new BusinessException("分类不存在");
        }
    }
}