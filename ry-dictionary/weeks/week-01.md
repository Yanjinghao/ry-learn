# 第 1 周：环境搭建 + 登录与用户 CRUD

**目标：** 跑通「Vue 登录页 → Spring Boot → MySQL」，完成注册 / 登录 / JWT 鉴权 / 用户列表。  
**项目目录建议：** `D:\workspace\ry\week1_shop`  
**本周不做：** 若依、Spring Security 全家桶、验证码、Redis、文件上传。

---

## 周末验收

- [ ] `GET /health` 正常
- [ ] 注册、登录、改昵称、禁用用户都通
- [ ] 不带 token 访问 `/user/list` 返回 401
- [ ] 错误密码不泄露「用户是否存在」这类可枚举信息
- [ ] 前端登录后能看到用户列表
- [ ] 能用自己的话画出一次登录请求经过哪些类

---

## 周一｜环境与空项目（3h）

| 分钟 | 任务 |
|:---:|------|
| 0–20 | 确认 JDK 17、Maven、MySQL 8、Git、Node LTS |
| 20–70 | 建仓库：`backend/`（Spring Boot）+ `frontend/`（先空着）+ 根目录 `.gitignore` |
| 70–120 | `application.yml` 连上 MySQL；写 `GET /health` → `{"ok": true}` |
| 120–160 | 统一返回体 `{code, msg, data}` 先定下来 |
| 160–180 | 笔记 + 提交 |

**验收：** 浏览器或 curl 访问 `http://127.0.0.1:8080/health` 看到 ok。

```powershell
cd D:\workspace\ry\week1_shop\backend
mvn spring-boot:run
```

---

## 周二｜建表 + 用户 CRUD（3h）

| 分钟 | 任务 |
|:---:|------|
| 0–20 | 复习分层：Controller → Service → Mapper |
| 20–70 | 自己写 SQL 建 `user` 表（不要生成器） |
| 70–150 | 注册、按 id 查询、用户列表、改昵称 |
| 150–170 | Apifox 测通 |
| 170–180 | 提交 |

**表结构（自己执行）：**

```sql
CREATE TABLE user (
  id            BIGINT PRIMARY KEY AUTO_INCREMENT,
  username      VARCHAR(32) NOT NULL UNIQUE,
  password      VARCHAR(100) NOT NULL,
  nickname      VARCHAR(32),
  status        TINYINT NOT NULL DEFAULT 1,
  created_at    DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
);
```

---

## 周三｜BCrypt + JWT 登录（3h）

| 分钟 | 任务 |
|:---:|------|
| 0–15 | 复习；跑通昨天接口 |
| 15–50 | 注册时 BCrypt 加密密码（禁止明文入库） |
| 50–140 | 登录：查用户 + 校验密码 + 返回 JWT |
| 140–170 | 错误密码返回统一业务错误，不写「查无此用户」 |
| 170–180 | 提交 |

**示例 curl：**

```bash
curl -X POST http://127.0.0.1:8080/api/auth/login -H "Content-Type: application/json" -d "{\"username\":\"admin\",\"password\":\"123456\"}"
```

---

## 周四｜拦截器鉴权（3h）

| 分钟 | 任务 |
|:---:|------|
| 0–20 | 阅读 JWT 放 Header：`Authorization: Bearer <token>` |
| 20–100 | 拦截器：无 token / 假 token → 401；登录、注册、health 放行 |
| 100–150 | 列表接口必须登录才能访问 |
| 150–180 | 测通后提交 |

---

## 周五｜校验、全局异常、前端登录（3h）

| 分钟 | 任务 |
|:---:|------|
| 0–50 | `@Valid`：用户名为空、密码太短 |
| 50–90 | 全局异常处理，全部走统一返回体 |
| 90–160 | Vue3 登录页 + 用户列表，token 存本地并带上请求头 |
| 160–180 | CORS 放行前端端口；提交 |

---

## 周六｜打磨与 README（3～4h）

1. 禁用用户后不能登录
2. `.gitignore` 排除 `target/`、`node_modules/`、本地配置密钥
3. README：启动方式、建表 SQL、架构三句话
4. 自测后打 tag：`v0.1.0`

---

## 周日｜复盘 + 预习第 2 周（2～3h）

- 口述：一次登录经过哪些层、密码为何 BCrypt、token 丢了会怎样
- 预习：一对多、分页、`LIMIT`、文件上传

---

## 本周注意

- 表结构手写，不要用若依或代码生成器
- JWT + 拦截器够了，不要一上来上完整 Spring Security
- 卡住时记录报错，优先解决：连库失败 / 密码没加密 / CORS / 拦截器把登录也拦了
