# Week 2

## 设计服务边界

### 服务职责

- user-service
  - 责任：用户注册、认证、用户基本信息查询
  - 数据：users 表（id, username, password, email, created_at）
- order-service
  - 责任：创建订单、查询订单、订单状态流转
  - 在创建订单时需校验用户是否存在（调用 user-service）
  - 数据：orders 表（id, user_id, product_id, amount, status, created_at）

### 简单 ASCII 架构图

[client] --> [order-service (8081)] --> [user-service (8080)]  
order-service -> order_db  
user-service -> user_db  
(common DTOs in simple-common)

## 项目结构

- `user-service/` — 用户服务模块，放置用户相关的 Controller/Service/Entity。
- `order-service/` — 订单服务模块，放置订单相关逻辑并调用用户服务进行校验。
- `simple-common/` — 公共模块，放置共享 DTO/常量/工具类。
- `pom.xml` — 父/聚合 POM（位于仓库根，用于多模块构建）。
- `mvnw`, `mvnw.cmd` — Maven wrapper（用于统一构建）。
- `docker-compose.yml` (若存在) — 本地联调的 compose 配置（如未添加，可在周三产出）。
- `*.md`（`本周计划.md`、`services.md`、`验收文档.md`、`README.md` 等） — 文档与验收材料。

## 本地容器化与 Docker Compose

### 单独启动服务

- user-service

```bash
cd user-service
docker build -t user-service:week2 .
docker run -p 8082:8082 user-service:week2
```

- order-service

```bash
cd order-service
docker build -t order-service:week2 .
docker run -p 8081:8081 order-service:week2
```

### Docker Compose

```bash
docker-compose up --build
```

## 接口契约与集成验证

### 接口契约

#### user-service

- **注册用户**

  - 请求：`POST /users/register`
  - 请求体：

    ```json
    {
      "username": "johndoe",
      "password": "securepassword"
    }
    ```

  - 响应：

    ```json
    {
      "id": "dacb27d0-c3b3-8279-59d0-f87381de8c3d",
      "username": "johndoe"
    }
    ```

- **获取用户信息**

  - 请求：`GET /users/{id}`
  - 响应：

    ```json
    {
      "id": "dacb27d0-c3b3-8279-59d0-f87381de8c3d",
      "username": "johndoe"
    }
    ```

- **用户认证**

  - 请求：`POST /users/login`
  - 请求体：

    ```json
    {
      "username": "johndoe",
      "password": "securepassword"
    }
    ```

  - 响应：

    ```json
    {
      "id": "dacb27d0-c3b3-8279-59d0-f87381de8c3d",
      "username": "johndoe"
    }
    ```

#### order-service

- **创建订单**

  - 请求：`POST /orders`
  - 请求体：

    ```json
    {
      "userId": "dacb27d0-c3b3-8279-59d0-f87381de8c3d",
      "items": [
        {
          "product": "prod-123",
          "quantity": 2
        }
      ]
    }
    ```

  - 响应：
    701e2774-6b89-39c9-0df2-4762a9f37c79

- **查询订单**

  - 请求：`GET /orders/{id}`
  - 响应：

    ```json
    {
      "id": "701e2774-6b89-39c9-0df2-4762a9f37c79",
      "userId": "dacb27d0-c3b3-8279-59d0-f87381de8c3d",
      "items": [
        {
          "product": "prod-123",
          "quantity": 2
        }
      ]
    }
    ```

### 集成验证

- **创建用户**

```bash
curl -X POST http://localhost:8082/api/users/register -H "Content-Type: application/json" -d '{"username": "johndoe","password": "securepassword"}'
```

- **创建订单并验证用户**

```bash
curl -X POST http://localhost:8083/api/orders -H "Content-Type: application/json" -d '{"userId": "dacb27d0-c3b3-8279-59d0-f87381de8c3d", "items": [{"product": "prod-123","quantity": 2}]}'
```

- **查询订单**

```bash
curl -X GET http://localhost:8083/api/orders/701e2774-6b89-39c9-0df2-4762a9f37c79
```

## 搭建 GitHub Actions CI（基础）
