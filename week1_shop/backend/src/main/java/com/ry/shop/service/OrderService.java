package com.ry.shop.service;

import com.ry.shop.common.BusinessException;
import com.ry.shop.dto.order.OrderItemCreate;
import com.ry.shop.dto.order.OrderItemVO;
import com.ry.shop.dto.order.OrderVO;
import com.ry.shop.entity.Order;
import com.ry.shop.entity.OrderItem;
import com.ry.shop.entity.Product;
import com.ry.shop.mapper.OrderItemMapper;
import com.ry.shop.mapper.OrderMapper;
import com.ry.shop.mapper.ProductMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;

@Service
public class OrderService {
    public static final int STATUS_PENDING = 0;
    public static final int STATUS_PAID = 1;
    public static final int STATUS_CANCELLED = 2;

    private final OrderMapper orderMapper;
    private final OrderItemMapper orderItemMapper;
    private final ProductMapper productMapper;
    private final PermissionService permissionService;

    public OrderService(OrderMapper orderMapper,
                        OrderItemMapper orderItemMapper,
                        ProductMapper productMapper,
                        PermissionService permissionService) {
        this.orderMapper = orderMapper;
        this.orderItemMapper = orderItemMapper;
        this.productMapper = productMapper;
        this.permissionService = permissionService;
    }

    /**非admin用户只能查看自己的订单*/
    private void assertOrderAccess(Long userId, Order order) {
        if (permissionService.isAdmin(userId)) {
            return;
        }
        if (!order.getUserId().equals(userId)) {
            throw new BusinessException("无权操作该订单");
        }
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
            // 周二：先校验库存（真正防超卖周三再用 WHERE stock >= qty）
            if (product.getStock() == null || product.getStock() < item.getQty()) {
                throw new BusinessException("库存不足：" + product.getName());
            }
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

            // 同一事务里扣库存；后面再抛异常会整单回滚
            // product.setStock(product.getStock() - item.getQty());
            // productMapper.updateById(product);

            // updateStock的返回值是sql执行的行数, 条件匹配, 就执行sql, 返回1, 条件不匹配就返回0
            int rows = productMapper.updateStock(product.getId(), item.getQty());
            if (rows == 0) {
                throw new BusinessException("库存不足：" + product.getName());
            }
        }

        return orderMapper.selectById(order.getId());
    }

    @Transactional
    public Order pay(Long userId, Long orderId) {
        Order order = orderMapper.selectById(orderId);
        if (order == null) {
            throw new BusinessException("订单不存在");
        }
        assertOrderAccess(userId, order);

        if (order.getStatus() == null || order.getStatus() != STATUS_PENDING) {
            throw new BusinessException("只有待支付订单可以支付");
        }
        
        order.setStatus(STATUS_PAID);
        orderMapper.updateById(order);
        return orderMapper.selectById(orderId);
    }

    public Order getById(Long userId, Long id) {
        Order order = orderMapper.selectById(id);
        if (order == null) {
            throw new BusinessException("订单不存在");
        }
        assertOrderAccess(userId, order);
        return order;
    }

    /**
     * 订单列表（数据权限）
     * admin → 全部；其他人 → 只看自己的
     * 注：username 请用 JOIN 一次查出（见课程说明），这里先只查订单表
     */
    public List<OrderVO> listForUser(Long userId) {
        if (userId == null) {
            throw new BusinessException("未登录");
        }

        // admin → 传 null 不加 user_id 条件；普通人 → 传自己的 id
        Long onlyUserId = permissionService.isAdmin(userId) ? null : userId;
        List<OrderVO> orders = orderMapper.selectListWithUsername(onlyUserId);
        if (orders.isEmpty()) {
            return orders;
        }

        // 收集订单 id
        List<Long> orderIds = new ArrayList<>();
        for (OrderVO o : orders) {
            orderIds.add(o.getId());
        }

        // 一次查出所有这些订单的明细（带商品名）
        List<OrderItemVO> allItems = orderItemMapper.selectByOrderIds(orderIds);

        // 按 orderId 分组挂到对应订单上
        Map<Long, List<OrderItemVO>> itemsByOrderId = new HashMap<>();
        for (OrderItemVO item : allItems) {
            Long oid = item.getOrderId();
            if (!itemsByOrderId.containsKey(oid)) {
                itemsByOrderId.put(oid, new ArrayList<>());
            }
            itemsByOrderId.get(oid).add(item);
        }

        for (OrderVO o : orders) {
            List<OrderItemVO> items = itemsByOrderId.get(o.getId());
            o.setItems(items != null ? items : new ArrayList<>());
        }
        return orders;
    }

    /**
     * 取消订单：仅待支付可取消；同一事务里加回库存
     */
    @Transactional
    public Order cancel(Long userId, Long orderId) {
        Order order = orderMapper.selectById(orderId);
        if (order == null) {
            throw new BusinessException("订单不存在");
        }
        assertOrderAccess(userId, order);
        
        // 非法流转就拒绝
        if (order.getStatus() == null || order.getStatus() != STATUS_PENDING) {
            throw new BusinessException("只有待支付订单可以取消");
        }

        List<OrderItem> items = orderItemMapper.selectList(
            new LambdaQueryWrapper<OrderItem>().eq(OrderItem::getOrderId, orderId)
        );
        for(OrderItem item : items) {
            int rows = productMapper.restoreStock(item.getProductId(), item.getQty());
            if (rows == 0) {
                throw new BusinessException("加回库存失败，商品可能已删除：" + item.getProductId());
            }
        }

        order.setStatus(STATUS_CANCELLED);
        orderMapper.updateById(order);
        return orderMapper.selectById(orderId);
    }
}