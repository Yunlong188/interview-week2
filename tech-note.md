# Docker Compose 在本地联调的注意点

本文为本周拆分微服务并使用 Docker Compose 联调时的实践总结与经验，面向在本地同时运行多个服务并进行端到端调试的场景。文中包含常见问题、解决方法与建议命令示例。

背景

- 项目已拆分为 `user-service` 与 `order-service` 两个服务，使用独立的 Spring Boot 应用和不同端口。我们希望在本地通过 Docker Compose 启动多服务环境进行联调与集成测试。

关键注意点

1. 网络与服务发现

   - 使用 Compose 默认网络（bridge）时，容器之间可以通过服务名互相访问（例如 `http://user-service:8080`）。在 Spring Boot 配置中使用服务名而非 `localhost`。

   - 若需与宿主机（IDE）互通，注意不要把服务绑定到 `0.0.0.0` 以外的地址；在容器内调用宿主需要使用特殊主机名（Windows/Mac: `host.docker.internal`）。

2. 端口冲突与映射
   - 本地开发时常见端口冲突。Compose 文件里把容器端口与宿主端口分离，例如 `8081:8080`，使两个服务在宿主上占用不同端口。
   - 使用环境变量或 profiles 来配置服务的端口，避免在代码中硬编码端口号。

3. 配置与环境一致性
   - 将数据库、缓存等依赖也放到 Compose 中（如 MySQL、Redis），并使用持久化卷来保留数据以便重复调试。
   - 把敏感配置（密码、API Key）通过 `.env` 或 Docker secrets 管理，不要直接写入仓库。

4. 依赖启动顺序与健康检查
   - 容器内服务可能在依赖尚未就绪时启动，导致连接失败。使用 `depends_on` 只能控制启动顺序，但不能保证就绪。建议在应用中实现重试逻辑或在 Compose 中使用健康检查（`healthcheck`）配合 `depends_on.condition`（兼容性依赖 Compose 版本）。

5. 本地调试与源码挂载
   - 为了快速调试，挂载源码卷（bind mount）到容器里可以实现无需重建镜像就能热加载代码。但注意宿主与容器的文件权限与构建工具缓存差异。
   - 对于 Java/Spring Boot，可在 IDE 使用“远程调试”模式（暴露调试端口），Compose 中设置 `JAVA_TOOL_OPTIONS` 或启动参数 `-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:5005`。

6. 日志与可观测性

   - 将日志输出到 stdout/stderr（不要只写文件），便于 `docker-compose logs` 聚合查看。为每个服务配置不同的日志级别与格式，便于在多服务场景中快速定位问题。

实用命令示例

启动并构建镜像：

```bash
docker-compose up --build
```

在后台启动：

```bash
docker-compose up -d --build
```

查看某个服务日志：

```bash
docker-compose logs -f order-service
```

在容器中打开交互 shell（以 order-service 为例）：

```bash
docker-compose exec order-service /bin/sh
```

建议与结论

- 把 Compose 文件作为开发环境配置管理的一部分，保持简洁并用注释说明每个服务的用途。
- 优先保证服务之间的网络与配置可替换（通过环境变量），避免在容器内部写死外部依赖地址。
- 为常见错误（端口冲突、依赖未就绪、环境变量缺失）建立检查清单，节省排查时间。

通过这些实践，我们能在本地高效地复现多服务场景并完成端到端调试，为后续迁移到容器编排平台（Kubernetes）打好基础。
