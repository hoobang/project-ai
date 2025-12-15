// 示例数据导入
use wechat;

db.users.insertMany([
  { _id: 1, username: "alice", email: "alice@example.com", phone: "13000000001", password: "$2a$10$7EqJtq98hPqEX7fNZaFWoOHi1CR5R.pItcBk.uI3Yyii1K9ofuSGK", nickname: "Alice", gender: "F", active: true, created_at: new Date(), updated_at: new Date() },
  { _id: 2, username: "bob", email: "bob@example.com", phone: "13000000002", password: "$2a$10$7EqJtq98hPqEX7fNZaFWoOHi1CR5R.pItcBk.uI3Yyii1K9ofuSGK", nickname: "Bob", gender: "M", active: true, created_at: new Date(), updated_at: new Date() },
  { _id: 3, username: "charlie", email: "charlie@example.com", phone: "13000000003", password: "$2a$10$7EqJtq98hPqEX7fNZaFWoOHi1CR5R.pItcBk.uI3Yyii1K9ofuSGK", nickname: "Charlie", gender: "M", active: true, created_at: new Date(), updated_at: new Date() }
]);

db.friendships.insertMany([
  { _id: 1, user_id: 1, friend_id: 2, status: "ACCEPTED", created_at: new Date(), updated_at: new Date(), accepted_at: new Date() },
  { _id: 2, user_id: 2, friend_id: 3, status: "PENDING", created_at: new Date(), updated_at: new Date() }
]);

db.messages.insertMany([
  { _id: 1, sender_id: 1, receiver_id: 2, content: "Hi Bob!", type: "TEXT", is_read: false, created_at: new Date(), updated_at: new Date() },
  { _id: 2, sender_id: 2, receiver_id: 1, content: "Hey Alice!", type: "TEXT", is_read: false, created_at: new Date(), updated_at: new Date() },
  { _id: 3, sender_id: 1, receiver_id: 3, content: "Hi Charlie!", type: "TEXT", is_read: false, created_at: new Date(), updated_at: new Date() }
]);

db.chat_groups.insertOne({ _id: 1, group_name: "Demo Group", creator_id: 1, member_count: 3, max_members: 500, is_public: true, invite_code: "ABC123", created_at: new Date(), updated_at: new Date() });

db.group_members.insertMany([
  { _id: 1, group_id: 1, user_id: 1, group_nickname: "Alice", role: "OWNER", is_muted: false, joined_at: new Date(), updated_at: new Date() },
  { _id: 2, group_id: 1, user_id: 2, group_nickname: "Bob", role: "ADMIN", is_muted: false, joined_at: new Date(), updated_at: new Date() },
  { _id: 3, group_id: 1, user_id: 3, group_nickname: "Charlie", role: "MEMBER", is_muted: false, joined_at: new Date(), updated_at: new Date() }
]);

db.group_messages.insertMany([
  { _id: 1, group_id: 1, sender_id: 1, content: "Welcome to the group!", type: "TEXT", created_at: new Date(), updated_at: new Date() },
  { _id: 2, group_id: 1, sender_id: 2, content: "Thanks!", type: "TEXT", created_at: new Date(), updated_at: new Date() },
  { _id: 3, group_id: 1, sender_id: 3, content: "Hello everyone!", type: "TEXT", created_at: new Date(), updated_at: new Date() }
]);

