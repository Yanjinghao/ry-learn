# week1_shop 前端

Vue 3 + TypeScript + Vite + Element Plus + Pinia + Axios

## 启动

先确保后端已运行：`http://127.0.0.1:8080`

```powershell
cd D:\workspace\ry\week1_shop\frontend
npm run dev
```

浏览器打开：http://localhost:5173

## 功能

- `/login` 登录（JWT 存 Pinia + localStorage）
- `/register` 注册
- `/users` 用户列表：筛选、详情、改昵称、删除（请求自动带 Bearer Token）

## 代理

Vite 将 `/api`、`/health` 代理到 `http://127.0.0.1:8080`
