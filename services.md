# Services

## 概述

- 目标：把单体应用拆分成两个服务：user-service 和 order-service；保留一个 simple-common （共享 DTO/异常）。
- 通信方式：REST（内部服务通过 HTTP 调用，后续可替换为 Feign/消息队列）。
- 数据存储：每个服务独立数据库（示例：user -> user_db, order -> order_db）。

## 服务职责

- user-service
  - 责任：用户注册、认证、用户基本信息查询
  - 数据：users 表（id, username, password, email, created_at）
- order-service
  - 责任：创建订单、查询订单、订单状态流转
  - 在创建订单时需校验用户是否存在（调用 user-service）
  - 数据：orders 表（id, user_id, product_id, amount, status, created_at）

## API 列表

### user-service (端口 8080)

- POST /api/users

  - 描述：注册用户
  - 请求体：

  ```json
  {
    "username": "string",
    "password": "string",
    "email": "string"
  }
  ```

  - 响应 201：

  ```json
  {
    "id": "long",
    "username": "string",
    "email": "string",
    "created_at": "string"
  }
  ```

- GET /api/users/{id}

  - 描述：获取用户信息
  - 响应 200：

  ```json
  {
    "id": "long",
    "username": "string",
    "email": "string",
    "created_at": "string"
  }
  ```

- POST /api/auth/login

  - 描述：用户登录，返回 JWT token
  - 请求体：

  ```json
  {
    "username": "string",
    "password": "string"
  }
  ```

  - 响应 200：

  ```json
  {
    "token": "string"
  }
  ```

### order-service (端口 8081)

- POST /api/orders

  - 描述：创建订单（在创建前调用 user-service 校验 userId）
  - 请求体：

  ```json
  {
    "userId": "long",
    "items": [
      {
        "productId": "string",
        "qty": "integer"
      }
    ]
  }
  ```

  - 响应 201：

  ```json
  {
    "id": "string",
    "status": "string",
    "created_at": "string"
  }
  ```

- GET /api/orders/{id}

  - 描述：查询订单
  - 响应 200：

  ```json
  {
    "id": "string",
    "userId": "long",
    "items": [
      {
        "productId": "string",
        "qty": "integer"
      }
    ],
    "status": "string",
    "created_at": "string"
  }
  ```

- PUT /api/orders/{id}/status

  - 描述：更新订单状态（如：PENDING -> CONFIRMED）
  - 请求体：

  ```json
  {
    "status": "string"
  }
  ```

  - 响应 200：

  ```json
  {
    "id": "string",
    "status": "string",
    "created_at": "string"
  }
  ```

## 简单 ASCII 架构图

[client] --> [order-service (8081)] --> [user-service (8080)]  
order-service -> order_db  
user-service -> user_db  
(common DTOs in simple-common)  
