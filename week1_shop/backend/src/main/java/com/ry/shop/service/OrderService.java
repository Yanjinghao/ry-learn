package com.ry.shop.service;

import com.ry.shop.common.BusinessException;
import com.ry.shop.dto.order.OrderItemCreate;
import com.ry.shop.entity.Order;
import com.ry.shop.entity.OrderItem;
import com.ry.shop.entity.Product;
import com.ry.shop.mapper.OrderItemMapper;
import com.ry.shop.mapper.OrderMapper;
import com.ry.shop.mapper.ProductMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@Service
public class OrderService {
    public static final int STATUS_PENDING = 0;

    private final OrderMapper orderMapper;
    private final OrderItemMapper orderItemMapper;
    private final ProductMapper productMapper;

    public OrderService(OrderMapper orderMapper, OrderItemMapper orderItemMapper, ProductMapper productMapper) {
        this.orderMapper = orderMapper;
        this.orderItemMapper = orderItemMapper;
        this.productMapper = productMapper;
    }

    // 生成订单号
    private String generateOrderNo() {
        String time = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        int rand = ThreadLocalRandom.current().nextInt(1000, 10000);
        return "O" + time + rand;
    }

    @Transactional  // 事务注解：要么都成功，要么都失败
    public Order create(Long userId, List<OrderItemCreate> items) {
        if (userId == null) {
            throw new BusinessException("未登录");
        }
        if (items == null || items.isEmpty()) {
            throw new BusinessException("订单明细不能为空");
        }

        BigDecimal payAmount = BigDecimal.ZERO;
        // 先校验并算金额（价格以库里为准，不信前端传价）
        for (OrderItemCreate item : items) {
            Product product = productMapper.selectById(item.getProductId());
            if (product == null) {
                throw new BusinessException("商品不存在：" + item.getProductId());
            }
            if (product.getStatus() == null || product.getStatus() != 1) {
                throw new BusinessException("商品未上架：" + product.getName());
            }
            // 周一可不校验库存；周二再扣库存时再严查
            BigDecimal line = product.getPrice().multiply(BigDecimal.valueOf(item.getQty()));
            payAmount = payAmount.add(line);
        }

        Order order = new Order();
        order.setOrderNo(generateOrderNo());
        order.setUserId(userId);
        order.setStatus(STATUS_PENDING);
        order.setPayAmount(payAmount);
        orderMapper.insert(order);

        for(OrderItemCreate item : items) {
            Product product = productMapper.selectById(item.getProductId());
            OrderItem row = new OrderItem();
            row.setOrderId(order.getId());
            row.setProductId(product.getId());
            row.setQty(item.getQty());
            row.setPrice(product.getPrice()); // 下单时价格快照
            orderItemMapper.insert(row);
        }

        return orderMapper.selectById(order.getId());
    }

    public Order getById(Long id) {
        Order order = orderMapper.selectById(id);
        if (order == null) {
            throw new BusinessException("订单不存在");
        }
        return order;
    }
}