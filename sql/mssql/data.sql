USE [wechat];
GO

SET IDENTITY_INSERT dbo.users ON;
INSERT INTO dbo.users (id, username, email, phone, password, nickname, gender, active, created_at, updated_at)
VALUES (1, N'alice', N'alice@example.com', N'13000000001', N'$2a$10$7EqJtq98hPqEX7fNZaFWoOHi1CR5R.pItcBk.uI3Yyii1K9ofuSGK', N'Alice', N'F', 1, SYSDATETIME(), SYSDATETIME());
INSERT INTO dbo.users (id, username, email, phone, password, nickname, gender, active, created_at, updated_at)
VALUES (2, N'bob', N'bob@example.com', N'13000000002', N'$2a$10$7EqJtq98hPqEX7fNZaFWoOHi1CR5R.pItcBk.uI3Yyii1K9ofuSGK', N'Bob', N'M', 1, SYSDATETIME(), SYSDATETIME());
INSERT INTO dbo.users (id, username, email, phone, password, nickname, gender, active, created_at, updated_at)
VALUES (3, N'charlie', N'charlie@example.com', N'13000000003', N'$2a$10$7EqJtq98hPqEX7fNZaFWoOHi1CR5R.pItcBk.uI3Yyii1K9ofuSGK', N'Charlie', N'M', 1, SYSDATETIME(), SYSDATETIME());
SET IDENTITY_INSERT dbo.users OFF;
GO

SET IDENTITY_INSERT dbo.friendships ON;
INSERT INTO dbo.friendships (id, user_id, friend_id, status, created_at, updated_at, accepted_at)
VALUES (1, 1, 2, N'ACCEPTED', SYSDATETIME(), SYSDATETIME(), SYSDATETIME());
INSERT INTO dbo.friendships (id, user_id, friend_id, status, created_at, updated_at, accepted_at)
VALUES (2, 2, 3, N'PENDING', SYSDATETIME(), SYSDATETIME(), NULL);
SET IDENTITY_INSERT dbo.friendships OFF;
GO

SET IDENTITY_INSERT dbo.messages ON;
INSERT INTO dbo.messages (id, sender_id, receiver_id, content, type, is_read, created_at, updated_at)
VALUES (1, 1, 2, N'Hi Bob!', N'TEXT', 0, SYSDATETIME(), SYSDATETIME());
INSERT INTO dbo.messages (id, sender_id, receiver_id, content, type, is_read, created_at, updated_at)
VALUES (2, 2, 1, N'Hey Alice!', N'TEXT', 0, SYSDATETIME(), SYSDATETIME());
INSERT INTO dbo.messages (id, sender_id, receiver_id, content, type, is_read, created_at, updated_at)
VALUES (3, 1, 3, N'Hi Charlie!', N'TEXT', 0, SYSDATETIME(), SYSDATETIME());
SET IDENTITY_INSERT dbo.messages OFF;
GO

SET IDENTITY_INSERT dbo.chat_groups ON;
INSERT INTO dbo.chat_groups (id, group_name, group_avatar, group_description, creator_id, member_count, max_members, is_public, invite_code, created_at, updated_at)
VALUES (1, N'Demo Group', NULL, N'Demo group for testing', 1, 3, 500, 1, N'ABC123', SYSDATETIME(), SYSDATETIME());
SET IDENTITY_INSERT dbo.chat_groups OFF;
GO

SET IDENTITY_INSERT dbo.group_members ON;
INSERT INTO dbo.group_members (id, group_id, user_id, group_nickname, role, is_muted, joined_at, updated_at)
VALUES (1, 1, 1, N'Alice', N'OWNER', 0, SYSDATETIME(), SYSDATETIME());
INSERT INTO dbo.group_members (id, group_id, user_id, group_nickname, role, is_muted, joined_at, updated_at)
VALUES (2, 1, 2, N'Bob', N'ADMIN', 0, SYSDATETIME(), SYSDATETIME());
INSERT INTO dbo.group_members (id, group_id, user_id, group_nickname, role, is_muted, joined_at, updated_at)
VALUES (3, 1, 3, N'Charlie', N'MEMBER', 0, SYSDATETIME(), SYSDATETIME());
SET IDENTITY_INSERT dbo.group_members OFF;
GO

SET IDENTITY_INSERT dbo.group_messages ON;
INSERT INTO dbo.group_messages (id, group_id, sender_id, content, type, created_at, updated_at)
VALUES (1, 1, 1, N'Welcome to the group!', N'TEXT', SYSDATETIME(), SYSDATETIME());
INSERT INTO dbo.group_messages (id, group_id, sender_id, content, type, created_at, updated_at)
VALUES (2, 1, 2, N'Thanks!', N'TEXT', SYSDATETIME(), SYSDATETIME());
INSERT INTO dbo.group_messages (id, group_id, sender_id, content, type, created_at, updated_at)
VALUES (3, 1, 3, N'Hello everyone!', N'TEXT', SYSDATETIME(), SYSDATETIME());
SET IDENTITY_INSERT dbo.group_messages OFF;
GO

