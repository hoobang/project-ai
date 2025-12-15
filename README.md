# Project AI（微信聊天示例项目）

## 项目概览
- 前后端分离的即时通讯示例，包含用户注册/登录、私聊、群聊、好友关系、未读消息与实时推送。
- 前端：`wechat-frontend`（Vite + Vue）
- 后端：`wechat-backend`（Spring Boot + Security + JWT + WebSocket(STOMP) + JPA）
- 数据库：MySQL（通过 JPA 自动建表/更新）

## 核心功能
- 用户注册与登录（JWT）
  - 接口：`POST /api/auth/register`、`POST /api/auth/login`
  - 认证：HTTP Header `Authorization: Bearer <token>`
- 私聊消息
  - REST：查询历史、发送消息、获取未读、标记已读
  - WebSocket：实时推送到用户队列 `/queue/messages/{userId}`
- 群聊消息
  - 群创建/加入/退出/成员管理（角色变更、禁言/解禁、移除）
  - WebSocket：群消息广播到 `/topic/group/{groupId}`
- 好友关系
  - 发送/接受/拒绝好友请求、删除好友
  - 查询好友列表、待处理/已发送请求

## 架构与模块
- 控制器（REST + WebSocket）
  - `AuthController`：认证与登录 `wechat-backend/src/main/java/com/wechat/controller/AuthController.java`
  - `MessageController`：私聊消息 `.../controller/MessageController.java`
  - `FriendshipController`：好友关系 `.../controller/FriendshipController.java`
  - `WebSocketController`：STOMP 消息入口 `.../controller/WebSocketController.java`
- 安全与配置
  - `SecurityConfig`：无状态安全、路径授权、JWT 过滤器 `.../config/SecurityConfig.java`
  - `JwtTokenUtil`：令牌生成/解析/校验 `.../config/JwtTokenUtil.java`
  - `JwtAuthenticationFilter`：HTTP 请求JWT认证 `.../config/JwtAuthenticationFilter.java`
  - `WebSocketConfig`：STOMP 端点/代理/前缀 `.../config/WebSocketConfig.java`
  - `WebSocketAuthInterceptor`：STOMP 连接阶段认证 `.../config/WebSocketAuthInterceptor.java`
- 领域模型（JPA 实体）
  - 用户 `User`、私聊消息 `Message`
  - 群组 `Group`、群成员 `GroupMember`、群消息 `GroupMessage`
  - 好友关系 `Friendship`
- 领域服务与实现
  - `UserService`、`MessageService`、`FriendshipService`、`GroupService`
  - 对应的 `impl` 实现类与仓储接口 `repository/*`

## 实现要点
- 认证与授权
  - 使用 Spring Security 无状态模式（禁用会话），JWT 用于鉴权
  - 通过 `JwtAuthenticationFilter` 从 `Authorization` 头提取 Bearer Token
  - 方法级权限控制使用 `@PreAuthorize`（已开启 `@EnableMethodSecurity`）
- WebSocket/STOMP
  - 端点：`/ws`（SockJS 兼容）
  - 应用前缀：`/app`（客户端发送使用 `/app/...`）
  - 代理前缀：`/topic`、`/queue`
  - 私聊入口：`@MessageMapping("/message/send")` → 推送到 `/queue/messages/{userId}`
  - 群聊入口：`@MessageMapping("/group/send")` → 推送到 `/topic/group/{groupId}`
- 数据持久化
  - MySQL 驱动：`com.mysql:mysql-connector-j`
  - JPA/Hibernate：`spring.jpa.hibernate.ddl-auto=update` 自动维护表结构
  - 常用查询通过 `repository` 的派生方法与 `@Query` 完成

## 接口概览
- 认证 `AuthController`
  - `POST /api/auth/register` 注册
  - `POST /api/auth/login` 登录 → 返回 `token` 与用户信息
  - `GET /api/auth/me` 获取当前用户
- 私聊 `MessageController`
  - `GET /api/messages/history?receiverId=` 历史消息
  - `POST /api/messages/send` 发送消息（REST）
  - `GET /api/messages/unread` 未读消息列表
  - `PUT /api/messages/read/{messageId}` 标记已读
  - `PUT /api/messages/read/all?senderId=` 会话全部已读
- 好友 `FriendshipController`
  - `GET /api/friends/list` 好友列表
  - `POST /api/friends/request` 发送请求
  - `POST /api/friends/accept` 接受请求
  - `POST /api/friends/reject` 拒绝请求
  - `DELETE /api/friends/delete/{friendId}` 删除好友
  - `GET /api/friends/requests/pending` 待处理请求列表
- WebSocket
  - 客户端连接：`/ws`，发送到 `/app/message/send` 或 `/app/group/send`
  - 私聊订阅：`/queue/messages/{userId}`
  - 群聊订阅：`/topic/group/{groupId}`

## 配置与环境
- 后端 `application.properties`（关键项示例）
  - `server.port=8080`，`server.servlet.context-path=/api`
  - `spring.datasource.*` 配置数据库连接
  - `jwt.secret`、`jwt.expiration`（请按生产环境要求设置，勿暴露真实秘钥）
  - CORS 全开（示例），实际生产需收敛来源与方法
- 编译与运行
  - JDK：17（`pom.xml` 已配置 `maven.compiler.release=17`）
  - 后端：`mvn spring-boot:run`
  - 前端：`npm install` → `npm run dev`（默认 Vite 开发服务器）

## 启动流程
1. 准备数据库（创建 `wechat` 库）并更新 `application.properties` 的数据库账号
2. 启动后端：`mvn spring-boot:run`，服务监听 `http://localhost:8080/api`
3. 启动前端：`npm run dev`，在浏览器打开前端地址
4. 前端登录后持有 JWT，在所有受保护接口与 STOMP 连接时附带 `Authorization: Bearer <token>`

## 代码导航
- 应用入口：`wechat-backend/src/main/java/com/wechat/WechatApplication.java`
- 安全配置：`.../config/SecurityConfig.java`
- JWT 工具：`.../config/JwtTokenUtil.java`
- WebSocket 配置与拦截：`.../config/WebSocketConfig.java`、`.../config/WebSocketAuthInterceptor.java`
- 控制器：`.../controller/*`
- 服务接口与实现：`.../service/*`、`.../service/impl/*`
- 仓储接口：`.../repository/*`
- 实体：`.../entity/*`

## 注意事项
- 生产环境请使用强度足够的 `jwt.secret` 并通过环境变量或外部配置管理，不要硬编码到仓库
- CORS 与 SockJS 配置需根据部署拓扑适配具体域名与证书策略
- WebSocket 与 REST 认证策略需保持一致，避免权限绕过

