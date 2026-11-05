package com.ry.shop.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ry.shop.entity.Product;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ProductMapper extends BaseMapper<Product> {
}