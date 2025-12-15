# SQL 脚本说明

## 目录结构
- `sql/mysql/`
  - `schema.sql`：MySQL 建库与建表脚本（含外键与索引）
  - `data.sql`：MySQL 示例数据（用户、好友、消息、群、群成员、群消息）
- `sql/oracle/`
  - `schema.sql`：Oracle 建表与约束脚本（12c+，使用 IDENTITY BY DEFAULT）
  - `data.sql`：Oracle 示例数据（时间类型使用 `SYSTIMESTAMP`）
- `sql/mssql/`
  - `schema.sql`：SQL Server 建库、建表与约束脚本
  - `data.sql`：SQL Server 示例数据（使用 `SET IDENTITY_INSERT` 指定主键）
- `sql/mongodb/`
  - `schema.js`：MongoDB 集合与索引创建脚本（`mongosh`）
  - `data.js`：MongoDB 示例数据（`insertMany`）
- 根目录历史脚本（保留）：
  - `sql/schema_mysql.sql`、`sql/data_mysql.sql`（与 `sql/mysql/*` 等价，推荐优先使用分目录版本）

## 设计约束
- 表与字段命名与后端 JPA 实体对应：`users`、`messages`、`friendships`、`chat_groups`、`group_members`、`group_messages`
- 主键：关系型数据库使用自增/IDENTITY；MongoDB 使用数值 `_id` 与关系型保持一致
- 唯一约束：`users.username/email/phone` 唯一；`group_members(group_id, user_id)` 唯一；`friendships(user_id, friend_id)` 唯一
- 外键与索引：为消息、群、好友等常用查询路径添加索引与外键约束

## 导入指南
- MySQL
  - `mysql -u <user> -p < sql/mysql/schema.sql`
  - `mysql -u <user> -p < sql/mysql/data.sql`
- Oracle
  - `sqlplus <user>/<pass>@<service> @sql/oracle/schema.sql`
  - `sqlplus <user>/<pass>@<service> @sql/oracle/data.sql`
- SQL Server
  - `sqlcmd -S <server> -d master -i sql/mssql/schema.sql`
  - `sqlcmd -S <server> -d wechat -i sql/mssql/data.sql`
- MongoDB
  - `mongosh <host>/wechat sql/mongodb/schema.js`
  - `mongosh <host>/wechat sql/mongodb/data.js`

## 示例数据说明
- 用户：`alice`、`bob`、`charlie`，密码为示例的 BCrypt 哈希（明文：`password`）
- 好友关系：`alice↔bob` 已接受；`bob→charlie` 待处理
- 消息：三条私聊文本消息
- 群：`Demo Group`，成员三人（角色：OWNER/ADMIN/MEMBER）及三条群消息

## 与后端配置的关系
- 默认后端使用 MySQL（`application.properties` 中的 `spring.datasource.*`），如切换到其它数据库，请调整连接、驱动与方言
- JWT、CORS 等安全配置不受数据库变更影响

## 注意事项
- 请勿在仓库中存储真实数据库账号、密码、连接串等敏感信息；生产环境通过环境变量或外部配置管理
- 不同数据库类型的时间/自增策略存在差异，脚本已按各自规范处理；如版本差异导致不兼容，请根据实际版本微调

