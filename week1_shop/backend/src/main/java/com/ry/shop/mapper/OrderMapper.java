package com.ry.shop.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ry.shop.dto.order.OrderVO;
import com.ry.shop.entity.Order;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface OrderMapper extends BaseMapper<Order> {
    /**
     * 订单 + 用户名
     * onlyUserId == null → 查全部（admin）
     * onlyUserId != null → 只查该用户（shop）
     */
    @Select("""
            <script>
                SELECT
                o.id,
                o.order_no   AS orderNo,
                o.user_id    AS userId,
                u.username   AS username,
                o.status,
                o.pay_amount AS payAmount,
                o.created_at AS createdAt
                FROM orders o
                LEFT JOIN `user` u ON u.id = o.user_id
                <where>
                <if test="onlyUserId != null">
                    AND o.user_id = #{onlyUserId}
                </if>
                </where>
                ORDER BY o.id DESC
            </script>
            """)
    List<OrderVO> selectListWithUsername(@Param("onlyUserId") Long onlyUserId);
}