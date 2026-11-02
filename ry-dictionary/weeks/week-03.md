# 第 3 周：订单、事务与防超卖

**目标：** 完成下单扣库存。这是转全栈的分水岭：事务、状态机、并发。  
**前置：** 商品列表、库存字段、登录鉴权已可跑。  
**项目目录：** 继续 `D:\workspace\ry\week1_shop`

---

## 周末验收

- [ ] 能下单：写订单主表 + 明细，并扣库存
- [ ] 库存不足时抛业务异常，订单和库存都不变
- [ ] 库存为 1 时并发 20 次，最终只成功 1 单
- [ ] 待支付订单可取消并加回库存；已支付不能取消
- [ ] 前端：商品详情 → 下单 → 我的订单 → 取消
- [ ] 能讲清：事务、回滚、你用的是悲观锁还是 `stock >= 1` 更新

---

## 工作日建议（每天 3h）

### 周一

- 建 `orders`、`order_item` 表
- 下单接口：校验商品上架、算金额、写主表 + 明细（先不扣库存也要能写出订单）

```sql
CREATE TABLE orders (
  id           BIGINT PRIMARY KEY AUTO_INCREMENT,
  order_no     VARCHAR(32) NOT NULL UNIQUE,
  user_id      BIGINT NOT NULL,
  status       TINYINT NOT NULL,
  pay_amount   DECIMAL(10,2) NOT NULL,
  created_at   DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  INDEX idx_user_id (user_id)
);

CREATE TABLE order_item (
  id          BIGINT PRIMARY KEY AUTO_INCREMENT,
  order_id    BIGINT NOT NULL,
  product_id  BIGINT NOT NULL,
  qty         INT NOT NULL,
  price       DECIMAL(10,2) NOT NULL,
  INDEX idx_order_id (order_id)
);
```

状态约定：`0` 待支付 / `1` 已支付 / `2` 已取消。

### 周二

- 同一 `@Transactional` 里扣库存
- 库存不足抛业务异常，验证回滚（库里不能留下「有订单无扣库存」）
- 支付先 mock：`POST /order/{id}/pay` 直接改已支付

### 周三

- 防超卖：`UPDATE stock = stock - 1 WHERE id = ? AND stock >= 1`，影响行数为 0 则失败
- 用循环、JMeter 或两个终端并发测「库存 = 1，打 20 次」
- 修到不超卖为止；把实验过程和结果记进笔记

### 周四

- 取消订单：仅待支付可取消，事务里加回库存
- 已支付 / 已取消的非法流转要拒绝
- 订单列表：当前登录用户只看自己的

### 周五

- Redis 只做一种：商品详情缓存，更新商品时删缓存
- 笔记：缓存和数据库不一致时你怎么处理（本周先「更新删缓存」）

### 周六（3～4h）

- 前端：下单、订单列表、取消、mock 支付
- 回归：库存 0 下单、重复取消、未登录下单

### 周日（2～3h）

- 口述：为什么要事务、两个请求抢最后 1 件会怎样、你怎么锁的
- 预习第 4 周：用户-角色-菜单、权限字、数据权限

---

## 本周不做

- 不上分布式事务、消息队列、秒杀架构
- 不接微信 / 支付宝
- 不要同时做三种 Redis 用法

---

## 面试可讲点

- `@Transactional` 回滚的触发条件（受检异常默认不回滚）  
- 超卖的根因：读库存与写库存之间的间隙  
- 悲观锁 / `WHERE stock >= 1` 更新 / 乐观锁，你选了哪个、为什么
