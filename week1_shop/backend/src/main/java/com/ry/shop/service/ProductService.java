package com.ry.shop.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ry.shop.common.BusinessException;
import com.ry.shop.entity.Category;
import com.ry.shop.entity.Product;
import com.ry.shop.mapper.CategoryMapper;
import com.ry.shop.mapper.ProductMapper;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.data.redis.core.StringRedisTemplate;

import com.ry.shop.dto.product.ProductDetailVO;

import java.math.BigDecimal;
import java.time.Duration;

@Service
public class ProductService {

    private static final String DETAIL_KEY_PREFIX = "product:detail:";

    private final ProductMapper productMapper;
    private final CategoryMapper categoryMapper;
    private final StringRedisTemplate stringRedisTemplate;
    private final ObjectMapper objectMapper;

    public ProductService(
        ProductMapper productMapper,
        CategoryMapper categoryMapper,
        StringRedisTemplate stringRedisTemplate,
        ObjectMapper objectMapper
    ) {
        this.productMapper = productMapper;
        this.categoryMapper = categoryMapper;
        this.stringRedisTemplate = stringRedisTemplate;
        this.objectMapper = objectMapper;
    }

    private void assertCategoryExists(Long categoryId) {
        Category category = categoryMapper.selectById(categoryId);
        if(category == null) {
            throw new BusinessException("分类不存在");
        }
    }

    private void assertPriceAndStock(BigDecimal price, Integer stock) {
        if(price == null || price.compareTo(BigDecimal.ZERO) < 0) {
            throw new BusinessException("价格不能为空且必须大于0");
        }
        if(stock == null || stock < 0) {
            throw new BusinessException("库存不能为空且必须大于0");
        }
    }

    public Product create(Long categoryId, String name, BigDecimal price,
                          Integer stock, String coverUrl, Integer status) {
        assertCategoryExists(categoryId);
        assertPriceAndStock(price, stock);

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
            assertPriceAndStock(price, stock);
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
        stringRedisTemplate.delete(DETAIL_KEY_PREFIX + id);
    }

    public void deleteById(Long id) {
        Product product = productMapper.selectById(id);
        if (product == null) {
            throw new BusinessException("商品不存在");
        }
        // 有 @TableLogic：这里实际是 UPDATE deleted=1
        productMapper.deleteById(id);
        stringRedisTemplate.delete(DETAIL_KEY_PREFIX + id);
    }

    // public Product getById(Long id) {
    //     Product product = productMapper.selectById(id);
    //     if (product == null) {
    //         throw new BusinessException("商品不存在");
    //     }
    //     return product;
    // }

    public ProductDetailVO getDetailById(Long id) {
        String key = DETAIL_KEY_PREFIX + id;
        String cached = stringRedisTemplate.opsForValue().get(key);
        if (cached != null) {
            try {
                return objectMapper.readValue(cached, ProductDetailVO.class);
            } catch (JsonProcessingException e) {
                // 缓存坏了就当没有，删掉再查库
                stringRedisTemplate.delete(key);
            }
        }

        Product product = productMapper.selectById(id);
        if(product == null) {
            throw new BusinessException("商品不存在");
        }

        ProductDetailVO vo = new ProductDetailVO();
        vo.setId(product.getId());
        vo.setCategoryId(product.getCategoryId());
        vo.setName(product.getName());
        vo.setPrice(product.getPrice());
        vo.setStock(product.getStock());
        vo.setCoverUrl(product.getCoverUrl());
        vo.setStatus(product.getStatus());
        vo.setCreatedAt(product.getCreatedAt());

        Category category = categoryMapper.selectById(product.getCategoryId());
        if (category != null) {
            vo.setCategoryName(category.getName());
        }
        
        try {
            stringRedisTemplate.opsForValue().set(
                    key,
                    objectMapper.writeValueAsString(vo),
                    Duration.ofMinutes(30)
            );
        } catch (JsonProcessingException e) {
            // 写缓存失败不影响主流程
        }

        return vo;
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

    public void updateStatus(Long id, Integer status) {
        if(status == null || (status != 0 && status != 1)) {
            throw new BusinessException("状态值不合法");
        }
        Product product = productMapper.selectById(id);
        if(product == null) {
            throw new BusinessException("商品不存在");
        }
        product.setStatus(status);
        productMapper.updateById(product);
        stringRedisTemplate.delete(DETAIL_KEY_PREFIX + id);
    }
}