IF NOT EXISTS (SELECT * FROM sys.databases WHERE name = N'wechat')
BEGIN
  CREATE DATABASE [wechat];
END
GO
USE [wechat];
GO

IF OBJECT_ID('dbo.users', 'U') IS NULL
CREATE TABLE dbo.users (
  id BIGINT IDENTITY(1,1) PRIMARY KEY,
  username NVARCHAR(50) NOT NULL UNIQUE,
  email NVARCHAR(100) NOT NULL UNIQUE,
  phone NVARCHAR(11) NOT NULL UNIQUE,
  password NVARCHAR(255) NOT NULL,
  nickname NVARCHAR(100),
  avatar NVARCHAR(255),
  signature NVARCHAR(500),
  gender NVARCHAR(20),
  location NVARCHAR(255),
  birth_date DATETIME2,
  created_at DATETIME2 NOT NULL,
  updated_at DATETIME2,
  last_login DATETIME2,
  active BIT DEFAULT 1
);
GO

IF OBJECT_ID('dbo.messages', 'U') IS NULL
CREATE TABLE dbo.messages (
  id BIGINT IDENTITY(1,1) PRIMARY KEY,
  sender_id BIGINT NOT NULL,
  receiver_id BIGINT NOT NULL,
  content NVARCHAR(MAX),
  type NVARCHAR(20) NOT NULL,
  file_path NVARCHAR(255),
  file_name NVARCHAR(100),
  file_size BIGINT,
  is_read BIT NOT NULL DEFAULT 0,
  created_at DATETIME2 NOT NULL,
  updated_at DATETIME2
);
GO
CREATE INDEX idx_messages_sender ON dbo.messages(sender_id);
CREATE INDEX idx_messages_receiver ON dbo.messages(receiver_id);
GO

IF OBJECT_ID('dbo.chat_groups', 'U') IS NULL
CREATE TABLE dbo.chat_groups (
  id BIGINT IDENTITY(1,1) PRIMARY KEY,
  group_name NVARCHAR(100) NOT NULL,
  group_avatar NVARCHAR(255),
  group_description NVARCHAR(MAX),
  creator_id BIGINT NOT NULL,
  member_count INT NOT NULL DEFAULT 0,
  max_members INT NOT NULL DEFAULT 500,
  is_public BIT NOT NULL DEFAULT 1,
  invite_code NVARCHAR(20) UNIQUE,
  created_at DATETIME2 NOT NULL,
  updated_at DATETIME2
);
GO
CREATE INDEX idx_groups_creator ON dbo.chat_groups(creator_id);
GO

IF OBJECT_ID('dbo.group_members', 'U') IS NULL
CREATE TABLE dbo.group_members (
  id BIGINT IDENTITY(1,1) PRIMARY KEY,
  group_id BIGINT NOT NULL,
  user_id BIGINT NOT NULL,
  group_nickname NVARCHAR(50),
  role NVARCHAR(20) NOT NULL,
  is_muted BIT NOT NULL DEFAULT 0,
  mute_end_time DATETIME2,
  joined_at DATETIME2 NOT NULL,
  updated_at DATETIME2,
  CONSTRAINT uniq_group_user UNIQUE (group_id, user_id)
);
GO
CREATE INDEX idx_group_members_group ON dbo.group_members(group_id);
CREATE INDEX idx_group_members_user ON dbo.group_members(user_id);
GO

IF OBJECT_ID('dbo.group_messages', 'U') IS NULL
CREATE TABLE dbo.group_messages (
  id BIGINT IDENTITY(1,1) PRIMARY KEY,
  group_id BIGINT NOT NULL,
  sender_id BIGINT NOT NULL,
  content NVARCHAR(MAX),
  type NVARCHAR(20) NOT NULL,
  file_path NVARCHAR(255),
  file_name NVARCHAR(100),
  file_size BIGINT,
  created_at DATETIME2 NOT NULL,
  updated_at DATETIME2
);
GO
CREATE INDEX idx_group_messages_group ON dbo.group_messages(group_id);
CREATE INDEX idx_group_messages_sender ON dbo.group_messages(sender_id);
GO

IF OBJECT_ID('dbo.friendships', 'U') IS NULL
CREATE TABLE dbo.friendships (
  id BIGINT IDENTITY(1,1) PRIMARY KEY,
  user_id BIGINT NOT NULL,
  friend_id BIGINT NOT NULL,
  status NVARCHAR(20) NOT NULL,
  created_at DATETIME2 NOT NULL,
  updated_at DATETIME2,
  accepted_at DATETIME2,
  CONSTRAINT uniq_user_friend UNIQUE (user_id, friend_id)
);
GO
CREATE INDEX idx_friendships_user ON dbo.friendships(user_id);
CREATE INDEX idx_friendships_friend ON dbo.friendships(friend_id);
GO

ALTER TABLE dbo.messages ADD CONSTRAINT fk_messages_sender FOREIGN KEY (sender_id) REFERENCES dbo.users(id);
ALTER TABLE dbo.messages ADD CONSTRAINT fk_messages_receiver FOREIGN KEY (receiver_id) REFERENCES dbo.users(id);
ALTER TABLE dbo.chat_groups ADD CONSTRAINT fk_groups_creator FOREIGN KEY (creator_id) REFERENCES dbo.users(id);
ALTER TABLE dbo.group_members ADD CONSTRAINT fk_group_members_group FOREIGN KEY (group_id) REFERENCES dbo.chat_groups(id);
ALTER TABLE dbo.group_members ADD CONSTRAINT fk_group_members_user FOREIGN KEY (user_id) REFERENCES dbo.users(id);
ALTER TABLE dbo.group_messages ADD CONSTRAINT fk_group_messages_group FOREIGN KEY (group_id) REFERENCES dbo.chat_groups(id);
ALTER TABLE dbo.group_messages ADD CONSTRAINT fk_group_messages_sender FOREIGN KEY (sender_id) REFERENCES dbo.users(id);
ALTER TABLE dbo.friendships ADD CONSTRAINT fk_friendships_user FOREIGN KEY (user_id) REFERENCES dbo.users(id);
ALTER TABLE dbo.friendships ADD CONSTRAINT fk_friendships_friend FOREIGN KEY (friend_id) REFERENCES dbo.users(id);
GO

