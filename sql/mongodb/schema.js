// 使用 Mongo Shell 或 mongosh 执行
// 切换数据库
use wechat;

// 创建集合（可选加入简单的验证规则）
db.createCollection("users");
db.createCollection("messages");
db.createCollection("chat_groups");
db.createCollection("group_members");
db.createCollection("group_messages");
db.createCollection("friendships");

// 索引（提高查询性能）
db.messages.createIndex({ sender_id: 1 });
db.messages.createIndex({ receiver_id: 1 });
db.chat_groups.createIndex({ creator_id: 1 });
db.group_members.createIndex({ group_id: 1 });
db.group_members.createIndex({ user_id: 1 });
db.group_messages.createIndex({ group_id: 1 });
db.group_messages.createIndex({ sender_id: 1 });
db.friendships.createIndex({ user_id: 1 });
db.friendships.createIndex({ friend_id: 1 });
db.users.createIndex({ username: 1 }, { unique: true });
db.users.createIndex({ email: 1 }, { unique: true });
db.users.createIndex({ phone: 1 }, { unique: true });

