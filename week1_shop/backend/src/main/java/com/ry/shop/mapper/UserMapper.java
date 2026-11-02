package com.ry.shop.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ry.shop.entity.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper extends BaseMapper<User> {
    // 先空着，insert/select 等方法 BaseMapper 已经有了
}