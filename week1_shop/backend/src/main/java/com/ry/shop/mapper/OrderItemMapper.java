package com.ry.shop.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ry.shop.dto.order.OrderItemVO;
import com.ry.shop.entity.OrderItem;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface OrderItemMapper extends BaseMapper<OrderItem> {

    @Select("""
            <script>
            SELECT
              oi.order_id   AS orderId,
              oi.product_id AS productId,
              p.name        AS productName,
              oi.qty        AS qty,
              oi.price      AS price
            FROM order_item oi
            LEFT JOIN product p ON p.id = oi.product_id
            WHERE oi.order_id IN
            <foreach collection="orderIds" item="id" open="(" separator="," close=")">
              #{id}
            </foreach>
            ORDER BY oi.id ASC
            </script>
            """)
    List<OrderItemVO> selectByOrderIds(@Param("orderIds") List<Long> orderIds);
}