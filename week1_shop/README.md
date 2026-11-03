# week1_shop

第 1～4 周练习项目：自研简易商城（登录 / 商品 / 订单 / RBAC）。

## 目录

```
week1_shop/
├── backend/     Spring Boot 3 + Java 17
├── frontend/    Vue3 + TS + Element Plus + Pinia
└── .gitignore
```

## 准备
JDK17 / Maven / MySQL / Node

## 后端架构
浏览器 Vue 页面调 /api，开发时由 Vite 代理到 Spring Boot。
后端经 JWT 拦截器鉴权后，走 Controller → Service → Mapper 访问 MySQL。
统一返回 { code, msg, data }，登录成功后发 JWT，后续请求带 Authorization: Bearer <token>。

## 建表SQL
CREATE DATABASE IF NOT EXISTS week1_shop DEFAULT CHARACTER SET utf8mb4;

USE week1_shop;

CREATE TABLE user (
  id            BIGINT PRIMARY KEY AUTO_INCREMENT,
  username      VARCHAR(32) NOT NULL UNIQUE,
  password      VARCHAR(100) NOT NULL,
  nickname      VARCHAR(32),
  status        TINYINT NOT NULL DEFAULT 1,
  created_at    DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
);

## 后端启动

```powershell
$env:JAVA_HOME = "D:\application\jdk17"
$env:Path = "D:\application\jdk17\bin;D:\application\maven\apache-maven-3.10.0-bin\apache-maven-3.10.0\bin;" + $env:Path

cd D:\workspace\ry\week1_shop\backend
mvn spring-boot:run
```

## 前端启动

另开终端：

```powershell
cd D:\workspace\ry\week1_shop\frontend
npm run dev
```

浏览器：http://localhost:5173  
`/api` 已代理到 `http://127.0.0.1:8080`

学习计划见：`D:\workspace\ry\ry-dictionary\`
