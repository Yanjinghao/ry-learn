# 第 2 周：多表、分页、文件上传与部署

**目标：** 在登录基础上做商品管理：分类、商品、分页筛选、封面上传，并至少部署一次。  
**前置：** 第 1 周 JWT 登录与用户列表已可跑。  
**项目目录：** 继续 `D:\workspace\ry\week1_shop`

---

## 周末验收

- [ ] 分类 CRUD、商品新增/编辑/删除都通
- [ ] 商品列表分页正确，可按名称、分类、上架状态筛选
- [ ] 上传封面后刷新页面图片还在
- [ ] 浏览器能打开前端，登录后能管商品（不是只在 IDEA 里点）
- [ ] 能解释：分页 `LIMIT offset, size` 页码大了为什么会慢

---

## 工作日建议（每天 3h）

### 周一

- 建 `category`、`product` 表，自己写 SQL
- `product.category_id` 加索引
- 完成分类 CRUD

```sql
CREATE TABLE category (
  id     BIGINT PRIMARY KEY AUTO_INCREMENT,
  name   VARCHAR(32) NOT NULL,
  sort   INT NOT NULL DEFAULT 0
);

CREATE TABLE product (
  id           BIGINT PRIMARY KEY AUTO_INCREMENT,
  category_id  BIGINT NOT NULL,
  name         VARCHAR(64) NOT NULL,
  price        DECIMAL(10,2) NOT NULL,
  stock        INT NOT NULL DEFAULT 0,
  cover_url    VARCHAR(255),
  status       TINYINT NOT NULL DEFAULT 1,
  deleted      TINYINT NOT NULL DEFAULT 0,
  created_at   DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  INDEX idx_category_id (category_id),
  INDEX idx_status (status)
);
```

### 周二

- 商品新增 / 编辑 / 删除（逻辑删除）
- 商品分页 + 多条件查询（名称模糊、分类、上架状态）
- 列表不要查出已删数据

### 周三

- 文件上传：先存本地 `uploads/`，返回可访问 URL
- 静态资源映射，前端能显示封面
- 商品详情带回分类名（join 或两次查询，先跑通再优化）

### 周四

- 上架 / 下架接口
- 价格、库存校验（不能为负）
- 补几个失败用例：无 token、分类不存在、上传空文件

### 周五

- 前端：分类管理 + 商品列表 / 表单 / 上传
- 对接分页组件；封面用上传组件

### 周六（3～4h）

- `mvn package`，jar 启动
- Nginx 反代前端，后端 8080（不会云服务器就本机 Docker / 本机 Nginx）
- README 补「如何启动上传目录、如何分页」

### 周日（2～3h）

- 口述：一对多怎么建表、分页怎么写、文件存在哪
- 预习第 3 周：事务、订单状态机、超卖

---

## 本周不做

- 不上 Redis
- 不上订单和支付
- 不为美化后台 UI 消耗全部时间

---

## 面试可讲点

- 逻辑删除 vs 物理删除  
- 给 `category_id` 加索引的原因  
- 深分页为什么慢（`LIMIT 100000, 10`）

---

## 口述笔记

### 一对多怎么建表

- 一方：`category`（分类）
- 多方：`product`（商品）
- 在多方表存外键：`product.category_id` 指向 `category.id`
- `category_id` 上建索引，便于按分类筛选、关联查询

### 分页怎么写

- 入参：页码、每页条数，以及名称、分类、上架状态等筛选条件
- 使用 MyBatis-Plus 的 `Page` + 分页插件；条件用 `LambdaQueryWrapper` 拼接
- SQL 本质是 `LIMIT offset, size`，其中 `offset = (pageNum - 1) * pageSize`
- 返回列表数据与总条数（如 `records`、`total`），前端据此渲染分页组件
- 逻辑删除的商品查询时不应出现在列表中

### 文件存在哪

- 上传接口将文件保存到配置的本地目录（如 `uploads/`）
- 数据库商品字段存可访问路径（如 `/uploads/文件名`）
- 通过静态资源映射，让浏览器能用该 URL 访问到磁盘上的文件
- 启动应用时的工作目录会影响相对路径落盘位置，部署时要注意
